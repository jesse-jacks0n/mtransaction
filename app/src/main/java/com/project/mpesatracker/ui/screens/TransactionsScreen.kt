package com.project.mpesatracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.project.mpesatracker.ui.components.TransactionItem
import com.project.mpesatracker.viewmodel.TransactionsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = koinViewModel(),
    onTransactionClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search transactions") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
        }
    }
} 