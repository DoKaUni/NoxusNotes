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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextNoteTitle = findViewById(R.id.editTextNoteTitle);
        editTextNoteContent = findViewById(R.id.editTextNoteContent);

        Button buttonSaveNote = findViewById(R.id.buttonSaveNote);
        buttonSaveNote.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = editTextNoteTitle.getText().toString();
        String content = editTextNoteContent.getText().toString();

        // Create a new Note object
        Note newNote = new Note(title, content);
        StorageUtils.saveNote(newNote, this);

        // For now, you can store the notes in a List in memory
        // In a real app, you would store them in a database or other persistent storage
        // For simplicity, we will use a List for demonstration purposes.
        // You may want to explore Room database for more complex scenarios.

        // TODO: Add the newNote to your list of notes (which could be a ViewModel or another appropriate component).

        // Optionally, you can navigate back to the main activity or perform other actions.
        // For now, we will just finish the activity.
        finish();
    }
}
