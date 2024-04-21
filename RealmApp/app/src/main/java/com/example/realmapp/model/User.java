/*
 * User.java
 * This class represents a user and extends RealmObject for Realm database functionality.
 */

package com.example.realmapp.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    private String id; // Unique ID for the user
    private String username;
    private String password;
    private RealmList<GymClass> bookedClasses; // List to store booked gym classes
    private RealmList<Trainer> bookedTrainer; // List to store booked gym classes

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addBookedClass(GymClass gymClass) {
        this.bookedClasses.add(gymClass);
    }

    public RealmList<GymClass> getBookedClasses() {
        return bookedClasses;
    }

    public void addBookedTrainer(Trainer trainer) {
        this.bookedTrainer.add(trainer);
    }

    public RealmList<Trainer> getBookedTrainer() {
        return bookedTrainer;
    }
}
