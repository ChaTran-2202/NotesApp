package com.example.notesapp.Models;

import androidx.room.*;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    int noteID = 0;
    @ColumnInfo(name = "title")
    String noteTitle;
    @ColumnInfo(name = "content")
    String noteContent;
    @ColumnInfo(name = "date")
    String noteDate;

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }
}
