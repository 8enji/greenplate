package com.example.greenplate.viewmodels;

import com.example.greenplate.model.FirebaseDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class IngredientViewModel extends ViewModel {
        private FirebaseFirestore db;
        private DocumentReference userRef;
        private FirebaseAuth mAuth;

        public IngredientViewModel() {
            db = FirebaseDB.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String email = currentUser.getEmail();
            userRef = db.collection("users").document(email);
        }
    public void createIngredient(
            String ingredientName, String quantity, String units,
            String calories, IngredientViewModel.AuthCallback callback) {

        if (ingredientName.isEmpty() || calories.isEmpty() || units.isEmpty() || quantity.isEmpty()) {
            //null checking
            callback.onFailure("Ingredient Name, Quantity, Units, and Calories are required");
            return;
        }
        if (calories.contains(" ")) {
            //calories will not parse to an int
            callback.onFailure("Calories cannot contain whitespace");
            return;
        }
        if (calories.contains("-")) {
            //calories cannot be negative
            callback.onFailure("Calories cannot be negative");
            return;
        }
        try {
            // Attempt to parse the calories string to an integer
            int caloriesInt = Integer.parseInt(calories);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(" HH:mm");
            LocalDateTime now = LocalDateTime.now();
            String time = dtf.format(now);

            Map<String, Object> ingredient = new HashMap<>();
            ingredient.put("name", ingredientName);
            ingredient.put("calories", caloriesInt);
            ingredient.put("time", time);

            userRef.collection("pantry")
                    .add(ingredient)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Success
                            callback.onSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failure
                            callback.onFailure("Failed to add ingredient: " + e.getMessage());
                        }
                    });

        } catch (NumberFormatException e) {
            // Parse Fails
            callback.onFailure("Calories must be a valid number");
            return;
        }
    }
    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
