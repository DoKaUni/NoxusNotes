package com.example.noxusnotess.ui;

import android.os.AsyncTask;
import android.content.Context;

import com.example.noxusnotess.database.NoteDatabase;

public class DeleteNoteAsync extends AsyncTask<Void, Void, Void> {
    private Note noteToDelete;
    private Context context;

    DeleteNoteAsync(Note note, Context context) {
        this.noteToDelete = note;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(context);
        noteDatabase.noteDAO().delete(noteToDelete);
        return null;
    }
}
