package com.project.mpesatracker.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.mpesatracker.ui.components.TransactionSummaryCard
import com.project.mpesatracker.ui.components.TransactionChart
import com.project.mpesatracker.ui.components.TransactionItem
import com.project.mpesatracker.viewmodel.DashboardViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import com.project.mpesatracker.viewmodel.MonthYear
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.material.icons.outlined.Download
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onTransactionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    // Create launcher for document creation
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        uri?.let { viewModel.savePdfToUri(it) }
    }

    // Error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.exportError) {
        uiState.exportError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    IconButton(
                        onClick = { 
                            val timestamp = System.currentTimeMillis()
                            val defaultFileName = "mpesa_transactions_$timestamp.pdf"
                            createDocumentLauncher.launch(defaultFileName)
                            viewModel.generatePdf()
                        },
                        enabled = !uiState.isExporting
                    ) {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Download,
                                contentDescription = "Export to PDF"
                            )
                        }
                    }
                }
            }

            item {
                MonthSelector(
                    availableMonths = uiState.availableMonths,
                    selectedMonth = uiState.selectedMonth,
                    onMonthSelected = { viewModel.selectMonth(it) }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TransactionSummaryCard(
                        title = "Income",
                        amount = uiState.totalIncome,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    TransactionSummaryCard(
                        title = "Expenses",
                        amount = uiState.totalExpenses,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                TransactionChart(
                    income = uiState.totalIncome,
                    expenses = uiState.totalExpenses,

                )
            }
            item {
                Text(
                    text = "Recent transactions",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Use items() with key parameter for the transactions list
            items(
                items = uiState.recentTransactions,
                key = { transaction -> transaction.id }
            ) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun MonthSelector(
    availableMonths: List<MonthYear>,
    selectedMonth: MonthYear?,
    onMonthSelected: (MonthYear?) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // All Time filter
        FilterChip(
            selected = selectedMonth == null,
            onClick = { onMonthSelected(null) },
            label = { Text("All Time") }
        )
        
        // Month filters
        availableMonths.forEach { monthYear ->
            FilterChip(
                selected = selectedMonth == monthYear,
                onClick = { onMonthSelected(monthYear) },
                label = { Text(monthYear.toString()) }
            )
        }
    }
} 