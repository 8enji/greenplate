package com.example.greenplate.model;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDB {
    private static FirebaseFirestore db;

    private FirebaseDB() { }

    public static FirebaseFirestore getInstance() {
        // so uh firebase uses singleton out of the box, not rly sure what the point
        // of this is
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }
}