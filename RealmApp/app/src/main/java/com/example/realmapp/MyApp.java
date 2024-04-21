package com.example.realmapp;

import android.app.Application;
import android.util.Log;

import com.example.realmapp.model.GymClass;
import com.example.realmapp.model.Trainer;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Realm
        Realm.init(this);

        // Setup Realm configuration with deleteRealmIfMigrationNeeded to handle schema changes automatically
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1) // You can increase this number if you make further schema changes
                .deleteRealmIfMigrationNeeded() // This will handle any schema changes by deleting the old database
                .build();

        Realm.setDefaultConfiguration(config);

        // Populate initial data
        populateInitialData();
    }

    private void populateInitialData() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(r -> {
            // Clear all existing data
            r.deleteAll();

            // Now repopulate the database
            GymClass holdClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            holdClass.setTitle("Dumbell Hold");
            holdClass.setDescription("Description for Dumbell Hold");
            holdClass.setTime("09:00 AM - 10:00 AM");
            holdClass.setImageResourceId(R.drawable.gym_dumbellhold);

            GymClass pushupClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            pushupClass.setTitle("Advanced Dumbell Pushups");
            pushupClass.setDescription("Description for Dumbell Pushups");
            pushupClass.setTime("09:00 AM - 12:00 AM");
            pushupClass.setImageResourceId(R.drawable.gym_pushup);

            GymClass ropeClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            ropeClass.setTitle("Battle Ropes");
            ropeClass.setDescription("Description for Battle Ropes class");
            ropeClass.setTime("11:00 AM - 12:00 PM");
            ropeClass.setImageResourceId(R.drawable.gym_rope);

            GymClass marathonClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            marathonClass.setTitle("Advanced Marathon");
            marathonClass.setDescription("Description for Marathon");
            marathonClass.setTime("11:00 AM - 12:00 PM");
            marathonClass.setImageResourceId(R.drawable.image2);

            Trainer john = r.createObject(Trainer.class, UUID.randomUUID().toString());
            john.setName("John Cena");
            john.setDescription("Description for John Cena");
            john.setImageResourceId(R.drawable.trainer_johncena);

            Trainer mickey = r.createObject(Trainer.class, UUID.randomUUID().toString());
            mickey.setName("Mickey Mouse");
            mickey.setDescription("Description for Mickey Mouse");
            mickey.setImageResourceId(R.drawable.trainer_mickey);

            Trainer trump = r.createObject(Trainer.class, UUID.randomUUID().toString());
            trump.setName("Donald Trump");
            trump.setDescription("Description for Donald Trump");
            trump.setImageResourceId(R.drawable.trainer_trump);
        }, () -> {
            realm.close();
            Log.i("Realm", "Initial data populated successfully!");
        }, error -> {
            realm.close();
            Log.e("Realm", "Error populating data: ", error);
        });
    }

}
