package com.example.noxusnotess.ui;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.util.Date;

import com.example.noxusnotess.utils.CryptoUtils;

@Entity
public class Note implements Parcelable {

    @PrimaryKey
    private int id;

    private String title;
    private boolean deleted;
    private String createdDate;
    private String modifiedDate;
    private String hashedPassword;
    private String encryptedFilePath;
    private boolean isPasswordLocked;

    public Note(String title) {
        this.title = title;
        this.deleted = false;
        this.createdDate = getCurrentDate();
        this.modifiedDate = createdDate;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        createdDate = in.readString();
        modifiedDate = in.readString();
        encryptedFilePath = in.readString();
        isPasswordLocked = in.readByte() != 0;
        hashedPassword = in.readString();
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    public void setEncryptedFilePath(String encryptedFilePath) {this.encryptedFilePath = encryptedFilePath;}

    public void setPasswordLocked(boolean isPasswordLocked){this.isPasswordLocked = isPasswordLocked;}


    public boolean isPasswordLocked(){return isPasswordLocked;}

    public void setHashedPassword(String hashedPassword){this.hashedPassword = hashedPassword;}

    public String getHashedPassword(){return hashedPassword;}

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
        dest.writeByte((byte) (isPasswordLocked ? 1 : 0)); // Write boolean value
        dest.writeString(hashedPassword);
    }
}

