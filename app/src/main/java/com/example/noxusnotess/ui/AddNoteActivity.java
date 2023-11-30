package com.example.noxusnotess.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.example.noxusnotess.R;
import com.example.noxusnotess.utils.CryptoUtils;
import com.example.noxusnotess.utils.StorageUtils;

public class AddNoteActivity extends AppCompatActivity {

    private AppCompatEditText editTextNoteTitle;
    private AppCompatEditText editTextNoteContent;
    private AlertDialog passwordDialog;
    private Note existingNote;
    private int selectedStart = -1;
    private int selectedEnd = -1;
    private int colourIndex = 0;

    private boolean isPasswordLocked = false;
    private String password;

    // Colors for highlighting
    private final int[] highlightColors = {
            Color.BLACK, Color.YELLOW, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.RED,
            Color.BLUE, Color.parseColor("#FFA500"), Color.parseColor("#800080"),
            Color.parseColor("#008000"), Color.parseColor("#800000")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextNoteTitle = findViewById(R.id.editTextNoteTitle);
        editTextNoteContent = findViewById(R.id.editTextNoteContent);
        Button buttonSaveNote = findViewById(R.id.buttonSaveNote);
        Button buttonDeleteNote = findViewById(R.id.buttonDeleteNote);
        Button buttonHighlightText = findViewById(R.id.buttonHighlightText);

        existingNote = getIntent().getParcelableExtra("selectedNote");
        if (existingNote != null) {
            editTextNoteTitle.setText(existingNote.getTitle());
            editTextNoteContent.setText(CryptoUtils.decryptData(existingNote.getEncryptedFilePath(), this));
            buttonDeleteNote.setVisibility(View.VISIBLE);

            buttonSaveNote.setOnClickListener(v -> saveEditedNote());
            buttonDeleteNote.setOnClickListener(v -> deleteNote());
        } else {
            buttonDeleteNote.setVisibility(View.GONE);
            buttonSaveNote.setOnClickListener(v -> saveNote());
        }

        editTextNoteContent.setOnClickListener(v -> clearSelection());

        buttonHighlightText.setOnClickListener(this::onHighlightButtonClick);
    }

    private void clearSelection() {
        selectedStart = -1;
        selectedEnd = -1;
        editTextNoteContent.clearFocus();
        editTextNoteContent.setSelected(false);
    }

    private void highlightSelectedText() {
        if (selectedStart != -1 && selectedEnd != -1) {
            SpannableString spannable = new SpannableString(editTextNoteContent.getText());

            if (colourIndex > highlightColors.length) {
                colourIndex = 0;
            } else
                colourIndex++;

            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(highlightColors[colourIndex]);
            spannable.setSpan(backgroundColorSpan, selectedStart, selectedEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editTextNoteContent.setText(spannable);
        }
    }

    private void saveNote() {
        String title = editTextNoteTitle.getText().toString();
        String content = editTextNoteContent.getText().toString();

        Note newNote = new Note(title);

        showPasswordDialog();
        if (isPasswordLocked) {
            newNote.setPasswordLocked(true);
            newNote.setHashedPassword(CryptoUtils.hashPassword(password));
        }

        StorageUtils.saveNote(newNote, content, this);
        finish();
    }

    private void saveEditedNote() {
        String title = editTextNoteTitle.getText().toString();
        String content = editTextNoteContent.getText().toString();

        existingNote.setTitle(title);
        existingNote.setModifiedDate(existingNote.getCurrentDate());
        StorageUtils.saveNote(existingNote, content, this);

        new UpdateNoteAsync(existingNote).execute();
        finish();
    }

    private void deleteNote() {
        existingNote.setModifiedDate(existingNote.getCurrentDate());
        existingNote.setDeleted(true);
        new UpdateNoteAsync(existingNote).execute();
        finish();
    }

    public void onHighlightButtonClick(View view) {
        int selectionStart = editTextNoteContent.getSelectionStart();
        int selectionEnd = editTextNoteContent.getSelectionEnd();

        if (selectionStart != selectionEnd) {
            selectedStart = selectionStart;
            selectedEnd = selectionEnd;
        }

        highlightSelectedText();
    }

    public void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Set Password")
                .setMessage("Do you want to set a password for this note?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    showEnterPasswordDialog();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });

        passwordDialog = builder.create(); // Assign the created dialog to the class variable
    }

    private void showEnterPasswordDialog() {
        // Use a custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_password, null);

        AppCompatEditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Enter Password")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Retrieve the entered password
                    password = editTextPassword.getText().toString();
                    isPasswordLocked = true;
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User canceled, don't set a password
                    dialog.dismiss();
                });

        passwordDialog = builder.create(); // Assign the created dialog to the class variable
        passwordDialog.show();
    }
}
