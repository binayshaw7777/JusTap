package com.binay.shaw.justap.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import com.binay.shaw.justap.presentation.themes.medium20


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxSize().background(White), verticalArrangement = Arrangement.Center) {
        Text(text = "This is home screen", style = medium20.copy(color = Color.Black))
    }
}