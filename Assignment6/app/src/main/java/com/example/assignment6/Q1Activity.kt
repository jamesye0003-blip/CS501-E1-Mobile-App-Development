package com.example.assignment6

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.assignment6.ui.theme.Assignment6Theme
import kotlin.math.pow
import kotlin.random.Random
import kotlinx.coroutines.delay

class Q1Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment6Theme {
                AltimeterScreen()
            }
        }
    }
}

private const val SEA_LEVEL_PRESSURE = 1013.25f

@Composable
fun AltimeterScreen() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val pressureSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) }

    var pressure by remember { mutableFloatStateOf(SEA_LEVEL_PRESSURE) }
    var simulatedPressure by remember { mutableFloatStateOf(950f) }
    var useSimulation by remember { mutableStateOf(pressureSensor == null) }
    var autoSimulation by remember { mutableStateOf(true) }

    val altitude = remember(pressure) { pressureToAltitude(pressure) }
    val backgroundColor by animateColorAsState(
        targetValue = altitudeColor(altitude),
        label = "altimeterBackground"
    )

    DisposableEffect(useSimulation, pressureSensor) {
        if (useSimulation || pressureSensor == null) {
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    val value = event?.values?.firstOrNull() ?: return
                    pressure = value
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            sensorManager.registerListener(
                listener,
                pressureSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
            onDispose { sensorManager.unregisterListener(listener) }
        }
    }

    LaunchedEffect(useSimulation, simulatedPressure) {
        if (useSimulation) {
            pressure = simulatedPressure
        }
    }

    LaunchedEffect(useSimulation, autoSimulation) {
        if (!useSimulation || !autoSimulation) return@LaunchedEffect
        while (true) {
            simulatedPressure = (simulatedPressure + Random.nextFloat() * 4f - 2f)
                .coerceIn(650f, SEA_LEVEL_PRESSURE)
            delay(600)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Digital Altimeter",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${altitude.toInt()} m",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pressure: ${"%.2f".format(pressure)} hPa",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (useSimulation) {
                            "Simulation active"
                        } else {
                            "Live sensor feed"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Enable simulation", style = MaterialTheme.typography.titleMedium)
                        Switch(
                            checked = useSimulation,
                            onCheckedChange = { useSimulation = it || pressureSensor == null }
                        )
                    }
                    if (useSimulation) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Auto fluctuate", style = MaterialTheme.typography.titleSmall)
                            Switch(
                                checked = autoSimulation,
                                onCheckedChange = { autoSimulation = it }
                            )
                        }
                        Text(
                            text = "Simulated pressure: ${"%.1f".format(simulatedPressure)} hPa",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Slider(
                            value = simulatedPressure,
                            onValueChange = { simulatedPressure = it },
                            valueRange = 650f..SEA_LEVEL_PRESSURE,
                            steps = 10
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Higher altitude = darker background for quick visual cues.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun pressureToAltitude(pressure: Float): Float {
    val ratio = (pressure / SEA_LEVEL_PRESSURE).coerceAtMost(1.5f)
    val exponent = 1.0 / 5.255
    return (44330.0 * (1.0 - ratio.toDouble().pow(exponent))).toFloat()
}

private fun altitudeColor(altitudeMeters: Float): Color {
    val normalized = (altitudeMeters / 5000f).coerceIn(0f, 1f)
    val lightness = 0.9f - normalized * 0.5f
    return Color.hsl(
        hue = 210f,
        saturation = 0.35f + normalized * 0.25f,
        lightness = lightness
    )
}

