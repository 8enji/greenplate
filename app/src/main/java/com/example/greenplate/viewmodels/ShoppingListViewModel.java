package com.example.greenplate.viewmodels;

import com.example.greenplate.model.FirebaseDB;
import com.example.greenplate.model.Ingredient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ShoppingListViewModel {
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;

    public ShoppingListViewModel() {
        db = FirebaseDB.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        userRef = db.collection("users").document(email);
    }

    public void addIngredient(String ingredientName, String quantity, IngredientUpdateCallback callback) {

        if (ingredientName.isEmpty() || quantity.isEmpty()) {
            //null checking
            callback.onFailure("Ingredient Name, Quantity, and Units are required");
            return;
        }

        String[] parts = quantity.split(" ");
        double quantityDouble = Double.parseDouble(parts[0]);
        if (quantityDouble < 0) {
            callback.onFailure("Quantity cannot be negative");
            return;
        }
        String unit = parts[1];
        if (unit.isEmpty()) {
            callback.onFailure(("Please enter a unit"));
            return;
        }
        Ingredient ingredient = new Ingredient(ingredientName, quantityDouble, unit, 0, "");

        userRef.collection("list").whereEqualTo("name", ingredientName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Ingredient already exists
                        updateIngredientQuantity(ingredient, quantityDouble, callback);
                    } else {
                        // Ingredient does not exist, proceed to add
                        Map<String, Object> ingredientMap = new HashMap<>();
                        ingredientMap.put("name", ingredientName);
                        ingredientMap.put("quantity", quantityDouble);
                        ingredientMap.put("units", unit);
                        ingredientMap.put("calories", 0);
                        userRef.collection("list").document(ingredientName)
                                .set(ingredientMap)
                                .addOnSuccessListener(documentReference -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure("Failed to add ingredient: " + e.getMessage()));

                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Failed to check if ingredient exists: " + e.getMessage()));

    }

    public void buyIngredient() {

    }

    private void updateIngredientQuantity(Ingredient ingredient, Double newQuantity, IngredientUpdateCallback callback) {
        if (newQuantity <= 0) {
            // Remove the ingredient if its quantity becomes 0 or less
            removeIngredient(ingredient, callback);
        } else {
            // Update the ingredient quantity in the database
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("quantity", newQuantity);
            userRef.collection("pantry").document(ingredient.getName())
                    .update(updateData)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully updated quantity
                        callback.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update quantity
                        callback.onFailure("Failed to update quantity");
                    });
        }
    }

    private void removeIngredient(Ingredient ingredient, IngredientUpdateCallback callback) {

    }

    public interface addIngredientCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface IngredientUpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
