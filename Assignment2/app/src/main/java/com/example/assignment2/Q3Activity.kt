package com.example.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment2.ui.theme.Assignment2Theme

class Q3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KotlinPracticeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun KotlinPracticeScreen(modifier: Modifier = Modifier) {
    var inputAnimal by rememberSaveable { mutableStateOf("cat") }
    var nullableString: String? by rememberSaveable { mutableStateOf("Hello Kotlin!") }
    var counter by rememberSaveable { mutableStateOf(0) }
    
    // Kotlin when expression based on input string
    val animalMessage = when (inputAnimal.lowercase()) {
        "cat" -> "🐱 Meow! Cats are independent and curious."
        "dog" -> "🐶 Woof! Dogs are loyal and friendly."
        "fish" -> "🐠 Glub! Fish are peaceful swimmers."
        else -> "🤔 Unknown animal. Try 'cat', 'dog', or 'fish'."
    }
    
    // Message only if nullable string is not null (using ?.let)
    val nullableMessage = nullableString?.let { 
        "Non-null message: $it" 
    } ?: "The string is null"
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Kotlin Practice Screen",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // When Expression Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "1. When Expression Demo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Blue
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = inputAnimal,
                    onValueChange = { inputAnimal = it },
                    label = { Text("Enter animal name") },
                    placeholder = { Text("cat, dog, or fish") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current animal: $inputAnimal",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = animalMessage,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        // Nullable String Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "2. Nullable String with ?.let",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Magenta
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Column {
                    OutlinedTextField(
                        value = nullableString ?: "",
                        onValueChange = { newValue ->
                            nullableString = if (newValue.isEmpty()) null else newValue
                        },
                        label = { Text("Enter text (empty = null)") },
                        placeholder = { Text("Type something...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text(
                        text = "Tip: Leave empty to set null",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = nullableMessage,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Counter Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "3. Counter (Max 5)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Green
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Current count: $counter",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { 
                            // Counter increments only if value is below 5
                            if (counter < 5) {
                                counter++
                            }
                        },
                        enabled = counter < 5,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Increment")
                    }
                    
                    Button(
                        onClick = { counter = 0 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text("Reset")
                    }
                }
                
                if (counter >= 5) {
                    Text(
                        text = "Maximum reached! Counter cannot go above 5.",
                        color = Color.Red,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KotlinPracticeScreenPreview() {
    Assignment2Theme {
        KotlinPracticeScreen()
    }
}
