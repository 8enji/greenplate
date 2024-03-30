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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeScreenViewModel extends ViewModel {
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;
    //private ArrayList<Ingredient> globalPantry;

    public RecipeScreenViewModel() {
        db = FirebaseDB.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        userRef = db.collection("users").document(email);
    }

    public void loadRecipes(ArrayList<Ingredient> globalPantry, LoadRecipesCallback callback) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        ArrayList<String> cookable = new ArrayList<>();
        db.collection("cookbook").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot recipesDocuments = task.getResult();
                for (DocumentSnapshot document : recipesDocuments) {
                    Map<String, Object> r = document.getData();
                    ArrayList<HashMap<String, Object>> rI = (ArrayList<HashMap<String, Object>>) r.get("ingredients");
                    ArrayList<Ingredient> recipeIngredients = new ArrayList<Ingredient>();
                    for (int i = 0; i < rI.size(); i++) {
                        Ingredient ingr = new Ingredient(rI.get(i).get("name").toString(), (double) rI.get(i).get("quantity"), rI.get(i).get("units").toString(), 0);
                        recipeIngredients.add(ingr);
                    }
                    Recipe recipe = new Recipe(r.get("name").toString(), recipeIngredients);
                    recipes.add(recipe);
                }
                for (Recipe r : recipes) {
                    if (r.canCook(globalPantry)) {
                        cookable.add("Yes");
                    } else {
                        cookable.add("No");
                    }
                }
                callback.onSuccess(recipes, cookable);

            } else {
                callback.onFailure("recipes cannot be accessed");
            }
        });
    }

    public void getPantry(GetPantryCallBack callback) {
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
        Recipe recipe = new Recipe(recipeName, ingredients);

        db.collection("cookbook").document(recipe.getName())
                .set(recipe)
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


    public interface LoadRecipesCallback {
        void onSuccess(ArrayList<Recipe> recipes, ArrayList<String> cookable);
        void onFailure(String error);
    }

    public interface GetPantryCallBack {
        void onSuccess(ArrayList<Ingredient> pantry);
        void onFailure(String error);
    }

    public void sortRecipesByName(ArrayList<Recipe> recipes) {
        Collections.sort(recipes, Comparator.comparing(Recipe::getName));
    }

    public void sortRecipesByAvailability(ArrayList<Recipe> recipes, ArrayList<String> cookable) {
        // Assuming recipes and cookable lists are aligned by index
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < cookable.size(); i++) {
            if ("Yes".equals(cookable.get(i))) {
                indices.add(i);
            }
        }
        Collections.sort(indices, Comparator.comparing(recipes::get));
        // Now, reorder recipes based on sorted indices
        ArrayList<Recipe> sorted = new ArrayList<>();
        for (int index : indices) {
            sorted.add(recipes.get(index));
        }
        recipes.clear();
        recipes.addAll(sorted);
    }

    public ArrayList<Recipe> filterByAvailability(ArrayList<Recipe> recipes, ArrayList<String> cookable) {
        // Filter recipes where cookable is "Yes"
        return recipes.stream().filter(recipe -> cookable.get(recipes.indexOf(recipe)).equals("Yes"))
                .collect(Collectors.toCollection(ArrayList::new));
    }



}
