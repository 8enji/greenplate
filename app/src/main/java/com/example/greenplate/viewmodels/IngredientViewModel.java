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
            String calories, AuthCallback callback) {

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
                        }
                    })
                    .addOnFailureListener(e -> callback.onFailure("Failed to check if ingredient exists: " + e.getMessage()));

        } catch (NumberFormatException e) {
            // Parse Fails
            callback.onFailure("Calories must be a valid number");
        }
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface GetPantryCallBack {
        void onSuccess(ArrayList<Ingredient> pantry);
        void onFailure(String error);
    }

    public static String validateIngredientInput(String ingredientName, String quantity, String calories) {
        if (ingredientName.isEmpty() || calories.isEmpty() || quantity.isEmpty()) {
            return "Ingredient Name, Quantity, and Calories are required";
        }
        if (calories.contains(" ")) {
            return "Calories cannot contain whitespace";
        }
        if (calories.contains("-")) {
            return "Calories cannot be negative";
        }
        try {
            double caloriesDouble = Double.parseDouble(calories);
            String[] parts = quantity.split(" ");
            double quantityDouble = Double.parseDouble(parts[0]);
            if (quantityDouble < 0) {
                return "Quantity cannot be negative";
            }
            return null;
        } catch (NumberFormatException e) {
            return "Calories must be a valid number";
        }
    }
}
