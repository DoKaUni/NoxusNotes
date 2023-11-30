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
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("selectedNote", note);
        startActivity(intent);
    }
}



