package com.example.greenplate.model;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDB {
    private static FirebaseFirestore db;

    private FirebaseDB() { }

    public static FirebaseFirestore getInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }
}