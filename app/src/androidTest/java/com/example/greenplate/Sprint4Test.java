package com.example.greenplate;

import com.example.greenplate.model.Ingredient;
import com.example.greenplate.viewmodels.RecipeDetailsScreenViewModel;
import com.example.greenplate.viewmodels.ShoppingListViewModel;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Sprint4Test {
    @Test
    public void testValidateIngredientInput_EmptyName() {
        String result = ShoppingListViewModel.validateIngredientInput("", "1 kg", "100");
        assertEquals("Ingredient Name, Quantity, and Units are required", result);
    }

    @Test
    public void testValidateIngredientInput_EmptyQuantity() {
        String result = ShoppingListViewModel.validateIngredientInput("Sugar", "", "100");
        assertEquals("Ingredient Name, Quantity, and Units are required", result);
    }

    @Test
    public void testValidateIngredientInput_NegativeQuantity() {
        String result = ShoppingListViewModel.validateIngredientInput("Sugar", "-1 kg", "100");
        assertEquals("Quantity cannot be negative", result);
    }

    @Test
    public void testValidateIngredientInput_InvalidQuantityFormat() {
        String result = ShoppingListViewModel.validateIngredientInput("Sugar", "one kg", "100");
        assertEquals("Quantity must be a valid number", result);
    }

    @Test
    public void testValidateIngredientInput_EmptyCalories() {
        String result = ShoppingListViewModel.validateIngredientInput("Sugar", "1 kg", "");
        assertEquals("Calories are required", result);
    }

    @Test
    public void testValidateIngredientInput_NegativeCalories() {
        String result = ShoppingListViewModel.validateIngredientInput("Sugar", "1 kg", "-100");
        assertEquals("Calories cannot be negative", result);
    }


    // next two are recommended sprint 4 unit tests on the sprint 4 doc
    @Test
    public void testMissingIngredientsCalculatedCorrectly() {
        HashMap<String, Ingredient> pantry = new HashMap<>();
        pantry.put("Flour", new Ingredient("Flour", 1.0, "kg", 500));
        pantry.put("Sugar", new Ingredient("Sugar", 0.5, "kg", 400));

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Flour", 1.0, "kg", 500));
        ingredients.add(new Ingredient("Eggs", 12, "pieces", 300)); // Not in pantry
        ingredients.add(new Ingredient("Sugar", 1.0, "kg", 600));  // More than available

        Map<String, Double> results = RecipeDetailsScreenViewModel.processIngredients(pantry, ingredients);

        assertEquals(0.0, results.get("Flour"), 0.01);  // Flour exactly used up
        assertEquals(-1.0, results.get("Eggs"), 0.01);  // Eggs are missing
        assertEquals(0.0, results.get("Sugar"), 0.01); // Not enough Sugar, marked as missing
    }

    @Test
    public void testRecipeIngredientsSubtractedFromPantry() {
        HashMap<String, Ingredient> pantry = new HashMap<>();
        pantry.put("Butter", new Ingredient("Butter", 2.0, "kg", 700));
        pantry.put("Vanilla", new Ingredient("Vanilla", 0.5, "kg", 100));

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Butter", 1.5, "kg", 700));  // Subtract from available
        ingredients.add(new Ingredient("Vanilla", 0.2, "kg", 50));  // Subtract from available

        Map<String, Double> results = RecipeDetailsScreenViewModel.processIngredients(pantry, ingredients);

        assertEquals(0.5, results.get("Butter"), 0.01);  // Butter should be reduced
        assertEquals(0.3, results.get("Vanilla"), 0.01); // Vanilla should be reduced
    }

    //  Checks if the pantry ingredient quantity exactly matches the recipe requirement and verifies that the pantry is emptied accordingly.
    @Test
    public void testProcessIngredientsWithExactMatch() {
        HashMap<String, Ingredient> pantry = new HashMap<>();
        pantry.put("Flour", new Ingredient("Flour", 2.0, "kg", 1500));

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Flour", 2.0, "kg", 1500));

        Map<String, Double> results = RecipeDetailsScreenViewModel.processIngredients(pantry, ingredients);

        assertEquals(0.0, results.get("Flour"), 0.01);  // Flour should be fully used
    }

    // Tests the scenario where the pantry does not contain any of the ingredients required by the recipe.
    @Test
    public void testProcessIngredientsWithEmptyPantry() {
        HashMap<String, Ingredient> pantry = new HashMap<>();

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Butter", 0.5, "kg", 700));

        Map<String, Double> results = RecipeDetailsScreenViewModel.processIngredients(pantry, ingredients);

        assertEquals(-1.0, results.get("Butter"), 0.01);  // Butter should be marked as missing
    }

    // Verifies the behavior when the recipe does not require any ingredients (i.e. the ingredient list is empty).
    @Test
    public void testProcessIngredientsWithNoIngredients() {
        HashMap<String, Ingredient> pantry = new HashMap<>();
        pantry.put("Milk", new Ingredient("Milk", 1.0, "liters", 500));

        ArrayList<Ingredient> ingredients = new ArrayList<>();

        Map<String, Double> results = RecipeDetailsScreenViewModel.processIngredients(pantry, ingredients);

        assertTrue(results.isEmpty());  // No ingredients to process, should return empty map
    }

    // Tests the condition where none of the required ingredients are present in the pantry, ensuring that each missing ingredient is marked correctly.
    @Test
    public void testProcessIngredientsAllIngredientsMissing() {
        HashMap<String, Ingredient> pantry = new HashMap<>();
        pantry.put("Sugar", new Ingredient("Sugar", 1.0, "kg", 400));

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Flour", 1.0, "kg", 500));
        ingredients.add(new Ingredient("Eggs", 6, "pieces", 300));

        Map<String, Double> results = RecipeDetailsScreenViewModel.processIngredients(pantry, ingredients);

        assertEquals(-1.0, results.get("Flour"), 0.01);  // Flour should be marked as missing
        assertEquals(-1.0, results.get("Eggs"), 0.01);  // Eggs should be marked as missing
        assertNull(results.get("Sugar")); // Sugar was not requested, should not be in results
    }

}
