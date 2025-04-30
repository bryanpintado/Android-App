package com.example.photosapp.model;

import java.io.Serializable;
import java.util.Objects;

public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String value;

    public Tag(String name, String value) {
        this.type = name.trim().toLowerCase();
        this.value = value.trim().toLowerCase();
    }

    public String getName() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Tag))
            return false;
        Tag other = (Tag) o;
        return type.equals(other.type) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return type + "=" + value;
    }

    public String getType() {
        return type;
    }

}