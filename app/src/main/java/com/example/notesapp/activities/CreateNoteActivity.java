package com.example.notesapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.R;
import com.example.notesapp.database.NotesDatabase;
import com.example.notesapp.models.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private EditText inputTitle, inputContent;
    private TextView dateTime;
    private String selectedNoteColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(view -> onBackPressed());

        inputTitle = findViewById(R.id.txtTitle);
        inputContent = findViewById(R.id.txtContent);
        dateTime = findViewById(R.id.txtDate);

        dateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(view -> saveNote());

        initMenu();
        selectedNoteColor = "#FF000000";
    }

    private void saveNote() {
        if (inputTitle.getText().toString().trim().isEmpty() && inputContent.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setNoteTitle(inputTitle.getText().toString());
        note.setNoteContent(inputContent.getText().toString());
        note.setNoteDate(dateTime.getText().toString());
        note.setColor(selectedNoteColor);

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new SaveNoteTask().execute();
    }

    private void initMenu(){
        final LinearLayout menuLayout = findViewById(R.id.menuLayout);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(menuLayout);
        menuLayout.findViewById(R.id.more).setOnClickListener(view -> {
            if (bottomSheetBehavior.getState()!= BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        final ImageView imgColor0 = menuLayout.findViewById(R.id.imgColor0);
        final ImageView imgColor1 = menuLayout.findViewById(R.id.imgColor1);
        final ImageView imgColor2 = menuLayout.findViewById(R.id.imgColor2);
        final ImageView imgColor3 = menuLayout.findViewById(R.id.imgColor3);
        final ImageView imgColor4 = menuLayout.findViewById(R.id.imgColor4);
        final ImageView imgColor5 = menuLayout.findViewById(R.id.imgColor5);
        final ImageView imgColor6 = menuLayout.findViewById(R.id.imgColor6);

        menuLayout.findViewById(R.id.vwColor0).setOnClickListener(view -> {
            selectedNoteColor = "#FF000000";
            imgColor0.setImageResource(R.drawable.ic_done);
            imgColor1.setImageResource(0);
            imgColor2.setImageResource(0);
            imgColor3.setImageResource(0);
            imgColor4.setImageResource(0);
            imgColor5.setImageResource(0);
            imgColor6.setImageResource(0);
        });

        menuLayout.findViewById(R.id.vwColor1).setOnClickListener(view -> {
            selectedNoteColor = "#264D3B";
            imgColor0.setImageResource(0);
            imgColor1.setImageResource(R.drawable.ic_done);
            imgColor2.setImageResource(0);
            imgColor3.setImageResource(0);
            imgColor4.setImageResource(0);
            imgColor5.setImageResource(0);
            imgColor6.setImageResource(0);
        });

        menuLayout.findViewById(R.id.vwColor2).setOnClickListener(view -> {
            selectedNoteColor = "#0C635D";
            imgColor0.setImageResource(0);
            imgColor1.setImageResource(0);
            imgColor2.setImageResource(R.drawable.ic_done);
            imgColor3.setImageResource(0);
            imgColor4.setImageResource(0);
            imgColor5.setImageResource(0);
            imgColor6.setImageResource(0);
        });

        menuLayout.findViewById(R.id.vwColor3).setOnClickListener(view -> {
            selectedNoteColor = "#256476";
            imgColor0.setImageResource(0);
            imgColor1.setImageResource(0);
            imgColor2.setImageResource(0);
            imgColor3.setImageResource(R.drawable.ic_done);
            imgColor4.setImageResource(0);
            imgColor5.setImageResource(0);
            imgColor6.setImageResource(0);
        });

        menuLayout.findViewById(R.id.vwColor4).setOnClickListener(view -> {
            selectedNoteColor = "#274255";
            imgColor0.setImageResource(0);
            imgColor1.setImageResource(0);
            imgColor2.setImageResource(0);
            imgColor3.setImageResource(0);
            imgColor4.setImageResource(R.drawable.ic_done);
            imgColor5.setImageResource(0);
            imgColor6.setImageResource(0);
        });

        menuLayout.findViewById(R.id.vwColor5).setOnClickListener(view -> {
            selectedNoteColor = "#4B443A";
            imgColor0.setImageResource(0);
            imgColor1.setImageResource(0);
            imgColor2.setImageResource(0);
            imgColor3.setImageResource(0);
            imgColor4.setImageResource(0);
            imgColor5.setImageResource(R.drawable.ic_done);
            imgColor6.setImageResource(0);
        });

        menuLayout.findViewById(R.id.vwColor6).setOnClickListener(view -> {
            selectedNoteColor = "#232428";
            imgColor0.setImageResource(0);
            imgColor1.setImageResource(0);
            imgColor2.setImageResource(0);
            imgColor3.setImageResource(0);
            imgColor4.setImageResource(0);
            imgColor5.setImageResource(0);
            imgColor6.setImageResource(R.drawable.ic_done);
        });
    }
}