package com.example.newsheadlines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen() {
    var isSplashVisible by remember { mutableStateOf(true) }
    if (isSplashVisible) {
        SplashScreen(onTimeout = { isSplashVisible = false })
    } else {
        NewsHeadlinesScreen(viewModel = viewModel())
    }
}
