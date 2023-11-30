package com.example.noxusnotess.ui;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noxusnotess.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrashCanActivity extends AppCompatActivity implements LoadNotesAsync.AsyncResponse<LiveData<List<Note>>>, NoteAdapter.OnItemClickListener {

    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_can);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewDeletedNotes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Execute the AsyncTask to load notes in the background
        new LoadNotesAsync(this, this, true).execute();

        Button buttonBackToNotes = findViewById(R.id.buttonBackToNotes);
        buttonBackToNotes.setOnClickListener(view -> finish());
    }

    @Override
    public void onAsyncTaskComplete(LiveData<List<Note>> notesLiveData) {
        // Initialize the RecyclerView adapter with an empty list
        noteAdapter = new NoteAdapter(new ArrayList<>(), this);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewDeletedNotes);

        recyclerView.setAdapter(noteAdapter);

        // Observe changes in notes LiveData
        notesLiveData.observe(this, notes -> {
            // Update the RecyclerView adapter when the list changes
            noteAdapter.updateNotes(notes);
        });
    }

    @Override
    public void onItemClick(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and message for the dialog
        builder.setTitle("Delete or Restore")
                .setMessage("Do you want to delete or restore this note?");

        builder.setPositiveButton("Restore", (dialog, which) -> {
            note.setModifiedDate(note.getCurrentDate());
            note.setDeleted(false);

            // Use AsyncTask to update the note in the background
            new UpdateNoteAsync(note).execute();
        });

        builder.setNegativeButton("Delete", (dialog, which) -> {
            File file = new File(note.getEncryptedFilePath());
            file.delete();
            new DeleteNoteAsync(note, this).execute();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
