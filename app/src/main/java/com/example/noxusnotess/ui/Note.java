package com.example.noxusnotess.ui;

import java.text.DateFormat;
import java.util.Date;

// Class made for individual notes and their attributes with functions to get those attributes - DK
public class Note {
    private String title;
    private String content;
    private String createdDate;
    private String modifiedDate;
    private String encryptedFilePath;

    public Note(String title, String content) {
        this.title = title;
        this.content = content;

        Date currentDate = new Date();
        DateFormat dateFormat = DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(currentDate);

        this.createdDate = formattedDate;
        this.modifiedDate = formattedDate;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for content
    public String getContent() {
        return content;
    }

    // Setter for content
    public void setContent(String content) {
        this.content = content;
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