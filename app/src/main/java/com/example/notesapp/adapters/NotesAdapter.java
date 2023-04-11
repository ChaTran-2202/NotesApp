package com.example.notesapp.adapters;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.api.NotesListener;
import com.example.notesapp.models.Note;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> notes;
    private List<Note> notesSource;
    private NotesListener notesListener;
    private Timer timer;
    public NotesAdapter(List<Note> notes, NotesListener notesListener){
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNoteItem.setOnClickListener(view -> {
            notesListener.onNoteClicked(notes.get(position), position);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // Tao view holder cho NotesAdapter
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView vwTitleItem, vwDateTimeItem;
        LinearLayout layoutNoteItem;
        RoundedImageView vwImageItem;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            vwTitleItem = itemView.findViewById(R.id.vwTitleItem);
            vwDateTimeItem = itemView.findViewById(R.id.vwDateTimeItem);
            layoutNoteItem = itemView.findViewById(R.id.layoutNoteItem);
            vwImageItem = itemView.findViewById(R.id.vwImageItem);
        }

        void setNote(Note note) {
            vwTitleItem.setText(note.getNoteTitle());
            vwDateTimeItem.setText(note.getNoteDate());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNoteItem.getBackground();
            if (note.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#FFC8EAC"));
            }

            if (note.getImagePath() != null) {
                vwImageItem.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                vwImageItem.setVisibility(View.VISIBLE);
            } else {
                vwImageItem.setVisibility(View.GONE);
            }
        }
    }

    public void searchNotes(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()){
                    notes = notesSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : notesSource){
                        if (note.getNoteTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                        || note.getNoteContent().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer(){
        if (timer != null){
            timer.cancel();
        }
    }
}
