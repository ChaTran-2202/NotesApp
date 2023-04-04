package com.example.notesapp.models;

import androidx.annotation.NonNull;
import androidx.room.*;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int noteID = 0;
    @ColumnInfo(name = "title")
    private String noteTitle;
    @ColumnInfo(name = "content")
    private String noteContent;
    @ColumnInfo(name = "date")
    private String noteDate;
    @ColumnInfo(name = "picture")
    private String imagePath;
    @ColumnInfo(name = "color")
    private String color;
    @ColumnInfo(name = "linkURL")
    private String linkURL;

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    @NonNull
    @Override
    public String toString() {
        return noteTitle + ": " + noteDate;
    }
}
