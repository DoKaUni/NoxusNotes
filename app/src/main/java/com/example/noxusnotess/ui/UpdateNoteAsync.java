package com.example.noxusnotess.ui;

import android.content.Context;
import android.os.AsyncTask;

import com.example.noxusnotess.database.NoteDatabase;

public class UpdateNoteAsync extends AsyncTask<Void, Void, Void> {
    private Note noteToUpdate;
    private Context context;

    UpdateNoteAsync(Note note) {
        this.noteToUpdate = note;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(context);
        noteDatabase.noteDAO().update(noteToUpdate);
        return null;
    }
}
