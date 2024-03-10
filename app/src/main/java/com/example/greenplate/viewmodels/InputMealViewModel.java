package com.example.greenplate.viewmodels;

import com.example.greenplate.model.FirebaseDB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class InputMealViewModel extends ViewModel {
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;

    public InputMealViewModel() {
        db = FirebaseDB.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        userRef = db.collection("Users").document(email);
    }

    public void createMeal(String mealName, String calories, AuthCallback callback) {
        if (mealName.isEmpty() || calories.isEmpty()) {
            //null checking
            callback.onFailure("Meal Name and Calories are required.");
            return;
        }

        if (calories.contains(" ")) {
            //calories will not parse to an int
            callback.onFailure("Calories cannot contain whitespace. Please try again.");
            return;
        }
        if (calories.contains("-")) {
            //calories cannot be negative
            callback.onFailure("Calories cannot be negative. Please try again.");
            return;
        }
        try {
            // Attempt to parse the calories string to an integer
            int caloriesInt = Integer.parseInt(calories);
            Map<String, Object> meal = new HashMap<>();
            meal.put("Name: ", mealName);
            meal.put("Calories: ", caloriesInt);

            userRef.collection("Meals").document(mealName)
                    .set(meal)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void v) {
                            callback.onSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onFailure("Failed to Add meal: " + e.getMessage()); // Notify failure
                        }
                    });



        } catch (NumberFormatException e) {
            // Parse Fails
            callback.onFailure("Calories must be a valid number.");
            return;
        }
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
