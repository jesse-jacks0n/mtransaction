package com.project.mpesatracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.mpesatracker.data.model.Transaction
import com.project.mpesatracker.data.model.TransactionType
import java.text.NumberFormat
import java.util.*

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.senderReceiver,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.timestamp.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = formatAmount(transaction.amount),
                style = MaterialTheme.typography.bodyLarge,
                color = when (transaction.type) {
                    TransactionType.SEND -> MaterialTheme.colorScheme.error
                    TransactionType.RECEIVE -> MaterialTheme.colorScheme.primary
                    TransactionType.WITHDRAW -> MaterialTheme.colorScheme.error
                    TransactionType.DEPOSIT -> MaterialTheme.colorScheme.primary
                    TransactionType.PAYBILL -> MaterialTheme.colorScheme.error
                    TransactionType.BUY_GOODS -> MaterialTheme.colorScheme.error
                    TransactionType.MSHWARI_TO -> MaterialTheme.colorScheme.error
                    TransactionType.MSHWARI_FROM -> MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

private fun formatAmount(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
    format.currency = Currency.getInstance("KES")
    return format.format(amount)
}
//preview this item
