package com.example.noxusnotess.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.noxusnotess.R;
import com.example.noxusnotess.ui.UpdateNoteAsync;
import com.example.noxusnotess.utils.CryptoUtils;
import com.example.noxusnotess.utils.StorageUtils;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextNoteTitle;
    private EditText editTextNoteContent;
    private Note existingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextNoteTitle = findViewById(R.id.editTextNoteTitle);
        editTextNoteContent = findViewById(R.id.editTextNoteContent);
        Button buttonSaveNote = findViewById(R.id.buttonSaveNote);
        Button buttonDeleteNote = findViewById(R.id.buttonDeleteNote);

        existingNote = getIntent().getParcelableExtra("selectedNote");
        if (existingNote != null) {
            // Populate the fields with existing note data
            editTextNoteTitle.setText(existingNote.getTitle());
            editTextNoteContent.setText(CryptoUtils.decryptData(existingNote.getEncryptedFilePath(), this));
            buttonDeleteNote.setVisibility(View.VISIBLE);

            buttonSaveNote.setOnClickListener(v -> saveEditedNote());
            buttonDeleteNote.setOnClickListener(v -> deleteNote());
        } else {
            buttonDeleteNote.setVisibility(View.GONE);

            buttonSaveNote.setOnClickListener(v -> saveNote());
        }
    }

    private void saveNote() {
        String title = editTextNoteTitle.getText().toString();
        String content = editTextNoteContent.getText().toString();

        // Create a new Note object
        Note newNote = new Note(title);
        StorageUtils.saveNote(newNote, content, this);

        finish();
    }

    private void saveEditedNote() {
        String title = editTextNoteTitle.getText().toString();
        String content = editTextNoteContent.getText().toString();

        existingNote.setTitle(title);
        existingNote.setModifiedDate(existingNote.getCurrentDate());
        StorageUtils.saveNote(existingNote, content, this);

        // Use AsyncTask to update the note in the background
        new UpdateNoteAsync(existingNote).execute();
        finish();
    }

    private void deleteNote() {
        existingNote.setModifiedDate(existingNote.getCurrentDate());
        existingNote.setDeleted(true);

        // Use AsyncTask to update the note in the background
        new UpdateNoteAsync(existingNote).execute();
        finish();
    }
}