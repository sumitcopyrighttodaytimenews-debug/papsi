package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryLight

@Composable
fun Avatar(name: String, modifier: Modifier = Modifier) {
    val initial = if (name.isNotEmpty()) name.first().uppercase() else "?"
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PrimaryLight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Primary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}
