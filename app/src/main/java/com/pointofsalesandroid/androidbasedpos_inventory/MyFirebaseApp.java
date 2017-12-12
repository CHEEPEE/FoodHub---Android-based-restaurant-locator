package com.pointofsalesandroid.androidbasedpos_inventory;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Keji's Lab on 09/12/2017.
 */

public class MyFirebaseApp extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
