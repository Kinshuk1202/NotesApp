package com.kinshuk.notestakingapp.repositoy

import com.kinshuk.notestakingapp.database.NoteDatabase
import com.kinshuk.notestakingapp.model.Note

class NoteRepository(private val db:NoteDatabase) {

    suspend fun insertNote(note: Note) = db.getNoteDao().insertNote(note)
    suspend fun deleteNote(note:Note) = db.getNoteDao().deleteNote(note)
    suspend fun updateNote(note:Note) = db.getNoteDao().updateNote(note)

    fun getAllNotes() = db.getNoteDao().getAllNotes()
    fun seachNotes(query:String?) = db.getNoteDao().searchNote(query)
}