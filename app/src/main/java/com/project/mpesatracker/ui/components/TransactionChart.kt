package com.project.mpesatracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf


@Composable
fun TransactionChart(
    income: Double,
    expenses: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Transaction Overview",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Chart(
                chart = lineChart(),
                model = entryModelOf(income, expenses),
                startAxis = startAxis(),
                bottomAxis = bottomAxis(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}
