package com.example.realmapp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Trainer extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String description;
    private int imageResourceId;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

}
