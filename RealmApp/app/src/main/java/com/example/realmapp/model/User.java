package com.example.realmapp.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private String id; // Unique ID for the user
    private int imageResourceId;
    private String username;
    private String gender;
    private String email;
    private String age;
    private String birthdate;
    private String password;
    private RealmList<GymClass> bookedClasses; // List to store booked gym classes
    private RealmList<Trainer> bookedTrainer; // List to store booked trainers

    // Constructor
    public User() {
        // Initialize booked classes and trainers lists
        this.bookedClasses = new RealmList<>();
        this.bookedTrainer = new RealmList<>();
    }

    // Method to remove a booked class
    public void removeBookedClass(String classId) {
        if (classId == null || classId.isEmpty()) {
            throw new IllegalArgumentException("Class ID cannot be null or empty");
        }

        // Iterate through the list of booked classes
        for (int i = 0; i < bookedClasses.size(); i++) {
            // Get the booked class at the current position
            GymClass bookedClass = bookedClasses.get(i);
            // Check if the ID of the booked class matches the specified classId
            if (bookedClass.getId().equals(classId)) {
                // Remove the booked class from the list
                bookedClasses.remove(i);
                // Exit the loop after removing the class
                break;
            }
        }
    }

    public void removeBookedTrainer(String trainerId) {
        if (trainerId == null || trainerId.isEmpty()) {
            throw new IllegalArgumentException("Trainer ID cannot be null or empty");
        }

        // Iterate through the list of booked trainers
        for (int i = 0; i < bookedTrainer.size(); i++) {
            // Get the booked trainer at the current position
            Trainer trainer = bookedTrainer.get(i);
            // Check if the ID of the booked trainer matches the specified trainerId
            if (trainer.getId().equals(trainerId)) {
                // Remove the booked trainer from the list
                bookedTrainer.remove(i);
                // Exit the loop after removing the trainer
                break;
            }
        }
    }

    // Method to add a booked class
    public void addBookedClass(GymClass gymClass) {
        if (gymClass == null) {
            throw new IllegalArgumentException("GymClass cannot be null");
        }
        this.bookedClasses.add(gymClass);
    }

    // Method to add a booked trainer
    public void addBookedTrainer(Trainer trainer) {
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }
        this.bookedTrainer.add(trainer);
    }

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

    public RealmList<GymClass> getBookedClasses() {
        return bookedClasses;
    }

    public RealmList<Trainer> getBookedTrainer() {
        return bookedTrainer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
