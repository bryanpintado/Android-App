package com.example.photosapp.model;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    private File file;
    private String caption;
    private LocalDateTime dateTime;
    private List<Tag> tags;

    public Photo(File file) {
        this.file = file;
        this.caption = "";
        this.tags = new ArrayList<>();
        this.dateTime = extractModifiedDate(file);
    }

    private LocalDateTime extractModifiedDate(File file) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    public File getFile() {
        return file;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Photo)) {
            return false;
        }
        Photo other = (Photo) o;
        return file.getAbsolutePath().equalsIgnoreCase(other.file.getAbsolutePath());
    }

    @Override
    public int hashCode() {
        return file.getAbsolutePath().toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return file.getName(); // always shows the filename, not the caption
    }

    public boolean addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            return true;
        }
        return false;
    }

    public boolean removeTag(Tag tag) {
        return tags.remove(tag);
    }

    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    public String getDateString() {
        return dateTime.toString();
    }

    public String getTagsString() {
        if (tags == null || tags.isEmpty()) {
            return "(No tags)";
        }
        List<String> tagStrings = new ArrayList<>();
        for (Tag tag : tags) {
            tagStrings.add(tag.getName() + "=" + tag.getValue());
        }
        return String.join(", ", tagStrings);
    }
    public LocalDateTime getModifiedDate() {
        return this.dateTime;
    }
}