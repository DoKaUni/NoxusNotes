package com.example.noxusnotess.ui;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.noxusnotess.database.NoteDAO;
import com.example.noxusnotess.database.NoteDatabase;

import java.util.List;

public class LoadNotesAsync extends AsyncTask<Void, Void, LiveData<List<Note>>> {

    private final Context context;
    private final AsyncResponse<LiveData<List<Note>>> delegate;
    private final boolean loadDeletedNotes; // New parameter to indicate whether to load deleted notes

    public interface AsyncResponse<T> {
        void onAsyncTaskComplete(T result);
    }

    public LoadNotesAsync(Context context, AsyncResponse<LiveData<List<Note>>> delegate, boolean loadDeletedNotes) {
        this.context = context;
        this.delegate = delegate;
        this.loadDeletedNotes = loadDeletedNotes;
    }

    @Override
    protected LiveData<List<Note>> doInBackground(Void... voids) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(context);
        NoteDAO noteDAO = noteDatabase.noteDAO();

        // Use the appropriate query based on loadDeletedNotes
        return loadDeletedNotes ? noteDAO.getDeletedNotesSortedByName() : noteDAO.getAllActiveNotesSortedByName();
    }

    @Override
    protected void onPostExecute(LiveData<List<Note>> notesLiveData) {
        if (delegate != null) {
            delegate.onAsyncTaskComplete(notesLiveData);
        }
    }
}






