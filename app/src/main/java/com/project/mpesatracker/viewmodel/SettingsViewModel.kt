package com.project.mpesatracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mpesatracker.data.repository.DataStoreRepository
import com.project.mpesatracker.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStore: DataStoreRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    
    var isDarkMode by mutableStateOf(false)
        private set
        
    init {
        viewModelScope.launch {
            dataStore.isDarkMode.collect { darkMode ->
                isDarkMode = darkMode
            }
        }
    }
    
    fun toggleDarkMode() {
        viewModelScope.launch {
            dataStore.setDarkMode(!isDarkMode)
        }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            transactionRepository.clearAllTransactions()
        }
    }
} 