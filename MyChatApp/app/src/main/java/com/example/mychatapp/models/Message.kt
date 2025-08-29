package com.example.mychatapp.models

data class Message(
    val sender: String? = null,
    val text: String? = null,
    val timestamp: Long? = System.currentTimeMillis()
)
