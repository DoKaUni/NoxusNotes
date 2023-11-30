package com.example.noxusnotess.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.example.noxusnotess.R;
import com.example.noxusnotess.utils.CryptoUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoadNotesAsync.AsyncResponse<LiveData<List<Note>>>, NoteAdapter.OnItemClickListener {

    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Execute the AsyncTask to load notes in the background
        new LoadNotesAsync(this, this, false).execute();

        Button buttonAddNote = findViewById(R.id.buttonAddNote);
        buttonAddNote.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddNoteActivity.class);
            view.getContext().startActivity(intent);
        });

        Button buttonTrashCan = findViewById(R.id.buttonTrashCan);
        buttonTrashCan.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), TrashCanActivity.class);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public void onAsyncTaskComplete(LiveData<List<Note>> notesLiveData) {
        // Initialize the RecyclerView adapter with an empty list
        noteAdapter = new NoteAdapter(new ArrayList<>(), this);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setAdapter(noteAdapter);

        // Observe changes in notes LiveData
        notesLiveData.observe(this, notes -> {
            // Update the RecyclerView adapter when the list changes
            noteAdapter.updateNotes(notes);
        });
    }

    @Override
    public void onItemClick(Note note) {
        if (note.isPasswordLocked()) {
            showPasswordDialog(note);
        } else {
            openNoteActivity(note);
        }
    }

    private void showPasswordDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and message for the dialog
        builder.setTitle("Enter Password")
                .setMessage("This note is password-locked. Enter the password to unlock.");

        // Use a custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_password, null);
        AppCompatEditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Retrieve the entered password
            String enteredPassword = editTextPassword.getText().toString();

            // Check if the entered password is correct
            String hashedPassword = CryptoUtils.hashPassword(enteredPassword);
            if (hashedPassword.equals(note.getHashedPassword())) {
                // If the password is correct, open the note
                openNoteActivity(note);
            } else {
                // If the password is incorrect, show a message (you can customize this)
                Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User canceled, do nothing
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openNoteActivity(Note note) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("selectedNote", note);
        startActivity(intent);
    }
}



