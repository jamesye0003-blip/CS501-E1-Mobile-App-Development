package com.example.assignment1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.assignment1.ui.theme.Assignment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserInfo(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun UserInfo(modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("Yuwei Ye - yeyuwei@bu.edu") }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = message,
            modifier = modifier
        )

        Button(
            onClick = {
                message = if (message == "Yuwei Ye - yeyuwei@bu.edu") {
                    "Button clicked! Message changed successfully!"
                } else {
                    "Yuwei Ye - yeyuwei@bu.edu"
                }
            }
        ) {
            Text("Change Message")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserInfoPreview() {
    Assignment1Theme {
        UserInfo()
    }
}

