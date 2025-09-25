package com.example.assignment3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment3.ui.theme.Assignment3Theme
import kotlinx.coroutines.launch

class Q3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumnWithStickyHeaders(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Contact(
    val name: String,
    val phone: String,
    val initial: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyColumnWithStickyHeaders(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Generate sample contacts (50+ contacts)
    val contacts = remember {
        generateContacts()
    }
    
    // Group contacts by first letter
    val groupedContacts = remember(contacts) {
        contacts.groupBy { it.initial }
    }
    
    // Show FAB only when scrolled past item 10
    val showFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 10
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            groupedContacts.forEach { (letter, contactsInGroup) ->
                item(key = "header_$letter") {
                    LetterHeader(letter = letter)
                }
                
                items(contactsInGroup, key = { contact -> contact.name }) { contact ->
                    ContactItem(contact = contact)
                }
            }
        }
        
        // Floating Action Button
        if (showFab) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll to Top"
                )
            }
        }
    }
}

@Composable
fun LetterHeader(letter: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        Text(
            text = letter,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ContactItem(contact: Contact) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.initial,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = contact.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = contact.phone,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun generateContacts(): List<Contact> {
    val firstNames = listOf(
        "Alice", "Bob", "Charlie", "David", "Emma", "Frank", "Grace", "Henry",
        "Ivy", "Jack", "Kate", "Liam", "Mia", "Noah", "Olivia", "Paul",
        "Quinn", "Rachel", "Sam", "Tina", "Uma", "Victor", "Wendy", "Xavier",
        "Yara", "Zoe", "Adam", "Beth", "Chris", "Diana", "Ethan", "Fiona",
        "George", "Hannah", "Ian", "Julia", "Kevin", "Lisa", "Mike", "Nina",
        "Oscar", "Penny", "Quentin", "Rita", "Steve", "Tara", "Ulysses", "Vera"
    )
    
    val lastNames = listOf(
        "Anderson", "Brown", "Clark", "Davis", "Evans", "Foster", "Garcia", "Harris",
        "Johnson", "King", "Lee", "Miller", "Nelson", "O'Connor", "Parker", "Quinn",
        "Roberts", "Smith", "Taylor", "Upton", "Vargas", "Wilson", "Young", "Zhang"
    )
    
    return (1..60).map { index ->
        val firstName = firstNames.random()
        val lastName = lastNames.random()
        Contact(
            name = "$firstName $lastName",
            phone = "555-${(1000..9999).random()}",
            initial = firstName.first().toString()
        )
    }.sortedBy { it.name }
}

@Preview(showBackground = true)
@Composable
fun LazyColumnWithStickyHeadersPreview() {
    Assignment3Theme {
        LazyColumnWithStickyHeaders()
    }
}
