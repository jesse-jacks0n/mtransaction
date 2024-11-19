package com.project.mpesatracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mpesatracker.data.model.Transaction
import com.project.mpesatracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getTransactions().collect { transactionsList ->
                _transactions.value = transactionsList
            }
        }
    }
    
    fun getTransaction(id: String): Transaction? {
        return _transactions.value.find { it.id == id }
    }
} 