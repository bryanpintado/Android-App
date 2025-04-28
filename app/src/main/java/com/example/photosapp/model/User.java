package com.example.photosapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Album> albums = new ArrayList<>();

    private String username;

    public User(String username) {
        this.username = username.toLowerCase();
    }

    @Override
    public String toString() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void addAlbum(Album album) {
        if (albums == null) {
            albums = new ArrayList<>();
        }
        albums.add(album);
    }
}