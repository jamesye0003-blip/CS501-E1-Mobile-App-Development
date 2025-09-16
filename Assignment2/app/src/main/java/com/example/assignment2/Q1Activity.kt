package com.example.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment2.ui.theme.Assignment2Theme

class Q1Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment2Theme {
                ColorCardScreen()
            }
        }
    }
}

@Composable
fun ColorCard(color: Color, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(200.dp, 100.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ColorCardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Color Cards Demo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        
        // First card: Red with padding and background
        ColorCard(
            color = Color.Red,
            label = "Red Card",
            modifier = Modifier
                .padding(8.dp)
                .background(Color.Gray)
        )
        
        // Second card: Blue with border and size
        ColorCard(
            color = Color.Blue,
            label = "Blue Card",
            modifier = Modifier
                .size(250.dp, 120.dp)
                .border(4.dp, Color.Black)
        )
        
        // Third card: Green with padding, background, and border
        ColorCard(
            color = Color.Green,
            label = "Green Card",
            modifier = Modifier
                .padding(12.dp)
                .background(Color.LightGray)
                .border(2.dp, Color.Black)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ColorCardPreview() {
    Assignment2Theme {
        ColorCardScreen()
    }
}
