package com.example.mychatapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mychatapp.firebase.FirebaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    onUserSelected: (String, String) -> Unit,
    onLogout: () -> Unit
) {
    var users by remember { mutableStateOf(listOf<Pair<String, String>>()) } // (name, email)

    LaunchedEffect(Unit) {
        FirebaseHelper.getAllUsers { allUsers ->
            val currentUser = FirebaseHelper.getCurrentUser()
            users = allUsers.filter { it.second != currentUser }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(users) { (name, email) ->
                ListItem(
                    headlineContent = { Text(name) },
                    supportingContent = { Text(email) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUserSelected(name, email) }
                        .padding(8.dp)
                )
                Divider()
            }
        }
    }
}
