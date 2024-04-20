package com.example.greenplate.viewmodels;

import android.widget.Toast;

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
import java.util.ArrayList;
import com.example.greenplate.model.Ingredient;
import com.example.greenplate.model.Recipe;

import org.w3c.dom.Document;

public class RecipeDetailsScreenViewModel extends ViewModel {
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;

    public RecipeDetailsScreenViewModel() {
        db = FirebaseDB.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        userRef = db.collection("users").document(email);
    }

    public void loadRecipe(String recipeNameString, LoadRecipeCallback callback) {
        db.collection("cookbook").document(recipeNameString).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot recipeDocument = task.getResult();
                Map<String, Object> r = recipeDocument.getData();
                ArrayList<HashMap<String, Object>> rI = (ArrayList<HashMap<String, Object>>) r.get("ingredients");
                ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
                for (int i = 0; i < rI.size(); i++) {
                    Ingredient ingr = new Ingredient(rI.get(i).get("name").toString(), (double) rI.get(i).get("quantity"), rI.get(i).get("units").toString(), (double) rI.get(i).get("calories"));
                    ingredients.add(ingr);
                }
                callback.onSuccess(ingredients);

            } else {
                callback.onFailure("recipe cannot be accessed");
            }
        });
    }

    public void getPantry(GetPantryCallBack callback) {
        //get the pantry:
        HashMap<String, Ingredient> pantry = new HashMap<String, Ingredient>();
        userRef.collection("pantry").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot ingredientDocuments = task.getResult();
                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Object> i = document.getData();
                    Ingredient ingre = new Ingredient(i.get("name").toString(), (double) i.get("quantity"), i.get("units").toString(), (double) i.get("calories"));
                    pantry.put(ingre.getName(), ingre);
                }
                callback.onSuccess(pantry);
            } else {
                callback.onFailure("Failed to access pantry: " + task.getException().getMessage());
            }
        }).addOnFailureListener(e -> System.out.println("Failed with exception: " + e.getMessage()));
    }

    public void removeIngredients(HashMap<String, Ingredient> pantry, ArrayList<Ingredient> ingredients, PantryCallback callback) {

        //for each ingredient that we are cooking:
        int totalCalories = 0;
        for (Ingredient i: ingredients) {
            totalCalories += (double) pantry.get(i.getName()).getCalories();
            double newQuantity = Math.max(0, pantry.get(i.getName()).getQuantity() - i.getQuantity());
            if (newQuantity <= 0) {
                // Remove the ingredient if its quantity becomes 0 or less
                userRef.collection("pantry").document(i.getName())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Successfully removed ingredient
                        })
                        .addOnFailureListener(e -> {
                                callback.onFailure("Failed to remove an ingredient: "
                                        + e.getMessage());
                        });
            } else {
                // Update the ingredient quantity in the database
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("quantity", newQuantity);
                userRef.collection("pantry").document(i.getName())
                        .update(updateData)
                        .addOnSuccessListener(aVoid -> {
                            // Successfully updated quantity
                        })
                        .addOnFailureListener(e -> {
                            // Failed to update quantity
                            callback.onFailure("Failed to update ingredient quantity: " + e.getMessage());
                        });
            }
        }
        callback.onSuccess(totalCalories);
    }

    public static Map<String, Double> processIngredients(HashMap<String, Ingredient> pantry, ArrayList<Ingredient> ingredients) {
        Map<String, Double> updatedQuantities = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            if (pantry.containsKey(ingredient.getName())) {
                Ingredient pantryIngredient = pantry.get(ingredient.getName());
                double newQuantity = Math.max(0, pantryIngredient.getQuantity() - ingredient.getQuantity());
                updatedQuantities.put(ingredient.getName(), newQuantity);
            } else {
                updatedQuantities.put(ingredient.getName(), -1.0);  // -1 to indicate missing ingredient
            }
        }
        return updatedQuantities;
    }


    public interface LoadRecipeCallback {
        void onSuccess(ArrayList<Ingredient> ingredients);
        void onFailure(String error);
    }

    public interface PantryCallback {
        void onSuccess(int totalCalories);
        void onFailure(String error);
    }

    public interface GetPantryCallBack {
        void onSuccess(HashMap<String, Ingredient> pantry);
        void onFailure(String error);
    }

}
