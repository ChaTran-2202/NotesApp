package com.example.notesapp.database;

import androidx.room.*;

import java.util.*;

import com.example.notesapp.models.Note;

@Dao
public interface NoteDao {
    // Return all objects in the database
    @Query("SELECT * FROM notes")
    List<Note> getAllNote();

    // Return an object
    @Query("SELECT * FROM notes WHERE title LIKE :title")
    public Note[] findNote(int title);

    // Use OnConflictStrategy.REPLACE to replace the existing rows with the new rows
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertNote(Note note);

    @Update
    public void updateNote(Note note);

    @Delete
    public void deleteNote(Note note);
}
