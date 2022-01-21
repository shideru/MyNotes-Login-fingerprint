package com.example.mynotes001.db

import androidx.room.*

@Dao
interface NoteDao {
    @Insert
     fun addNote(note: Note)

    @Query("SELECT * FROM note ORDER BY id DESC")
     fun getAllNotes() : List<Note>

    @Insert
     fun addMultipleNotes(vararg note: Note)

    @Update
     fun updateNote(note: Note)

    @Delete
     fun deleteNote(note: Note)
}