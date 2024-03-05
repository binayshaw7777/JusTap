package com.binay.shaw.justap.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.binay.shaw.justap.presentation.themes.medium20

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier

) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Text(text = "This is history screen", style = medium20.copy(color = MaterialTheme.colorScheme.onPrimaryContainer))
    }
}