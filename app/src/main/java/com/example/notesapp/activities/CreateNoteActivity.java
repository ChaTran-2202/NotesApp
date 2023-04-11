package com.example.notesapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.R;
import com.example.notesapp.database.NotesDatabase;
import com.example.notesapp.models.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECTED_IMAGE = 2;
    private EditText inputTitle, inputContent;
    private TextView dateTime;
    private ImageView imgPicture;
    private TextView URL;
    private LinearLayout URLlayout;
    private String selectedNoteColor;
    private String selectedImagePath;
    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;
    private Note alreadyAvailabelNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(view -> onBackPressed());

        inputTitle = findViewById(R.id.txtTitle);
        inputContent = findViewById(R.id.txtContent);
        dateTime = findViewById(R.id.txtDate);
        imgPicture = findViewById(R.id.imgPicture);
        URL = findViewById(R.id.URL);
        URLlayout = findViewById(R.id.URLlayout);

        dateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(view -> saveNote());

        selectedNoteColor = "#FFFC8EAC";
        selectedImagePath = "";
        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailabelNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdate();
        }
        findViewById(R.id.imgRemovePicture).setOnClickListener(view -> {
            imgPicture.setImageBitmap(null);
            imgPicture.setVisibility(View.GONE);
            findViewById(R.id.imgRemovePicture).setVisibility(View.GONE);
            selectedImagePath = "";
        });
        findViewById(R.id.imgRemoveURL).setOnClickListener(view -> {
            URL.setText(null);
            URLlayout.setVisibility(View.GONE);
        });
        if (getIntent().getBooleanExtra("isFromQuickAction", false)) {
            String type = getIntent().getStringExtra("quickActionType");
            if (type != null) {
                if (type.equals("image")) {
                    selectedImagePath = getIntent().getStringExtra("imagePath");
                    imgPicture.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                    imgPicture.setVisibility(View.VISIBLE);
                    findViewById(R.id.imgRemovePicture).setVisibility(View.VISIBLE);
                } else if (type.equals("URL")) {
                    URL.setText(getIntent().getStringExtra("URL"));
                    URLlayout.setVisibility(View.VISIBLE);
                }
            }
        }
        initMenu();
    }

    private void setViewOrUpdate() {
        inputTitle.setText(alreadyAvailabelNote.getNoteTitle());
        inputContent.setText(alreadyAvailabelNote.getNoteContent());
        dateTime.setText(alreadyAvailabelNote.getNoteDate());
        if (alreadyAvailabelNote.getImagePath() != null && !alreadyAvailabelNote.getImagePath().trim().isEmpty()) {
            imgPicture.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailabelNote.getImagePath()));
            imgPicture.setVisibility(View.VISIBLE);
            findViewById(R.id.imgRemovePicture).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailabelNote.getImagePath();
        }

        if (alreadyAvailabelNote.getLinkURL() != null && !alreadyAvailabelNote.getLinkURL().trim().isEmpty()) {
            URL.setText(alreadyAvailabelNote.getLinkURL());
            URLlayout.setVisibility(View.VISIBLE);
        }
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
        note.setImagePath(selectedImagePath);
        // Check and set URL for note
        if (URLlayout.getVisibility() == View.VISIBLE) {
            note.setLinkURL(URL.getText().toString());
        }

        // If note's ID is already available in DB => Replace with new note = Update note
        if (alreadyAvailabelNote != null) {
            note.setNoteID(alreadyAvailabelNote.getNoteID());
        }

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

    private void initMenu() {
        final LinearLayout menuLayout = findViewById(R.id.menuLayout);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(menuLayout);
        menuLayout.findViewById(R.id.imgExtension).setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
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
            selectedNoteColor = "#FFFC8EAC";
            imgColor0.setImageResource(R.drawable.ic_done);
            imgColor1.setImageResource(0);
            imgColor2.setImageResource(0);
            imgColor3.setImageResource(0);
            imgColor4.setImageResource(0);
            imgColor5.setImageResource(0);
            imgColor6.setImageResource(0);
        });

        menuLayout.findViewById(R.id.vwColor1).setOnClickListener(view -> {
            selectedNoteColor = "#FFA500";
            imgColor0.setImageResource(0);
            imgColor1.setImageResource(R.drawable.ic_done);
            imgColor2.setImageResource(0);
            imgColor3.setImageResource(0);
            imgColor4.setImageResource(0);
            imgColor5.setImageResource(0);
            imgColor6.setImageResource(0);
        });

        menuLayout.findViewById(R.id.vwColor2).setOnClickListener(view -> {
            selectedNoteColor = "#00CC66";
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
            selectedNoteColor = "#0066CC";
            imgColor0.setImageResource(0);
            imgColor1.setImageResource(0);
            imgColor2.setImageResource(0);
            imgColor3.setImageResource(0);
            imgColor4.setImageResource(R.drawable.ic_done);
            imgColor5.setImageResource(0);
            imgColor6.setImageResource(0);
        });

        menuLayout.findViewById(R.id.vwColor5).setOnClickListener(view -> {
            selectedNoteColor = "#993300";
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

        if (alreadyAvailabelNote != null && alreadyAvailabelNote.getColor() != null && !alreadyAvailabelNote.getColor().trim().isEmpty()) {
            switch (alreadyAvailabelNote.getColor()) {
                case "#FF424242":
                    menuLayout.findViewById(R.id.vwColor0).performClick();
                    break;
                case "#264D3B":
                    menuLayout.findViewById(R.id.vwColor1).performClick();
                    break;
                case "#0C635D":
                    menuLayout.findViewById(R.id.vwColor2).performClick();
                    break;
                case "#256476":
                    menuLayout.findViewById(R.id.vwColor3).performClick();
                    break;
                case "#274255":
                    menuLayout.findViewById(R.id.vwColor4).performClick();
                    break;
                case "#4B443A":
                    menuLayout.findViewById(R.id.vwColor5).performClick();
                    break;
                case "#232428":
                    menuLayout.findViewById(R.id.vwColor6).performClick();
                    break;
            }
        }

        menuLayout.findViewById(R.id.addImageLayout).setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        CreateNoteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION
                );
            } else {
                selectedImage();
            }
        });

        menuLayout.findViewById(R.id.addLinkLayout).setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showAddURLDialog();
        });

        if (alreadyAvailabelNote != null) {
            menuLayout.findViewById(R.id.deleteNote).setVisibility(View.VISIBLE);
            menuLayout.findViewById(R.id.deleteNote).setOnClickListener(view -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDeleteNoteDialog();
            });
        }
    }

    private void showDeleteNoteDialog() {
        if (dialogDeleteNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_notify,
                    findViewById(R.id.deleteLayout)
            );
            builder.setView(view);

            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.yesConfirm).setOnClickListener(view1 -> {
                @SuppressLint("StaticFieldLeak")
                class DeleteNoteTask extends AsyncTask<Void, Void, Void> {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        NotesDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(alreadyAvailabelNote);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void unused) {
                        super.onPostExecute(unused);
                        Intent intent = new Intent();
                        intent.putExtra("isNoteDeleted", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                new DeleteNoteTask().execute();
            });

            view.findViewById(R.id.noConfirm).setOnClickListener(view1 -> dialogDeleteNote.dismiss());
            view.findViewById(R.id.imgClose).setOnClickListener(view1 -> dialogDeleteNote.dismiss());
        }
        dialogDeleteNote.show();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void selectedImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECTED_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectedImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECTED_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgPicture.setImageBitmap(bitmap);
                        imgPicture.setVisibility(View.VISIBLE);
                        findViewById(R.id.imgRemovePicture).setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_link,
                    findViewById(R.id.linkLayout)
            );
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText txtURL = view.findViewById(R.id.txtURL);
            txtURL.requestFocus();

            view.findViewById(R.id.addURL).setOnClickListener(view1 -> {
                if (txtURL.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.WEB_URL.matcher(txtURL.getText().toString()).matches()) {
                    Toast.makeText(CreateNoteActivity.this, "Please enter a valid URL!", Toast.LENGTH_SHORT).show();
                } else {
                    URL.setText(txtURL.getText().toString());
                    URLlayout.setVisibility(View.VISIBLE);
                    dialogAddURL.dismiss();
                }
            });

            view.findViewById(R.id.imgCancel).setOnClickListener(view1 -> dialogAddURL.dismiss());
        }
        dialogAddURL.show();
    }
}