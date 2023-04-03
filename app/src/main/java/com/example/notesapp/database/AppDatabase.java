package com.example.notesapp.database;

import android.content.Context;

import androidx.room.*;

import com.example.notesapp.models.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase database;
    private static final String DB_NAME = "NotesApp";

    static AppDatabase getDatabase(Context context) {
        if (database == null) {
            synchronized (AppDatabase.class) {
                database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                        .build();
            }
        }
        return database;
    }

    public abstract NoteDao noteDao();
}
