package com.example.photosapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private List<Photo> photos;
    private LocalDateTime earliestDate;
    private LocalDateTime latestDate;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
        this.earliestDate = null;
        this.latestDate = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
        updateDateRange();
    }

    public boolean removePhoto(Photo photo) {
        boolean removed = photos.remove(photo);
        if (removed) {
            updateDateRange();
        }
        return removed;
    }

    private void updateDateRange() {
        if (photos.isEmpty()) {
            earliestDate = null;
            latestDate = null;
        } else {
            earliestDate = photos.get(0).getDateTime();
            latestDate = photos.get(0).getDateTime();

            for (Photo photo : photos) {
                if (photo.getDateTime().isBefore(earliestDate)) {
                    earliestDate = photo.getDateTime();
                }
                if (photo.getDateTime().isAfter(latestDate)) {
                    latestDate = photo.getDateTime();
                }
            }
        }
    }

    public LocalDateTime getEarliestDate() {
        return earliestDate;
    }

    public LocalDateTime getLatestDate() {
        return latestDate;
    }

    public int getPhotoCount() {
        return photos.size();
    }

    @Override
    public String toString() {
        return name + " (" + getPhotoCount() + " photos)";
    }
}