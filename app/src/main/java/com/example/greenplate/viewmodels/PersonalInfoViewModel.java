package com.example.greenplate.viewmodels;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.FirebaseDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PersonalInfoViewModel extends ViewModel {
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;

    public PersonalInfoViewModel() {
        db = FirebaseDB.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        userRef = db.collection("users").document(email);
    }

    public void savePersonalInfo(String height, String weight, String gender, AuthCallback callback) {
        if (height.isEmpty() || weight.isEmpty() || gender.isEmpty()) {
            //null checking
            callback.onFailure("All fields must be non-empty");
            return;
        }

        try {
            // Attempt to parse the calories string to an integer
            Map<String, Object> personalInfo = new HashMap<>();
            personalInfo.put("height", height);
            personalInfo.put("weight", weight);
            personalInfo.put("gender", gender);

            userRef
                    .update(personalInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void v) {
                            callback.onSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onFailure("Failed to save personal info: " + e.getMessage()); // Notify failure
                        }
                    });



        } catch (Exception e) {
            // Parse Fails
            callback.onFailure("Uncaught exception occurred");
            return;
        }
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}

