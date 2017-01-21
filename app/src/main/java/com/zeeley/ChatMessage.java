package com.zeeley;

import android.graphics.Bitmap;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by gannu on 16-06-2016.
 */
@Parcel
public class ChatMessage {
    public String message;
    public String Time;
    public Boolean isMine;
    public String date;
    public long id;
    public Bitmap image;
    public boolean isDate=false,isSent=true;
    public boolean isImage=false;

    public ChatMessage(String messageString, Boolean isMINE) {

        message = messageString;
        isMine = isMINE;
        this.isDate = isDate;
        this.isImage = isImage;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }

    public void setIsDate(boolean isDate) {
        this.isDate = isDate;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isDate() {
        return isDate;
    }

    public boolean isImage() {
        return isImage;
    }

    public ChatMessage() {

    }

    public long getId() {
        return this.id;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return Time;
    }

    public Boolean isMine() {
        return isMine;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.Time = time;
    }
}
