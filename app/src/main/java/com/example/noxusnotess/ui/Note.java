package com.example.noxusnotess.ui;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.util.Date;

@Entity
public class Note implements Parcelable {

    @PrimaryKey
    private int id;

    private String title;
    private String createdDate;
    private String modifiedDate;
    private String encryptedFilePath;

    public Note(String title) {
        this.title = title;
        this.createdDate = getCurrentDate();
        this.modifiedDate = createdDate;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        createdDate = in.readString();
        modifiedDate = in.readString();
        encryptedFilePath = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getEncryptedFilePath() {
        return encryptedFilePath;
    }

    public void setEncryptedFilePath(String encryptedFilePath) {
        this.encryptedFilePath = encryptedFilePath;
    }

    public String getCurrentDate() {
        Date currentDate = new Date();
        DateFormat dateFormat = DateFormat.getDateInstance();
        return dateFormat.format(currentDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(createdDate);
        dest.writeString(modifiedDate);
        dest.writeString(encryptedFilePath);
    }
}

