package com.example.notesappwithsqlitedb

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val date: String
)
