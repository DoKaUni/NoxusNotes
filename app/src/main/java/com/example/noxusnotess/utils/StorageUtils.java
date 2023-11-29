package com.example.noxusnotess.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.example.noxusnotess.ui.Note;
import com.example.noxusnotess.database.NoteDatabase;

public class StorageUtils {

    public static void saveNote(Note note, String noteContent, Context context) {
        note.setId(generateUniqueKey());

        // Save encrypted content to file
        String encryptedFilePath = CryptoUtils.encryptData(noteContent, String.valueOf(note.getId()), context);
        note.setEncryptedFilePath(encryptedFilePath);

        // Save other note information to database asynchronously
        new SaveNoteTask(context).execute(note);
    }

    public static int generateUniqueKey() {
        // Get the current timestamp
        long timestamp = System.currentTimeMillis();

        // Generate a random number
        int random = (int) (Math.random() * 1000000);

        // Combine timestamp and random number to create a unique key
        return (int) (timestamp + random);
    }

    private static class SaveNoteTask extends AsyncTask<Note, Void, Void> {
        private Context context;

        SaveNoteTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            if (notes != null && notes.length > 0) {
                NoteDatabase noteDatabase = NoteDatabase.getInstance(context);
                noteDatabase.noteDAO().insert(notes[0]);
            }
            return null;
        }
    }
}


