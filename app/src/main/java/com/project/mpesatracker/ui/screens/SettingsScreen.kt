package com.project.mpesatracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.mpesatracker.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    var showClearDataDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Appearance Section
        Text(
            text = "Appearance",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Mode")
                Switch(
                    checked = viewModel.isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Data Management Section
        Text(
            text = "Data Management",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
//        Button(
//            onClick = { showClearDataDialog = true },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.error
//            ),
//            modifier = Modifier.width( intrinsicSize = IntrinsicSize.Max)
//        ) {
//            Text("Clear All Data")
//        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Version
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
    
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Data") },
            text = { Text("Are you sure you want to clear all transaction data? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
//preview
@Preview
@Composable
fun SettingsScreenPreview() {

}