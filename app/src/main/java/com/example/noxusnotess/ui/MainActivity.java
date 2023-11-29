package com.example.noxusnotess.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noxusnotess.R;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoadNotesAsync.AsyncResponse<LiveData<List<Note>>> {

    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Execute the AsyncTask to load notes in the background
        new LoadNotesAsync(this, this).execute();

        Button buttonAddNote = findViewById(R.id.buttonAddNote);
        buttonAddNote.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddNoteActivity.class);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public void onAsyncTaskComplete(LiveData<List<Note>> notesLiveData) {
        // Observe changes in notes LiveData
        notesLiveData.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                // Initialize the RecyclerView adapter with the loaded notes
                noteAdapter = new NoteAdapter(notes);
                RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
                recyclerView.setAdapter(noteAdapter);
            }
        });
    }
}


