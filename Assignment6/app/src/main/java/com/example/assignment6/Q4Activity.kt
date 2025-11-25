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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.assignment6.ui.theme.Assignment6Theme
import kotlinx.coroutines.delay

class Q4Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment6Theme {
                GyroBallGameScreen()
            }
        }
    }
}

private data class FractionalObstacle(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    fun toRect(area: Size): Rect = Rect(
        left = left * area.width,
        top = top * area.height,
        right = right * area.width,
        bottom = bottom * area.height
    )
}

@Composable
fun GyroBallGameScreen() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val gyroscope = remember { sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) }

    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }
    var lastGyroTimestamp by remember { mutableLongStateOf(0L) }
    var goalReached by remember { mutableStateOf(false) }
    var boardInitialized by remember { mutableStateOf(false) }
    var boardSize by remember { mutableStateOf(Size.Zero) }
    var ballPosition by remember { mutableStateOf(Offset.Zero) }
    var velocity by remember { mutableStateOf(Offset.Zero) }

    DisposableEffect(gyroscope) {
        if (gyroscope == null) {
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val timestamp = event.timestamp
                    if (lastGyroTimestamp != 0L) {
                        val dt = (timestamp - lastGyroTimestamp) / 1_000_000_000f
                        tiltX = (tiltX + event.values[0] * dt * 180f / Math.PI.toFloat()).coerceIn(-90f, 90f)
                        tiltY = (tiltY + event.values[1] * dt * 180f / Math.PI.toFloat()).coerceIn(-90f, 90f)
                    }
                    lastGyroTimestamp = timestamp
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME)
            onDispose { sensorManager.unregisterListener(listener) }
        }
    }

    val obstacles = remember {
        listOf(
            FractionalObstacle(0.05f, 0.25f, 0.95f, 0.3f),
            FractionalObstacle(0.05f, 0.45f, 0.75f, 0.5f),
            FractionalObstacle(0.25f, 0.65f, 0.95f, 0.7f),
            FractionalObstacle(0.05f, 0.15f, 0.15f, 0.75f),
            FractionalObstacle(0.85f, 0.35f, 0.95f, 0.85f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Gyroscope Ball Maze",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tilt the phone left/right to roll the ball into the green goal at the top.",
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current
            val paddingPx = remember(density) { with(density) { 24.dp.toPx() } }
            val ballRadius = with(density) { 14.dp.toPx() }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { size ->
                        val boardWidth = size.width.toFloat() - paddingPx
                        val boardHeight = size.height.toFloat() - paddingPx
                        if (boardWidth > 0f && boardHeight > 0f) {
                            boardSize = Size(boardWidth, boardHeight)
                            if (!boardInitialized) {
                                ballPosition = Offset(boardWidth / 2f, boardHeight * 0.85f)
                                velocity = Offset.Zero
                                boardInitialized = true
                            }
                        }
                    }
            )

            val goalRect = remember(boardSize) {
                Rect(
                    left = boardSize.width * 0.35f,
                    top = boardSize.height * 0.05f,
                    right = boardSize.width * 0.65f,
                    bottom = boardSize.height * 0.12f
                )
            }

            LaunchedEffect(boardSize, boardInitialized) {
                if (boardSize.width == 0f || boardSize.height == 0f || !boardInitialized) return@LaunchedEffect
                var lastUpdate = System.currentTimeMillis()
                while (true) {
                    val now = System.currentTimeMillis()
                    val dt = (now - lastUpdate) / 1000f
                    lastUpdate = now

                    val accelX = (-tiltY / 60f).coerceIn(-1f, 1f) * 1600f
                    val accelY = (tiltX / 60f).coerceIn(-1f, 1f) * 1600f

                    velocity = Offset(
                        (velocity.x + accelX * dt) * 0.97f,
                        (velocity.y + accelY * dt) * 0.97f
                    )

                    var newPos = Offset(
                        (ballPosition.x + velocity.x * dt).coerceIn(ballRadius, boardSize.width - ballRadius),
                        (ballPosition.y + velocity.y * dt).coerceIn(ballRadius, boardSize.height - ballRadius)
                    )

                    val obstacleRects = obstacles.map { it.toRect(boardSize) }
                    for (rect in obstacleRects) {
                        if (rect.contains(newPos)) {
                            newPos = ballPosition
                            velocity = Offset(-velocity.x * 0.6f, -velocity.y * 0.6f)
                        }
                    }

                    ballPosition = newPos
                    if (goalRect.contains(newPos)) {
                        goalReached = true
                    }
                    delay(16L)
                }
            }

            BallGameCanvas(
                ball = ballPosition,
                ballRadius = ballRadius,
                boardSize = boardSize,
                obstacles = obstacles,
                goalRect = goalRect
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Tilt now: X=${"%.1f".format(tiltX)}° Y=${"%.1f".format(tiltY)}°")
                Text(
                    text = if (goalReached) "Goal reached!" else "Goal: roll into the green zone",
                    color = if (goalReached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = {
                        goalReached = false
                        boardInitialized = false
                        ballPosition = Offset.Zero
                        velocity = Offset.Zero
                    }
                ) {
                    Text("Reset game")
                }
            }
        }
    }
}

@Composable
private fun BallGameCanvas(
    ball: Offset,
    ballRadius: Float,
    boardSize: Size,
    obstacles: List<FractionalObstacle>,
    goalRect: Rect
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101828), RoundedCornerShape(20.dp))
    ) {
        drawRoundRect(
            color = Color(0xFF1C2536),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(32f, 32f),
            style = Stroke(width = 4f)
        )

        drawRect(
            color = Color(0xFF1ABC9C),
            topLeft = goalRect.topLeft,
            size = goalRect.size
        )

        val board = boardSize
        obstacles.forEach { obstacle ->
            val rect = obstacle.toRect(board)
            drawRect(
                color = Color(0xFF34495E),
                topLeft = rect.topLeft,
                size = rect.size
            )
        }

        drawCircle(
            color = Color(0xFFFFC857),
            radius = ballRadius,
            center = ball
        )
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            radius = ballRadius,
            center = ball + Offset(6f, 6f)
        )
    }
}

