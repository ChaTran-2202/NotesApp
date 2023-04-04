package com.example.notesapp.database;

import android.content.Context;

import androidx.room.*;

import com.example.notesapp.models.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase notesDatabase;
    private static final String DB_NAME = "NotesApp";

    public static NotesDatabase getDatabase(Context context) {
        if (notesDatabase == null) {
            synchronized (NotesDatabase.class) {
                notesDatabase = Room.databaseBuilder(context.getApplicationContext(), NotesDatabase.class, DB_NAME)
                        .build();
            }
        }
        return notesDatabase;
    }

    public abstract NoteDao noteDao();
}
