package com.project.mpesatracker.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.project.mpesatracker.data.repository.DataStoreRepository
import com.project.mpesatracker.data.repository.TransactionRepository
import com.project.mpesatracker.util.MpesaSmsParser
import com.project.mpesatracker.viewmodel.DashboardViewModel
import com.project.mpesatracker.viewmodel.SettingsViewModel
import com.project.mpesatracker.viewmodel.TransactionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@RequiresApi(Build.VERSION_CODES.O)
val appModule = module {
    // Repositories
    single { DataStoreRepository(get()) }
    single { MpesaSmsParser() }
    single { TransactionRepository(get(), get()) }
    
    // ViewModels
    viewModel { TransactionsViewModel(get()) }
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
} 