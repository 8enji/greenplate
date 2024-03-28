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

import org.w3c.dom.Document;

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

    public void loadRecipes(LoadRecipesCallback callback) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        ArrayList<String> cookable = new ArrayList();
        db.collection("cookbook").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot recipesDocuments = task.getResult();
                for (DocumentSnapshot document : recipesDocuments) {
                    Map<String, Object> r = document.getData();
                    Recipe recipe = new Recipe(r.get("name").toString(), (ArrayList<Ingredient>) r.get("ingredients"));
                    recipes.add(recipe);
                    ArrayList<Ingredient> pantry = getPantry();
                    if (recipe.canCook(pantry)) {
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

    public ArrayList<Ingredient> getPantry() {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        userRef.collection("pantry").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot ingredientDocuments = task.getResult();
                for (DocumentSnapshot document : ingredientDocuments) {
                    Map<String, Object> i = document.getData();
                    ingredients.add(new Ingredient(i.get("name").toString(), (double) i.get("quantity"), i.get("unit").toString(), (int) i.get("calories")));
                }
            }
        });
        return ingredients;
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
                ingredients.add(new Ingredient(ingredientName, quantityDouble, unit));

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

    public interface GetPantryCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface LoadRecipesCallback {
        void onSuccess(ArrayList<Recipe> recipes, ArrayList<String> cookable);
        void onFailure(String error);
    }


}
