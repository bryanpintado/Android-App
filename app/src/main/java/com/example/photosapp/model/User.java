package com.example.photosapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

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
    public Album getAlbumByName(String name) {
        if (albums != null) {
            for (Album album : albums) {
                if (album.getName().equalsIgnoreCase(name)) {
                    return album;
                }
            }
        }
        return null;
    }
    public boolean removeAlbumByName(String name) {
        Iterator<Album> iter = albums.iterator();
        while (iter.hasNext()) {
            Album a = iter.next();
            if (a.getName().equalsIgnoreCase(name)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }
}