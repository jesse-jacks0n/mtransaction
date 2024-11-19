package com.project.mpesatracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.project.mpesatracker.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigate: (String) -> Unit
) {
    LaunchedEffect(key1 = true) {
        delay(2000) // 2 seconds delay
        onNavigate("permissions")
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
} 