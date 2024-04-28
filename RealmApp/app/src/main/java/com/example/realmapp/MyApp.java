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
            holdClass.setPrice(70000);
            holdClass.setJoined(10);
            holdClass.setMaximum(11);
            holdClass.setDifficulty("Regular");
            holdClass.setImageResourceId(R.drawable.gym_dumbellhold);

            GymClass yogaClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            yogaClass.setTitle("Yoga Class");
            yogaClass.setDescription("Description for Yoga Class");
            yogaClass.setTime("10:00 AM - 12:00 AM");
            yogaClass.setPrice(100000);
            yogaClass.setJoined(10);
            yogaClass.setMaximum(11);
            yogaClass.setDifficulty("Regular");
            yogaClass.setImageResourceId(R.drawable.gym_yoga);

            GymClass pushupClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            pushupClass.setTitle("Dumbell Pushups");
            pushupClass.setDescription("Description for Dumbell Pushups");
            pushupClass.setTime("08:00 AM - 10:00 AM");
            pushupClass.setPrice(250000);
            pushupClass.setJoined(12);
            pushupClass.setMaximum(20);
            pushupClass.setDifficulty("Advanced");
            pushupClass.setImageResourceId(R.drawable.gym_pushup);

            GymClass ropeClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            ropeClass.setTitle("Battle Ropes");
            ropeClass.setDescription("Description for Battle Ropes class");
            ropeClass.setTime("15:00 PM - 17:00 PM");
            ropeClass.setPrice(150000);
            ropeClass.setJoined(10);
            ropeClass.setMaximum(10);
            ropeClass.setDifficulty("Beginner");
            ropeClass.setImageResourceId(R.drawable.gym_rope);

            GymClass zumbaClass = r.createObject(GymClass.class, UUID.randomUUID().toString());
            zumbaClass.setTitle("Advanced Marathon");
            zumbaClass.setDescription("Description for Marathon");
            zumbaClass.setTime("11:00 AM - 12:00 PM");
            zumbaClass.setPrice(100000);
            zumbaClass.setJoined(8);
            zumbaClass.setMaximum(10);
            zumbaClass.setDifficulty("Advanced");
            zumbaClass.setImageResourceId(R.drawable.gym_zumba);

            Trainer tate = r.createObject(Trainer.class, UUID.randomUUID().toString());
            tate.setDescription("Description for Tate");
            tate.setName("Andrew Tate");
            tate.setRating("5/5");
            tate.setExperience("10 Years");
            tate.setPrice(80000);
            tate.setClient("110 People");
            tate.setStatus("Online");
            tate.setImageResourceId(R.drawable.trainer_tate);

            Trainer musk = r.createObject(Trainer.class, UUID.randomUUID().toString());
            musk.setDescription("Description for Musk");
            musk.setName("Elon Musk");
            musk.setRating("4/5");
            musk.setExperience("2 Years");
            musk.setPrice(100000);
            musk.setClient("18 People");
            musk.setStatus("Online");
            musk.setImageResourceId(R.drawable.trainer_musk);

            Trainer cena = r.createObject(Trainer.class, UUID.randomUUID().toString());
            cena.setDescription("Description for Cena");
            cena.setName("John Cena");
            cena.setRating("3/5");
            cena.setExperience("5 Years");
            cena.setPrice(70000);
            cena.setClient("22 People");
            cena.setStatus("Online");
            cena.setImageResourceId(R.drawable.trainer_johncena);

            Trainer eminem = r.createObject(Trainer.class, UUID.randomUUID().toString());
            eminem.setDescription("Description for Eminem");
            eminem.setName("Eminem");
            eminem.setRating("1/5");
            eminem.setExperience("1 Years");
            eminem.setPrice(50000);
            eminem.setClient("31 People");
            eminem.setStatus("Online");
            eminem.setImageResourceId(R.drawable.trainer_eminem);

            Trainer kanye = r.createObject(Trainer.class, UUID.randomUUID().toString());
            kanye.setDescription("Description for Kanye");
            kanye.setName("Kanye West");
            kanye.setRating("3/5");
            kanye.setExperience("5 Years");
            kanye.setPrice(110000);
            kanye.setClient("26 People");
            kanye.setStatus("Online");
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
