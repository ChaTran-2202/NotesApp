package com.example.notesapp.activities;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private TextView vwDateTime;
    private ImageView vwImage;
    private TextView vwUrl;
    private LinearLayout urlArea;
    private String selectedNoteColor;
    private String selectedImagePath;
    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;
    private Note alreadyAvailabelNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        // Quay lai MainLayout
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> onBackPressed());

        // Khai bao thanh phan Notelayout
        inputTitle = findViewById(R.id.inputTitle);
        inputContent = findViewById(R.id.inputContent);
        vwDateTime = findViewById(R.id.vwDateTime);
        vwImage = findViewById(R.id.vwImage);
        vwUrl = findViewById(R.id.vwUrl);
        urlArea = findViewById(R.id.urlArea);

        vwDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );

        // Luu note
        ImageView btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(view -> saveNote());

        selectedNoteColor = "#FFFC8EAC";
        selectedImagePath = "";
        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailabelNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdate();
        }

        // Xoa anh
        findViewById(R.id.btnRemoveImage).setOnClickListener(view -> {
            vwImage.setImageBitmap(null);
            vwImage.setVisibility(View.GONE);
            findViewById(R.id.btnRemoveImage).setVisibility(View.GONE);
            selectedImagePath = "";
        });

        // Xoa link
        findViewById(R.id.btnRemoveURL).setOnClickListener(view -> {
            vwUrl
                    .setText(null);
            urlArea.setVisibility(View.GONE);
        });

        if (getIntent().getBooleanExtra("isFromMainMenu", false)) {
            String type = getIntent().getStringExtra("actionType");
            if (type != null) {
                if (type.equals("image")) {
                    selectedImagePath = getIntent().getStringExtra("imagePath");
                    vwImage.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                    vwImage.setVisibility(View.VISIBLE);
                    findViewById(R.id.btnRemoveImage).setVisibility(View.VISIBLE);
                } else if (type.equals("URL")) {
                    vwUrl
                            .setText(getIntent().getStringExtra("URL"));
                    urlArea.setVisibility(View.VISIBLE);
                }
            }
        }

        initMenu();
    }

    private void setViewOrUpdate() {
        inputTitle.setText(alreadyAvailabelNote.getNoteTitle());
        inputContent.setText(alreadyAvailabelNote.getNoteContent());
        vwDateTime.setText(alreadyAvailabelNote.getNoteDate());
        if (alreadyAvailabelNote.getImagePath() != null && !alreadyAvailabelNote.getImagePath().trim().isEmpty()) {
            vwImage.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailabelNote.getImagePath()));
            vwImage.setVisibility(View.VISIBLE);
            findViewById(R.id.btnRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailabelNote.getImagePath();
        }

        if (alreadyAvailabelNote.getLinkURL() != null && !alreadyAvailabelNote.getLinkURL().trim().isEmpty()) {
            vwUrl
                    .setText(alreadyAvailabelNote.getLinkURL());
            urlArea.setVisibility(View.VISIBLE);
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
        note.setNoteDate(vwDateTime.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);
        // Check and set URL for note
        if (urlArea.getVisibility() == View.VISIBLE) {
            note.setLinkURL(vwUrl.getText().toString());
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
        final LinearLayout layoutNoteMenu = findViewById(R.id.layoutNoteMenu);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutNoteMenu);

        layoutNoteMenu.findViewById(R.id.btnAction).setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                layoutNoteMenu.findViewById(R.id.vwName).setVisibility(View.GONE);
                layoutNoteMenu.findViewById(R.id.colorExtension).setVisibility(View.GONE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                layoutNoteMenu.findViewById(R.id.vwName).setVisibility(View.VISIBLE);
                layoutNoteMenu.findViewById(R.id.colorExtension).setVisibility(View.VISIBLE);
            }
        });

        layoutNoteMenu.findViewById(R.id.btnColorExtension).setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                layoutNoteMenu.findViewById(R.id.btnAddImageNote).setVisibility(View.GONE);
                layoutNoteMenu.findViewById(R.id.btnAddUrlNote).setVisibility(View.GONE);
                layoutNoteMenu.findViewById(R.id.btnDeleteNote).setVisibility(View.GONE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                layoutNoteMenu.findViewById(R.id.btnAddImageNote).setVisibility(View.VISIBLE);
                layoutNoteMenu.findViewById(R.id.btnAddUrlNote).setVisibility(View.VISIBLE);
                layoutNoteMenu.findViewById(R.id.btnDeleteNote).setVisibility(View.VISIBLE);
            }
        });

        final ImageView btnColor0 = layoutNoteMenu.findViewById(R.id.btnColor0);
        final ImageView btnColor1 = layoutNoteMenu.findViewById(R.id.btnColor1);
        final ImageView btnColor2 = layoutNoteMenu.findViewById(R.id.btnColor2);
        final ImageView btnColor3 = layoutNoteMenu.findViewById(R.id.btnColor3);
        final ImageView btnColor4 = layoutNoteMenu.findViewById(R.id.btnColor4);
        final ImageView btnColor5 = layoutNoteMenu.findViewById(R.id.btnColor5);
        final ImageView btnColor6 = layoutNoteMenu.findViewById(R.id.btnColor6);

        layoutNoteMenu.findViewById(R.id.vwColor0).setOnClickListener(view -> {
                    selectedNoteColor = "#FFFC8EAC";
                    btnColor0.setImageResource(R.drawable.ic_done);
                    btnColor1.setImageResource(0);
                    btnColor2.setImageResource(0);
                    btnColor3.setImageResource(0);
                    btnColor4.setImageResource(0);
                    btnColor5.setImageResource(0);
                    btnColor6.setImageResource(0);
                });

        layoutNoteMenu.findViewById(R.id.vwColor1).setOnClickListener(view -> {
                    selectedNoteColor = "#FFA500";
                    btnColor0.setImageResource(0);
                    btnColor1.setImageResource(R.drawable.ic_done);
                    btnColor2.setImageResource(0);
                    btnColor3.setImageResource(0);
                    btnColor4.setImageResource(0);
                    btnColor5.setImageResource(0);
                    btnColor6.setImageResource(0);
                });

        layoutNoteMenu.findViewById(R.id.vwColor2).setOnClickListener(view -> {
                    selectedNoteColor = "#00CC66";
                    btnColor0.setImageResource(0);
                    btnColor1.setImageResource(0);
                    btnColor2.setImageResource(R.drawable.ic_done);
                    btnColor3.setImageResource(0);
                    btnColor4.setImageResource(0);
                    btnColor5.setImageResource(0);
                    btnColor6.setImageResource(0);
                });

        layoutNoteMenu.findViewById(R.id.vwColor3).setOnClickListener(view -> {
                    selectedNoteColor = "#256476";
                    btnColor0.setImageResource(0);
                    btnColor1.setImageResource(0);
                    btnColor2.setImageResource(0);
                    btnColor3.setImageResource(R.drawable.ic_done);
                    btnColor4.setImageResource(0);
                    btnColor5.setImageResource(0);
                    btnColor6.setImageResource(0);
                });

        layoutNoteMenu.findViewById(R.id.vwColor4).setOnClickListener(view -> {
                    selectedNoteColor = "#0066CC";
                    btnColor0.setImageResource(0);
                    btnColor1.setImageResource(0);
                    btnColor2.setImageResource(0);
                    btnColor3.setImageResource(0);
                    btnColor4.setImageResource(R.drawable.ic_done);
                    btnColor5.setImageResource(0);
                    btnColor6.setImageResource(0);
                });

        layoutNoteMenu.findViewById(R.id.vwColor5).setOnClickListener(view -> {
                    selectedNoteColor = "#993300";
                    btnColor0.setImageResource(0);
                    btnColor1.setImageResource(0);
                    btnColor2.setImageResource(0);
                    btnColor3.setImageResource(0);
                    btnColor4.setImageResource(0);
                    btnColor5.setImageResource(R.drawable.ic_done);
                    btnColor6.setImageResource(0);
                });

        layoutNoteMenu.findViewById(R.id.vwColor6).setOnClickListener(view -> {
                    selectedNoteColor = "#232428";
                    btnColor0.setImageResource(0);
                    btnColor1.setImageResource(0);
                    btnColor2.setImageResource(0);
                    btnColor3.setImageResource(0);
                    btnColor4.setImageResource(0);
                    btnColor5.setImageResource(0);
                    btnColor6.setImageResource(R.drawable.ic_done);
                });

        if (alreadyAvailabelNote != null && alreadyAvailabelNote.getColor() != null && !alreadyAvailabelNote.getColor().trim().isEmpty()) {
            switch (alreadyAvailabelNote.getColor()) {
                case "#FFC8EAC":
                    layoutNoteMenu
                            .findViewById(R.id.vwColor0).performClick();
                    break;
                case "#FFA500":
                    layoutNoteMenu
                            .findViewById(R.id.vwColor1).performClick();
                    break;
                case "#00CC66":
                    layoutNoteMenu
                            .findViewById(R.id.vwColor2).performClick();
                    break;
                case "#256476":
                    layoutNoteMenu
                            .findViewById(R.id.vwColor3).performClick();
                    break;
                case "#0066CC":
                    layoutNoteMenu
                            .findViewById(R.id.vwColor4).performClick();
                    break;
                case "#993300":
                    layoutNoteMenu
                            .findViewById(R.id.vwColor5).performClick();
                    break;
                case "#232428":
                    layoutNoteMenu
                            .findViewById(R.id.vwColor6).performClick();
                    break;
            }
        }

        // Them anh vao note
        layoutNoteMenu.findViewById(R.id.btnAddImageNote).setOnClickListener(view -> {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                    } else {
                        selectedImage();
                    }
                });

        // Them link vao note
        layoutNoteMenu.findViewById(R.id.btnAddUrlNote).setOnClickListener(view -> {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showAddURLDialog();
                });

        // Xoa note
        if (alreadyAvailabelNote != null) {
            layoutNoteMenu.findViewById(R.id.btnDeleteNote).setVisibility(View.VISIBLE);
            layoutNoteMenu.findViewById(R.id.btnDeleteNote).setOnClickListener(view -> {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        showDeleteNoteDialog();
                    });
        }
    }

    private void showDeleteNoteDialog() {
        if (dialogDeleteNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_delete_note, findViewById(R.id.layoutDeleteNote));
            builder.setView(view);
            dialogDeleteNote = builder.create();

            if (dialogDeleteNote.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.btnConfirmYes).setOnClickListener(view1 -> {
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

            view.findViewById(R.id.btnClose).setOnClickListener(view1 -> dialogDeleteNote.dismiss());
            view.findViewById(R.id.btnConfirmNo).setOnClickListener(view1 -> dialogDeleteNote.dismiss());
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
                        vwImage.setImageBitmap(bitmap);
                        vwImage.setVisibility(View.VISIBLE);
                        findViewById(R.id.btnRemoveImage).setVisibility(View.VISIBLE);

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

    // Phuong thuc hien thi layoutAddUrl
    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_add_url, findViewById(R.id.layoutAddUrl));
            builder.setView(view);
            dialogAddURL = builder.create();

            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputUrl = view.findViewById(R.id.inputUrl);
            inputUrl.requestFocus();

            view.findViewById(R.id.btnConfirmUrl).setOnClickListener(view1 -> {
                if (inputUrl.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CreateNoteActivity.this, "Please enter URL!", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.WEB_URL.matcher(inputUrl.getText().toString()).matches()) {
                    Toast.makeText(CreateNoteActivity.this, "Please enter a valid URL!", Toast.LENGTH_SHORT).show();
                } else {
                    vwUrl.setText(inputUrl.getText().toString());
                    urlArea.setVisibility(View.VISIBLE);
                    dialogAddURL.dismiss();
                }
            });

            view.findViewById(R.id.btnCloseUrl).setOnClickListener(view1 -> dialogAddURL.dismiss());
        }
        dialogAddURL.show();
    }
}