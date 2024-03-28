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
import java.util.ArrayList;
import com.example.greenplate.model.Ingredient;
import com.example.greenplate.model.Recipe;

public class RecipeScreenViewModel extends ViewModel {
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;

    public RecipeScreenViewModel() {
        db = FirebaseDB.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        userRef = db.collection("users").document(email);
    }

    public void createRecipe(String recipeName, String inputDetails, AuthCallback callback) {
        if (recipeName.isEmpty() || inputDetails.isEmpty()) {
            //null checking
            callback.onFailure("Recipe Name and Ingredients List are required");
            return;
        }

        if (inputDetails.contains("-")) {
            //negative quantities not allowed
            callback.onFailure("Quantities cannot be negative");
            return;
        }

        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        try {
            //Attempt to parse the Input Details to an ArrayList of Ingredients
            String currString = inputDetails;
            String currentIngredient;
            boolean lastIngredient = false;
            while (!lastIngredient) {
                int indexComma = currString.indexOf(',');
                if (indexComma == -1) {
                    //last ingredient, set indexComma to last index instead
                    indexComma = currString.length();
                    lastIngredient = true;
                }
                currentIngredient = currString.substring(0,indexComma);
                int index = currentIngredient.lastIndexOf(' ');
                String unit = currentIngredient.substring(index + 1);
                if (!((unit.equalsIgnoreCase("cup")) || (unit.equalsIgnoreCase("cups"))
                || (unit.equalsIgnoreCase("tbsp")) || (unit.equalsIgnoreCase("tbsps"))
                || (unit.equalsIgnoreCase("tsp")) || (unit.equalsIgnoreCase("tsps"))
                || (unit.equalsIgnoreCase("oz")))) {
                    //invalid unit
                    callback.onFailure("Units must be of valid type");
                    return;
                }
                //removes the unit from the string
                currentIngredient = currentIngredient.substring(0, index);
                index = currentIngredient.lastIndexOf(' ');
                String quantity = currentIngredient.substring(index + 1);
                double quantityDouble = Double.parseDouble(quantity);
                //remove the quantity from the string
                currentIngredient = currentIngredient.substring(0, index);
                String ingredientName = currentIngredient;
                int calories = 0;
                ingredients.add(new Ingredient(ingredientName, quantityDouble, unit, calories));

                //removes entire current string from input string to go to the next ingredient
                if (!lastIngredient) {
                    currString = currString.substring(indexComma + 2);
                }
            }
        } catch (NumberFormatException e) {
            //Parsing quantity fails
            callback.onFailure("Quantity must be a valid number");
            return;
        }
        for (Ingredient i : ingredients) {
            System.out.println(i.getName() + " " + i.getQuantity() + " " + i.getUnits());
        }
        Recipe recipe = new Recipe(recipeName, ingredients);
        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("name", recipe.getName());
        recipeMap.put("ingredients", recipe.getIngredients());

        db.collection("cookbook").document(recipe.getName())
                .set(recipeMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        callback.onSuccess(); // Notify success
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure("Failed to add Recipe" + e.getMessage());
                        //Notify failure
                    }
                });
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }


}
