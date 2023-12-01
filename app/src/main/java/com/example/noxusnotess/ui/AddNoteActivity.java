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

        if(title.isEmpty())
            title = "Unnamed note";

        Note newNote = new Note(title);

        // Show the password dialog and wait for the result
        showPasswordDialog(isPasswordLocked -> {
            if (isPasswordLocked) {
                newNote.setPasswordLocked(true);
                newNote.setHashedPassword(CryptoUtils.hashPassword(password));
            }

            StorageUtils.saveNote(newNote, content, this);
            finish();
        });
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

    private void showPasswordDialog(PasswordDialogCallback callback) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_password, null);

        Button buttonPositive = dialogView.findViewById(R.id.buttonPositive);
        Button buttonNegative = dialogView.findViewById(R.id.buttonNegative);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        buttonPositive.setOnClickListener(view -> {
            showEnterPasswordDialog(callback);
            dialog.dismiss();
        });

        buttonNegative.setOnClickListener(view -> {
            dialog.dismiss();
            callback.onPasswordResult(false);
        });

        dialog.show();
    }

    private void showEnterPasswordDialog(PasswordDialogCallback callback) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_password, null);

        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonEnter = dialogView.findViewById(R.id.buttonEnter);
        AppCompatEditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        buttonEnter.setOnClickListener(view -> {
            password = editTextPassword.getText().toString();
            isPasswordLocked = true;
            dialog.dismiss();  // Dismiss the dialog before finishing the activity
            callback.onPasswordResult(true);
        });

        buttonCancel.setOnClickListener(view -> {
            dialog.dismiss();
            callback.onPasswordResult(false);
        });

        dialog.show();
    }

    // Define a callback interface
    interface PasswordDialogCallback {
        void onPasswordResult(boolean isPasswordLocked);
    }
}
