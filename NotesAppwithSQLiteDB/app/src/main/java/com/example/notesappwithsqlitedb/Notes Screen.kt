package com.example.notesappwithsqlitedb

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(viewModel: NotesViewModel) {
    var newNoteText by remember { mutableStateOf("") } // single input

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
        // Single OutlinedTextField for title + content
        OutlinedTextField(
            value = newNoteText,
            onValueChange = { newNoteText = it },
            placeholder = { Text("Enter title and note content (title first line)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp), // taller for multi-line input
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newNoteText.isNotBlank()) {
                    val lines = newNoteText.lines()
                    val title = lines.firstOrNull() ?: "Untitled"
                    val content = if (lines.size > 1) lines.drop(1).joinToString("\n") else ""
                    val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
                    viewModel.addNote(title = title, content = content, date = date)
                    newNoteText = ""
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(viewModel.notes, key = { it.id }) { note ->
                NoteItem(
                    note = note,
                    onUpdate = { updatedNote -> viewModel.updateNote(updatedNote) },
                    onDelete = { id -> viewModel.deleteNote(id) }
                )
            }
        }
    }
}


@Composable
fun NoteItem(note: Note, onUpdate: (Note) -> Unit, onDelete: (Long) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var updatedTitle by remember { mutableStateOf(note.title) }
    var updatedContent by remember { mutableStateOf(note.content) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(note.title, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            text = {
                Column(modifier = Modifier.heightIn(min = 200.dp, max = 400.dp)) {
                    Text(note.date ?: "", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(note.content, color = Color.DarkGray)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { showDialog = true },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8DC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (isEditing) {
                OutlinedTextField(
                    value = updatedTitle,
                    onValueChange = { updatedTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") }
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = updatedContent,
                    onValueChange = { updatedContent = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Content") }
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { isEditing = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        if (updatedTitle.isNotBlank() && updatedContent.isNotBlank()) {
                            onUpdate(note.copy(title = updatedTitle, content = updatedContent))
                            isEditing = false
                        }
                    }) {
                        Text("Save")
                    }
                }
            } else {
                Text(note.title, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(2.dp))
                Text(note.date ?: "", fontSize = MaterialTheme.typography.bodySmall.fontSize, color = Color.Gray)

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Edit icon button
                    IconButton(onClick = { isEditing = true }) {
                        Image(painter = painterResource(id = R.drawable.baseline_mode_edit_outline_24), contentDescription = "Edit Note")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Delete icon button
                    IconButton(onClick = { onDelete(note.id) }) {
                        Image(painter = painterResource(id = R.drawable.baseline_delete_outline_24), contentDescription = "Delete Note")
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun NotesScreenUIPreview() {
    val sampleNotes = listOf(
        Note(id = 1, title = "Grocery List", content = "Buy milk, eggs, and bread", date = "17 Aug 2025, 09:45"),
        Note(id = 2, title = "Meeting Reminder", content = "Call John at 5pm", date = "16 Aug 2025, 18:10"),
        Note(id = 3, title = "Learning", content = "Read Jetpack Compose tutorial", date = "15 Aug 2025, 20:30")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
        LazyColumn {
            items(sampleNotes, key = { it.id }) { note ->
                NoteItem(note = note, onUpdate = {}, onDelete = {})
            }
        }
    }
}
