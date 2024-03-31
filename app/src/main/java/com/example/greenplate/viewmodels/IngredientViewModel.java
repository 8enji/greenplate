package com.example.greenplate.viewmodels;

import com.example.greenplate.model.FirebaseDB;
import com.example.greenplate.model.Ingredient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void getPantry(IngredientViewModel.GetPantryCallBack callback) {
        ArrayList<Ingredient> pantry = new ArrayList<Ingredient>();
        userRef.collection("pantry").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot ingredientDocuments = task.getResult();
                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Object> i = document.getData();
                    Ingredient ingre = new Ingredient(i.get("name").toString(), (double) i.get("quantity"), i.get("units").toString(), (double) i.get("calories"));
                    pantry.add(ingre);
                }
                callback.onSuccess(pantry);
            } else {
                callback.onFailure("Failed to access pantry: " + task.getException().getMessage());
            }
        }).addOnFailureListener(e -> System.out.println("Failed with exception: " + e.getMessage()));
    }


    public void createIngredient(
            String ingredientName, String quantity,
            String calories, String expirationdate, AuthCallback callback) {

        if (ingredientName.isEmpty() || calories.isEmpty() || quantity.isEmpty()) {
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
            double caloriesDouble = Double.parseDouble(calories);
            String[] parts = quantity.split(" ");
            double quantityDouble = Double.parseDouble(parts[0]);
            if (quantityDouble < 0) {
                callback.onFailure("Quantity cannot be negative");
                return;
            }
            String unit = parts[1];

            userRef.collection("pantry").whereEqualTo("name", ingredientName).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Ingredient already exists
                            callback.onFailure("Ingredient already exists in the pantry");
                        } else {
                            // Ingredient does not exist, proceed to add
                            if (expirationdate.isEmpty()) {
                                Ingredient ingredient = new Ingredient(ingredientName, quantityDouble, unit, caloriesDouble);
                                Map<String, Object> ingredientMap = new HashMap<>();
                                ingredientMap.put("name", ingredientName);
                                ingredientMap.put("quantity", quantityDouble);
                                ingredientMap.put("units", unit);
                                ingredientMap.put("calories", caloriesDouble);
                                userRef.collection("pantry").document(ingredient.getName())
                                        .set(ingredientMap)
                                        .addOnSuccessListener(documentReference -> callback.onSuccess())
                                        .addOnFailureListener(e -> callback.onFailure("Failed to add ingredient: " + e.getMessage()));
                            } else {
                                Ingredient ingredient = new Ingredient(ingredientName, quantityDouble, unit, caloriesDouble, expirationdate);
                                Map<String, Object> ingredientMap = new HashMap<>();
                                ingredientMap.put("name", ingredientName);
                                ingredientMap.put("quantity", quantityDouble);
                                ingredientMap.put("units", unit);
                                ingredientMap.put("calories", caloriesDouble);
                                ingredientMap.put("expiration date", expirationdate);
                                userRef.collection("pantry").document(ingredient.getName())
                                        .set(ingredientMap)
                                        .addOnSuccessListener(documentReference -> callback.onSuccess())
                                        .addOnFailureListener(e -> callback.onFailure("Failed to add ingredient: " + e.getMessage()));
                            }
                        }
                    })
                    .addOnFailureListener(e -> callback.onFailure("Failed to check if ingredient exists: " + e.getMessage()));

        } catch (NumberFormatException e) {
            // Parse Fails
            callback.onFailure("Calories must be a valid number");
        }
    }

    public void increaseIngredientQuantity(Ingredient ingredient, IngredientUpdateCallback callback) {
        double newQuantity = ingredient.getQuantity() + 1.0;
        updateIngredientQuantity(ingredient, newQuantity, callback);
    }

    public void decreaseIngredientQuantity(Ingredient ingredient, IngredientUpdateCallback callback) {
        double newQuantity = Math.max(0, ingredient.getQuantity() - 1.0);
        updateIngredientQuantity(ingredient, newQuantity, callback);
    }

    private void updateIngredientQuantity(Ingredient ingredient, double newQuantity, IngredientUpdateCallback callback) {
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
                        callback.onIngredientUpdated(true, "Successful update");
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update quantity
                        callback.onIngredientUpdated(false, "Failed to update quantity");
                    });
        }
    }

    private void removeIngredient(Ingredient ingredient, IngredientUpdateCallback callback) {
        userRef.collection("pantry").document(ingredient.getName())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully removed ingredient
                    callback.onIngredientUpdated(true, "Successful removal");
                })
                .addOnFailureListener(e ->
                        callback.onIngredientUpdated(false, "Failed to check if ingredient exists: "
                                + e.getMessage()));
    }
    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface GetPantryCallBack {
        void onSuccess(ArrayList<Ingredient> pantry);
        void onFailure(String error);
    }

    public interface IngredientUpdateCallback {
        void onIngredientUpdated(boolean success, String message);
    }
}
