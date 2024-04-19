package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.FirebaseDB;
import com.example.greenplate.model.Ingredient;
import com.example.greenplate.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingListViewModel extends ViewModel {
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

    public void addIngredient(String ingredientName, String quantity, String calories, IngredientUpdateCallback callback) {
        if (ingredientName.isEmpty() || quantity.isEmpty()) {
            //null checking
            callback.onIngredientUpdated(false,"Ingredient Name, Quantity, and Units are required");
            return;
        }
        String[] parts = quantity.split(" ");
        double quantityDouble = Double.parseDouble(parts[0]);
        if (quantityDouble < 0) {
            callback.onIngredientUpdated(false, "Quantity cannot be negative");
            return;
        }
        String unit = parts[1];
        if (unit.isEmpty()) {
            callback.onIngredientUpdated(false, "Please enter a unit");
            return;
        }
        double caloriesDouble = Double.parseDouble(calories);
        Ingredient ingredient = new Ingredient(ingredientName, quantityDouble, unit, caloriesDouble, "");

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
                        ingredientMap.put("calories", caloriesDouble);
                        userRef.collection("list").document(ingredientName)
                                .set(ingredientMap)
                                .addOnSuccessListener(documentReference -> callback.onIngredientUpdated(true,"Success"))
                                .addOnFailureListener(e -> callback.onIngredientUpdated(false,"Failed to add ingredient: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onIngredientUpdated(false,"Failed to check if ingredient exists: " + e.getMessage()));

    }

    public void buyIngredient(String ingredientName, String quantity, String calories, IngredientUpdateCallback callback) {
        userRef.collection("list").document(ingredientName)
            .delete()
            .addOnSuccessListener(aVoid -> {
                // Successfully bought ingredient
                // Add to pantry
                createIngredient(ingredientName, quantity, calories, "", callback);
                callback.onIngredientUpdated(true, "Success");
            })
            .addOnFailureListener(e -> {
                // Failed to update quantity
                callback.onIngredientUpdated(false, "Failed to buy ingredient");
            });
    }

    public void createIngredient(
            String ingredientName, String quantity,
            String calories, String expirationdate, IngredientUpdateCallback callback) {

        if (ingredientName.isEmpty() || calories.isEmpty() || quantity.isEmpty()) {
            //null checking
            callback.onIngredientUpdated(false,"Ingredient Name, Quantity, Units, and Calories are required");
            return;
        }
        if (calories.contains(" ")) {
            //calories will not parse to an int
            callback.onIngredientUpdated(false,"Calories cannot contain whitespace");
            return;
        }
        if (calories.contains("-")) {
            //calories cannot be negative
            callback.onIngredientUpdated(false,"Calories cannot be negative");
            return;
        }
        try {
            double caloriesDouble = Double.parseDouble(calories);
            String[] parts = quantity.split(" ");
            double quantityDouble = Double.parseDouble(parts[0]);
            if (quantityDouble < 0) {
                callback.onIngredientUpdated(false, "Quantity cannot be negative");
                return;
            }
            String unit = parts[1];
            if (unit.isEmpty()) {
                callback.onIngredientUpdated(false, "Please enter a unit");
                return;
            }

            userRef.collection("pantry").whereEqualTo("name", ingredientName).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Ingredient already exists
                            callback.onIngredientUpdated(false,"Ingredient already exists in the pantry");
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
                                        .addOnSuccessListener(documentReference -> callback.onIngredientUpdated(true, "Success"))
                                        .addOnFailureListener(e -> callback.onIngredientUpdated(false, "Failed to add ingredient: " + e.getMessage()));
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
                                        .addOnSuccessListener(documentReference -> callback.onIngredientUpdated(true, "Success"))
                                        .addOnFailureListener(e -> callback.onIngredientUpdated(false, "Failed to add ingredient: " + e.getMessage()));
                            }
                        }
                    })
                    .addOnFailureListener(e -> callback.onIngredientUpdated(false, "Failed to check if ingredient exists: " + e.getMessage()));

        } catch (NumberFormatException e) {
            // Parse Fails
            callback.onIngredientUpdated(false,"Calories must be a valid number");
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

    private void updateIngredientQuantity(Ingredient ingredient, Double newQuantity, IngredientUpdateCallback callback) {
        if (newQuantity <= 0) {
            // Remove the ingredient if its quantity becomes 0 or less
            removeIngredient(ingredient, callback);
        } else {
            // Update the ingredient quantity in the database
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("quantity", newQuantity);
            userRef.collection("list").document(ingredient.getName())
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
        userRef.collection("list").document(ingredient.getName())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully removed ingredient
                    callback.onIngredientUpdated(true, "Successful removal");
                })
                .addOnFailureListener(e ->
                        callback.onIngredientUpdated(false, "Failed to check if ingredient exists: "
                                + e.getMessage()));
    }

    public void getShoppingList(ShoppingListViewModel.GetListCallback callback) {
        ArrayList<Ingredient> slist = new ArrayList<Ingredient>();
        userRef.collection("list").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot ingredientDocuments = task.getResult();
                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Object> i = document.getData();
                    Ingredient ingre = new Ingredient(i.get("name").toString(), (double) i.get("quantity"), i.get("units").toString(), (double) i.get("calories"));
                    slist.add(ingre);
                }
                callback.onSuccess(slist);
            } else {
                callback.onFailure("Failed to access list: " + task.getException().getMessage());
            }
        }).addOnFailureListener(e -> System.out.println("Failed with exception: " + e.getMessage()));
    }

    public void getPantry(String recipeName, GetPantryCallBack callback) {
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

    public void addIngredientsRecipe(ArrayList<Ingredient> pantry, ArrayList<Recipe> recipes, int recipePosition, AddCallback callback) {
        //Figure out which more ingredients we need
        ArrayList<Ingredient> neededIngredients = new ArrayList<Ingredient>();
        Recipe recipe = recipes.get(recipePosition);
        ArrayList<Ingredient> recipeIngredients = recipe.getIngredients();
        for (Ingredient i : recipeIngredients) {
            String name = i.getName();
            for (Ingredient pI : pantry) {
                if (pI.getName().equalsIgnoreCase(name)) {
                    //We have some of the ingredient
                    if (pI.getQuantity() < i.getQuantity()) {
                        //We need more of said ingredient
                        addIngredient(name, ((i.getQuantity() - pI.getQuantity()) + i.getUnits()), Double.toString(i.getCalories()), new IngredientUpdateCallback() {
                            @Override
                            public void onSuccess() {
                                //ingredient added, we may proceed
                            }

                            @Override
                            public void onFailure(String error) {

                            }
                        });
                    }
                }
            }
        }

    }



    public interface addIngredientCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface IngredientUpdateCallback {
        void onIngredientUpdated(boolean success, String message);
    }

    public interface GetListCallback {
        void onSuccess(ArrayList<Ingredient> pantry);
        void onFailure(String error);
    }

    public interface RecipeCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface AddCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface GetPantryCallBack {
        void onSuccess(ArrayList<Ingredient> pantry);
        void onFailure(String error);
    }

}
