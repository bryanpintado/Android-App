package com.example.photosapp.model;

import android.net.Uri;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileUri; // Store the URI as a String
    private String caption;
    private ArrayList<Tag> tags;
//    private LocalDateTime dateTime;

    public Photo(Uri uri) {
        this.fileUri = uri.toString();
        this.caption = "";
        this.tags = new ArrayList<>();
//        this.dateTime = LocalDateTime.now();
    }

    public String getFileUri() {
        return fileUri;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

//    public LocalDateTime getDateTime() {
//        return dateTime;
//    }
}