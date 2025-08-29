package com.example.mychatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.mychatapp.firebase.FirebaseHelper
import com.example.mychatapp.screens.*
import com.example.mychatapp.ui.theme.MyChatAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyChatAppTheme {
                ChatAppRoot()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseHelper.setUserOnline(true)
    }

    override fun onStop() {
        super.onStop()
        FirebaseHelper.setUserOnline(false)
    }
}

@Composable
fun ChatAppRoot() {
    var isLoggedIn by remember { mutableStateOf(FirebaseHelper.getCurrentUser() != null) }
    var selectedUser by remember { mutableStateOf<Pair<String, String>?>(null) } // (name, email)

    when {
        !isLoggedIn -> AuthScreen(onLoginSuccess = { isLoggedIn = true })
        selectedUser == null -> UserListScreen(
            onUserSelected = { name, email -> selectedUser = name to email },
            onLogout = {
                FirebaseHelper.signOut()
                isLoggedIn = false
            }
        )
        else -> ChatScreen(
            partnerName = selectedUser!!.first,
            partnerEmail = selectedUser!!.second,
            onBack = { selectedUser = null }
        )
    }
}
