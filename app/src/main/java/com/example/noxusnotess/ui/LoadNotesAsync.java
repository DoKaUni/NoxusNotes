package com.example.noxusnotess.ui;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.noxusnotess.ui.Note;
import com.example.noxusnotess.database.NoteDAO;
import com.example.noxusnotess.database.NoteDatabase;

import java.util.List;

public class LoadNotesAsync extends AsyncTask<Void, Void, LiveData<List<Note>>> {

    private final Context context;
    private final AsyncResponse<LiveData<List<Note>>> delegate;

    public interface AsyncResponse<T> {
        void onAsyncTaskComplete(T result);
    }

    public LoadNotesAsync(Context context, AsyncResponse<LiveData<List<Note>>> delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected LiveData<List<Note>> doInBackground(Void... voids) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(context);
        NoteDAO noteDAO = noteDatabase.noteDAO();
        return noteDAO.getAllNotesSortedByName();
    }

    @Override
    protected void onPostExecute(LiveData<List<Note>> notesLiveData) {
        if (delegate != null) {
            delegate.onAsyncTaskComplete(notesLiveData);
        }
    }
}





