package com.example.noxusnotess.database;

import com.example.noxusnotess.ui.Note;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDAO {
    @Insert
    void insert(Note note);

    @Query("SELECT * FROM Note WHERE deleted = 0 ORDER BY title ASC")
    LiveData<List<Note>> getAllActiveNotesSortedByName();

    @Query("SELECT * FROM Note WHERE deleted = 1 ORDER BY title ASC")
    LiveData<List<Note>> getDeletedNotesSortedByName();

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM Note WHERE deleted = 1 AND strftime('%s', 'now') - strftime('%s', modifiedDate) > :deleteAfterSeconds")
    void deleteExpiredNotes(long deleteAfterSeconds);
}

