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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.assignment6.ui.theme.Assignment6Theme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Q2Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment6Theme {
                CompassAndLevelScreen()
            }
        }
    }
}

@Composable
fun CompassAndLevelScreen() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    var heading by remember { mutableFloatStateOf(0f) }
    var roll by remember { mutableFloatStateOf(0f) }
    var pitch by remember { mutableFloatStateOf(0f) }

    val accelerometerValues = remember { FloatArray(3) }
    val magnetometerValues = remember { FloatArray(3) }
    val rotationMatrix = remember { FloatArray(9) }
    val orientationAngles = remember { FloatArray(3) }
    var lastGyroTimestamp by remember { mutableLongStateOf(0L) }
    var sensorStatus by remember { mutableStateOf("Waiting for sensor data…") }

    DisposableEffect(Unit) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelerometerValues[0] = event.values[0]
                        accelerometerValues[1] = event.values[1]
                        accelerometerValues[2] = event.values[2]
                        computeHeading(accelerometerValues, magnetometerValues, rotationMatrix, orientationAngles)?.let {
                            heading = it
                        }
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        magnetometerValues[0] = event.values[0]
                        magnetometerValues[1] = event.values[1]
                        magnetometerValues[2] = event.values[2]
                        computeHeading(accelerometerValues, magnetometerValues, rotationMatrix, orientationAngles)?.let {
                            heading = it
                        }
                    }

                    Sensor.TYPE_GYROSCOPE -> {
                        val timestamp = event.timestamp
                        if (lastGyroTimestamp != 0L) {
                            val dt = (timestamp - lastGyroTimestamp) / 1_000_000_000f
                            roll = (roll + event.values[0] * dt * RAD_TO_DEG).coerceIn(-90f, 90f)
                            pitch = (pitch + event.values[1] * dt * RAD_TO_DEG).coerceIn(-90f, 90f)
                        }
                        lastGyroTimestamp = timestamp
                    }
                }
                sensorStatus =
                    "Compass ${"%.1f".format(heading)}° | Roll ${"%.1f".format(roll)}° | Pitch ${"%.1f".format(pitch)}°"
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }
        magnetometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }
        gyroscope?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Compass & Digital Level",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = sensorStatus, style = MaterialTheme.typography.bodyMedium)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompassDial(heading = heading, diameter = 220.dp)
                Text(
                    text = "${heading.toInt()}°",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Needle powered by accelerometer + magnetometer fusion.")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Digital Level (Gyroscope)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                LevelGauge(label = "Roll", value = roll)
                LevelGauge(label = "Pitch", value = pitch)
                Text(
                    text = "Values stem from gyroscope integration for smooth tilt feedback.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun CompassDial(heading: Float, diameter: Dp) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(diameter)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            drawCircle(
                color = colorScheme.onSurface.copy(alpha = 0.08f),
                radius = radius,
                style = Stroke(width = 6f)
            )
            drawCircle(
                color = colorScheme.primary.copy(alpha = 0.12f),
                radius = radius * 0.78f
            )
            repeat(12) { index ->
                val tickAngle = index * 30f * DEG_TO_RAD
                val start = Offset(
                    x = center.x + (radius - 24f) * cos(tickAngle),
                    y = center.y + (radius - 24f) * sin(tickAngle)
                )
                val end = Offset(
                    x = center.x + radius * cos(tickAngle),
                    y = center.y + radius * sin(tickAngle)
                )
                drawLine(
                    color = colorScheme.onSurface.copy(alpha = 0.5f),
                    start = start,
                    end = end,
                    strokeWidth = if (index % 3 == 0) 6f else 3f
                )
            }

            val angleRad = (heading - 90f) * DEG_TO_RAD
            val needleLength = radius * 0.9f
            val tip = Offset(
                x = center.x + needleLength * cos(angleRad),
                y = center.y + needleLength * sin(angleRad)
            )

            drawLine(
                color = colorScheme.primary,
                start = center,
                end = tip,
                strokeWidth = 10f
            )
            drawLine(
                color = colorScheme.error,
                start = center,
                end = center - (tip - center) * 0.5f,
                strokeWidth = 6f
            )
            drawCircle(
                color = colorScheme.onSurface,
                radius = 14f,
                center = center
            )
        }
    }
}

@Composable
private fun LevelGauge(label: String, value: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontWeight = FontWeight.Medium)
            Text(text = "${"%.1f".format(value)}°")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
        ) {
            val progress = ((value + 90f) / 180f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(12.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
            )
        }
    }
}

private const val RAD_TO_DEG = (180f / Math.PI).toFloat()
private const val DEG_TO_RAD = (PI / 180f).toFloat()

private fun computeHeading(
    accel: FloatArray,
    magnet: FloatArray,
    rotationMatrix: FloatArray,
    orientationAngles: FloatArray
): Float? {
    val success = SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)
    if (!success) return null
    SensorManager.getOrientation(rotationMatrix, orientationAngles)
    val azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble())
    return ((azimuthDegrees + 360) % 360).toFloat()
}

