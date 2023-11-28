package com.example.noxusnotess.utils;

import android.content.Context;

import com.example.noxusnotess.ui.Note;

import java.io.FileWriter;
import java.io.IOException;

public class StorageUtils {

    public static void saveNote(Note note, Context context) {
        // Replace whitespace with underscores in the note title
        String sanitizedTitle = note.getTitle().replaceAll("\\s", "_");

        // Save encrypted content to file
        String encryptedFilePath = CryptoUtils.encryptData(note.getContent(), sanitizedTitle, context);
        note.setEncryptedFilePath(encryptedFilePath);
    }
}
