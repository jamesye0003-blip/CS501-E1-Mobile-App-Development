package com.example.assignment6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.assignment6.ui.theme.Assignment6Theme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

class Q6Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment6Theme {
                TrailOverlayScreen()
            }
        }
    }
}

@Composable
fun TrailOverlayScreen() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(TRAIL_POINTS.first(), 13.5f)
    }
    var polylineColor by remember { mutableStateOf(Color(0xFF4CAF50)) }
    var polylineWidth by remember { mutableStateOf(12f) }
    var polygonColor by remember { mutableStateOf(Color(0xFF3F51B5)) }
    var polygonStroke by remember { mutableStateOf(5f) }
    var infoText by remember { mutableStateOf("Tap a path or area to see more info") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Trail & Polygon Overlays",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Showcase customizable polylines, polygons, and click feedback with Maps Compose.",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Polyline(
                    points = TRAIL_POINTS,
                    color = polylineColor,
                    width = polylineWidth,
                    clickable = true,
                    onClick = { infoText = "Trail: lake loop, 3.2 km out & back" }
                )

                Polygon(
                    points = PARK_BOUNDARY,
                    fillColor = polygonColor.copy(alpha = 0.25f),
                    strokeColor = polygonColor,
                    strokeWidth = polygonStroke,
                    clickable = true,
                    onClick = { infoText = "Park area: camping and picnic friendly" }
                )

                Marker(
                    state = MarkerState(position = TRAIL_POINTS.first()),
                    title = "Trailhead"
                )
                Marker(
                    state = MarkerState(position = TRAIL_POINTS.last()),
                    title = "Scenic Overlook"
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = infoText, fontWeight = FontWeight.Medium)

                Text(text = "Trail color")
                ColorSelector(
                    selected = polylineColor,
                    onColorSelected = { polylineColor = it }
                )
                Text(text = "Trail width: ${polylineWidth.toInt()} px")
                Slider(
                    value = polylineWidth,
                    onValueChange = { polylineWidth = it },
                    valueRange = 4f..20f
                )

                Text(text = "Area color")
                ColorSelector(
                    selected = polygonColor,
                    onColorSelected = { polygonColor = it }
                )
                Text(text = "Area stroke: ${polygonStroke.toInt()} px")
                Slider(
                    value = polygonStroke,
                    onValueChange = { polygonStroke = it },
                    valueRange = 2f..12f
                )
            }
        }
    }
}

@Composable
private fun ColorSelector(
    selected: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFFEF5350),
        Color(0xFF29B6F6),
        Color(0xFFFFC107),
        Color(0xFF8E24AA)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(color, RoundedCornerShape(12.dp))
                    .border(
                        width = if (color == selected) 3.dp else 0.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

private val TRAIL_POINTS = listOf(
    LatLng(37.7459, -119.5332),
    LatLng(37.7475, -119.5296),
    LatLng(37.7498, -119.5257),
    LatLng(37.7524, -119.5231),
    LatLng(37.7542, -119.5265),
    LatLng(37.7520, -119.5308)
)

private val PARK_BOUNDARY = listOf(
    LatLng(37.7538, -119.5380),
    LatLng(37.7580, -119.5325),
    LatLng(37.7565, -119.5215),
    LatLng(37.7490, -119.5185),
    LatLng(37.7460, -119.5300)
)

