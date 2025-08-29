package com.example.mychatapp.screens

import android.text.format.DateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mychatapp.firebase.FirebaseHelper
import com.example.mychatapp.models.Message
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    partnerName: String,
    partnerEmail: String,
    onBack: () -> Unit
) {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var input by remember { mutableStateOf("") }
    var isOnline by remember { mutableStateOf(false) }
    var lastSeen by remember { mutableLongStateOf(0L) }

    // Listen for conversation
    LaunchedEffect(partnerEmail) {
        FirebaseHelper.listenConversation(partnerEmail) { allMsgs ->
            messages = allMsgs
        }
    }

    // Listen status
    LaunchedEffect(partnerEmail) {
        FirebaseHelper.listenUserStatus(partnerEmail) { online, seen ->
            isOnline = online
            lastSeen = seen
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = partnerName)
                        if (isOnline) {
                            Text("Online", color = Color.Green, style = MaterialTheme.typography.bodySmall)
                        } else if (lastSeen > 0) {
                            Text(
                                "Last seen: ${DateFormat.format("hh:mm a", Date(lastSeen))}",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(messages) { msg ->
                    val isMe = msg.sender == FirebaseHelper.getCurrentUser()
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                    ) {
                        Surface(
                            color = if (isMe) Color(0xFFDCF8C6) else Color.White,
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(msg.text.orEmpty())
                                Text(
                                    text = DateFormat.format(
                                        "hh:mm a",
                                        Date(msg.timestamp ?: 0L)
                                    ).toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") }
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (input.isNotBlank()) {
                        FirebaseHelper.sendMessage(partnerEmail, input)
                        input = ""
                    }
                }) {
                    Text("Send")
                }
            }
        }
    }
}
