package com.example.greenplate.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.FirebaseDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountCreateViewModel extends ViewModel {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public AccountCreateViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDB.getInstance();
    }

    public void createUser(String email, String password, AuthCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onFailure("Email and password are required");
            return;
        }
        if (password.contains(" ")) {
            // Password contains whitespace
            callback.onFailure("Password cannot contain whitespace");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Account creation successful
                        callback.onSuccess();
                    } else {
                        // Account creation failed
                        callback.onFailure(task.getException().getMessage());
                    }
                });
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);

        db.collection("users").document(email)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        callback.onSuccess(); // Notify success
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure("Failed to add meal: " + e.getMessage()); // Notify failure
                    }
                });
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}

