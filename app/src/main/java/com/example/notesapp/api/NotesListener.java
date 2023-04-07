package com.example.notesapp.api;

import com.example.notesapp.models.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
