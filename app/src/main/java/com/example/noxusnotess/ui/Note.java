package com.example.noxusnotess.ui;

import java.text.DateFormat;
import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note {
    @PrimaryKey
    private int id;

    private String title;
    private String createdDate;
    private String modifiedDate;
    private String encryptedFilePath;

    public Note(String title) {
        this.title = title;

        Date currentDate = new Date();
        DateFormat dateFormat = DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(currentDate);

        this.createdDate = formattedDate;
        this.modifiedDate = formattedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for createdDate
    public String getCreatedDate() {
        return createdDate;
    }

    // Setter for createdDate
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    // Getter for modifiedDate
    public String getModifiedDate() {
        return modifiedDate;
    }

    // Setter for modifiedDate
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getEncryptedFilePath() {
        return encryptedFilePath;
    }

    public void setEncryptedFilePath(String encryptedFilePath) {
        this.encryptedFilePath = encryptedFilePath;
    }
}
