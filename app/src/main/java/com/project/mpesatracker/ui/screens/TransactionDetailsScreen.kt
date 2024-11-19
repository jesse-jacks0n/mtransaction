package com.project.mpesatracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.mpesatracker.data.model.Transaction
import com.project.mpesatracker.data.model.TransactionType
import com.project.mpesatracker.viewmodel.TransactionsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    transactionId: String,
    onNavigateBack: () -> Unit,
    viewModel: TransactionsViewModel = koinViewModel()
) {
    val transaction: Transaction? = remember(transactionId) {
        viewModel.getTransaction(transactionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            transaction?.let { tx ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = tx.type.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            Text(
                                text = formatAmount(tx.amount),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = getTransactionColor(tx.type)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            DetailRow("Date & Time", tx.timestamp.toString())
                            DetailRow("Recipient/Sender", tx.senderReceiver)
                            DetailRow("Reference", tx.reference)
                            DetailRow("Balance After", formatAmount(tx.balance))
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Original Message",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = tx.description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Transaction not found")
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatAmount(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
    format.currency = Currency.getInstance("KES")
    return format.format(amount)
}

@Composable
private fun getTransactionColor(type: TransactionType) = when (type) {
    TransactionType.SEND -> MaterialTheme.colorScheme.error
    TransactionType.RECEIVE -> MaterialTheme.colorScheme.primary
    TransactionType.WITHDRAW -> MaterialTheme.colorScheme.error
    TransactionType.DEPOSIT -> MaterialTheme.colorScheme.primary
    TransactionType.PAYBILL -> MaterialTheme.colorScheme.error
    TransactionType.BUY_GOODS -> MaterialTheme.colorScheme.error
    TransactionType.MSHWARI_TO -> MaterialTheme.colorScheme.error
    TransactionType.MSHWARI_FROM -> MaterialTheme.colorScheme.primary
} 