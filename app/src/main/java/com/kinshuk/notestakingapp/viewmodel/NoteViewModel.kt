package com.kinshuk.notestakingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kinshuk.notestakingapp.model.Note
import com.kinshuk.notestakingapp.repositoy.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(app:Application,
                    private val noteRepo:NoteRepository):

        AndroidViewModel(app)
{
        fun addNote(note: Note) = viewModelScope.launch {
             noteRepo.insertNote(note)
         }
        fun deleteNote(note:Note)= viewModelScope.launch {
            noteRepo.deleteNote(note)
        }
        fun updateNote(note: Note) = viewModelScope.launch {
            noteRepo.updateNote(note)
        }
        fun getAllNotes() = noteRepo.getAllNotes()
        fun searchNotes(query:String?) = noteRepo.seachNotes(query)
}