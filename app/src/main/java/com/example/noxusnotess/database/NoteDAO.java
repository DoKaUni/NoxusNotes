package com.example.noxusnotess.database;

import com.example.noxusnotess.ui.Note;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDAO {
    @Insert
    void insert(Note note);

    @Query("SELECT * FROM Note ORDER BY title ASC")
    LiveData<List<Note>> getAllNotesSortedByName();

    // Add additional queries for sorting by date, etc.
}

