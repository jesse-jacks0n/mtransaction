package com.project.mpesatracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    onPermissionGranted: () -> Unit
) {
    val smsPermissionState = rememberPermissionState(
        android.Manifest.permission.READ_SMS
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to M-Pesa Tracker",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        
        Text(
            text = "To track your M-Pesa transactions, we need permission to read your SMS messages. " +
                "We only read messages from M-Pesa to provide you with transaction insights.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 30.dp)
        )
        
        Button(
            onClick = {
                if (smsPermissionState.status.isGranted) {
                    onPermissionGranted()
                } else {
                    smsPermissionState.launchPermissionRequest()
                }
            },
            modifier = Modifier.width( intrinsicSize = IntrinsicSize.Max)
        ) {
            Text(
                text = if (smsPermissionState.status.isGranted) 
                    "Continue" 
                else 
                    "Grant Permission"
            )
        }
    }
} 