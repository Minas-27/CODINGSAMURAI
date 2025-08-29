package com.example.notesappwithsqlitedb

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesViewModel(context: Context) : ViewModel() {

    private val dbHelper = NotesDatabaseHelper(context)
    var notes = mutableStateListOf<Note>()
        private set

    init {
        loadNotes()
    }

    private fun loadNotes() {
        notes.clear()
        notes.addAll(dbHelper.getAllNotes())
    }

    fun addNote(title: String, content: String, date: String) {
        val id = dbHelper.insertNote(Note(title = title, content = content, date = date))
        if (id != -1L) {
            notes.add(Note(id = id, title = title, content = content, date = date))
        }
    }

    fun updateNote(note: Note) {
        val updatedRows = dbHelper.updateNote(note)
        if (updatedRows > 0) {
            val index = notes.indexOfFirst { it.id == note.id }
            if (index != -1) {
                notes[index] = note
            }
        }
    }

    fun deleteNote(id: Long) {
        val deletedRows = dbHelper.deleteNote(id)
        if (deletedRows > 0) {
            notes.removeAll { it.id == id }
        }
    }
}
