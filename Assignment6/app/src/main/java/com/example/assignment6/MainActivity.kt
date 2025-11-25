package com.example.assignment6

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment6.ui.theme.Assignment6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment6Theme {
                AssignmentHome()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentHome() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = "Assignment 6 Demos") })
        }
    ) { innerPadding ->
        QuestionDashboard(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun QuestionDashboard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val questions = remember {
        listOf(
            DemoCard(
                title = "Q1 Altimeter",
                description = "Pressure sensor + simulated changes with live altimeter UI.",
                activity = Q1Activity::class.java
            ),
            DemoCard(
                title = "Q2 Compass & Level",
                description = "Magnetometer + accelerometer heading and gyroscope digital level.",
                activity = Q2Activity::class.java
            ),
            DemoCard(
                title = "Q3 Sound Meter",
                description = "AudioRecord based dB monitor with visual meter.",
                activity = Q3Activity::class.java
            ),
            DemoCard(
                title = "Q4 Gyro Ball Game",
                description = "Gyroscope controlled maze with collisions and obstacles.",
                activity = Q4Activity::class.java
            ),
            DemoCard(
                title = "Q5 Location & Maps",
                description = "Permission-gated Google Map with address + custom markers.",
                activity = Q5Activity::class.java
            ),
            DemoCard(
                title = "Q6 Trails Overlay",
                description = "Polyline + polygon overlays with runtime styling and clicks.",
                activity = Q6Activity::class.java
            )
        )
    }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(questions) { demo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(context, demo.activity))
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = demo.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = demo.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

data class DemoCard(
    val title: String,
    val description: String,
    val activity: Class<out ComponentActivity>
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    Assignment6Theme {
        AssignmentHome()
    }
}