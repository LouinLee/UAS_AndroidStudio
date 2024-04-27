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
            holdClass.setDescription("Regular");
            holdClass.setTime("09:00 AM - 10:00 AM");
            holdClass.setPrice("Rp 200,000");
            holdClass.setImageResourceId(R.drawable.gym_dumbellhold);

            GymClass yogaClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            yogaClass.setTitle("Yoga Class");
            yogaClass.setDescription("Regular");
            yogaClass.setTime("10:00 AM - 12:00 AM");
            yogaClass.setPrice("Rp 100,000");
            yogaClass.setImageResourceId(R.drawable.gym_yoga);

            GymClass pushupClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            pushupClass.setTitle("Dumbell Pushups");
            pushupClass.setDescription("Advanced");
            pushupClass.setTime("08:00 AM - 10:00 AM");
            pushupClass.setPrice("Rp 250,000");
            pushupClass.setImageResourceId(R.drawable.gym_pushup);

            GymClass ropeClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            ropeClass.setTitle("Battle Ropes");
            ropeClass.setDescription("Beginner");
            ropeClass.setTime("15:00 PM - 17:00 PM");
            ropeClass.setPrice("Rp 150,000");
            ropeClass.setImageResourceId(R.drawable.gym_rope);

            GymClass zumbaClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            zumbaClass.setTitle("Advanced Marathon");
            zumbaClass.setDescription("Description for Marathon");
            zumbaClass.setTime("11:00 AM - 12:00 PM");
            zumbaClass.setPrice("Rp 100,000");
            zumbaClass.setImageResourceId(R.drawable.gym_zumba);

            Trainer tate = r.createObject(Trainer.class, UUID.randomUUID().toString());
            tate.setName("Andrew Tate");
            tate.setRating("5/5");
            tate.setExperience("10 Years");
            tate.setPrice("Rp 500,000");
            tate.setImageResourceId(R.drawable.trainer_tate);

            Trainer musk = r.createObject(Trainer.class, UUID.randomUUID().toString());
            musk.setName("Elon Musk");
            musk.setRating("4/5");
            musk.setExperience("2 Years");
            musk.setPrice("Rp 200,000");
            musk.setImageResourceId(R.drawable.trainer_musk);

            Trainer cena = r.createObject(Trainer.class, UUID.randomUUID().toString());
            cena.setName("John Cena");
            cena.setRating("3/5");
            cena.setExperience("5 Years");
            cena.setPrice("Rp 300,000");
            cena.setImageResourceId(R.drawable.trainer_johncena);

            Trainer eminem = r.createObject(Trainer.class, UUID.randomUUID().toString());
            eminem.setName("Eminem");
            eminem.setRating("1/5");
            eminem.setExperience("1 Years");
            eminem.setPrice("Rp 50,000");
            eminem.setImageResourceId(R.drawable.trainer_eminem);

            Trainer kanye = r.createObject(Trainer.class, UUID.randomUUID().toString());
            kanye.setName("Kanye West");
            kanye.setRating("3/5");
            kanye.setExperience("5 Years");
            kanye.setPrice("Rp 1,000,000");
            kanye.setImageResourceId(R.drawable.trainer_kanye);
        }, () -> {
            realm.close();
            Log.i("Realm", "Initial data populated successfully!");
        }, error -> {
            realm.close();
            Log.e("Realm", "Error populating data: ", error);
        });
    }

}
