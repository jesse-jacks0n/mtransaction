package com.project.mpesatracker.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mpesatracker.data.model.Transaction
import com.project.mpesatracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.number
import android.content.Context
import com.project.mpesatracker.util.PdfGenerator
import kotlinx.coroutines.flow.first
import java.io.File
import android.os.Environment
import android.net.Uri

data class DashboardUiState(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedMonth: MonthYear? = null,
    val availableMonths: List<MonthYear> = emptyList(),
    val pdfFile: File? = null,
    val isExporting: Boolean = false,
    val exportError: String? = null
)

data class MonthYear(
    val month: Month,
    val year: Int
) {
    override fun toString(): String = "${month.name.lowercase().capitalize()} $year"
}

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    private val repository: TransactionRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val pdfGenerator = PdfGenerator(context)

    private var generatedPdfContent: ByteArray? = null

    init {
        loadTransactions()
        generateAvailableMonths()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateAvailableMonths() {
        val current = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val months = mutableListOf<MonthYear>()
        
        // Add current month first
        var currentMonthYear = MonthYear(current.month, current.year)
        months.add(currentMonthYear)
        
        // Add past 11 months
        repeat(11) {
            currentMonthYear = if (currentMonthYear.month == Month.JANUARY) {
                MonthYear(Month.DECEMBER, currentMonthYear.year - 1)
            } else {
                MonthYear(Month.of(currentMonthYear.month.number - 1), currentMonthYear.year)
            }
            months.add(currentMonthYear)
        }
        
        _uiState.update { it.copy(availableMonths = months) }
    }

    fun selectMonth(monthYear: MonthYear?) {
        _uiState.update { it.copy(selectedMonth = monthYear) }
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            repository.getTransactions().collect { transactions ->
                val filteredTransactions = _uiState.value.selectedMonth?.let { selected ->
                    transactions.filter { transaction ->
                        transaction.timestamp.month == selected.month &&
                        transaction.timestamp.year == selected.year
                    }
                } ?: transactions

                val income = filteredTransactions.filter { it.amount > 0 }.sumOf { it.amount }
                val expenses = filteredTransactions.filter { it.amount < 0 }.sumOf { it.amount.absoluteValue }

                _uiState.update { currentState ->
                    currentState.copy(
                        totalIncome = income,
                        totalExpenses = expenses,
                        recentTransactions = filteredTransactions.take(5)
                    )
                }
            }
        }
    }

    fun generatePdf() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isExporting = true, exportError = null) }
                
                val transactions = _uiState.value.selectedMonth?.let { selected ->
                    repository.getTransactions().first().filter { transaction ->
                        transaction.timestamp.month == selected.month &&
                        transaction.timestamp.year == selected.year
                    }
                } ?: repository.getTransactions().first()

                val period = _uiState.value.selectedMonth?.toString() ?: "All Time"
                
                generatedPdfContent = pdfGenerator.generateTransactionReport(
                    transactions = transactions,
                    totalIncome = _uiState.value.totalIncome,
                    totalExpenses = _uiState.value.totalExpenses,
                    period = period
                )
                
                _uiState.update { it.copy(isExporting = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isExporting = false,
                        exportError = "Failed to generate PDF: ${e.message}"
                    )
                }
            }
        }
    }

    fun savePdfToUri(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isExporting = true) }
                
                generatedPdfContent?.let { content ->
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(content)
                    }
                }
                
                _uiState.update { it.copy(isExporting = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isExporting = false,
                        exportError = "Failed to save PDF: ${e.message}"
                    )
                }
            }
        }
    }
} 