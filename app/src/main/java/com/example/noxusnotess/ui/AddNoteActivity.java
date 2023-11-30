package com.example.noxusnotess.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.noxusnotess.R;
import com.example.noxusnotess.utils.CryptoUtils;
import com.example.noxusnotess.utils.StorageUtils;

import java.text.DateFormat;

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

        existingNote = getIntent().getParcelableExtra("selectedNote");
        if (existingNote != null) {
            // Populate the fields with existing note data
            editTextNoteTitle.setText(existingNote.getTitle());
            editTextNoteContent.setText(CryptoUtils.decryptData(existingNote.getEncryptedFilePath(), this));

            buttonSaveNote.setOnClickListener(v -> saveEditedNote());
        }else
            buttonSaveNote.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = editTextNoteTitle.getText().toString();
        String content = editTextNoteContent.getText().toString();

        // Create a new Note object
        Note newNote = new Note(title);
        StorageUtils.saveNote(newNote, content, this);

        finish();
    }

    private void saveEditedNote(){
        String title = editTextNoteTitle.getText().toString();
        String content = editTextNoteContent.getText().toString();

        existingNote.setTitle(title);
        existingNote.setModifiedDate(existingNote.getCurrentDate());
        StorageUtils.saveNote(existingNote, content, this);

        finish();
    }
}
