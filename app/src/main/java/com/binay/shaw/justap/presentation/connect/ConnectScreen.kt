package com.binay.shaw.justap.presentation.connect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.binay.shaw.justap.presentation.themes.medium20

@Composable
fun ConnectScreen(
    modifier: Modifier = Modifier

) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Text(text = "This is connect screen", style = medium20.copy(color = MaterialTheme.colorScheme.onPrimaryContainer))
    }
}