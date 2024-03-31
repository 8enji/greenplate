package com.example.greenplate;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.greenplate.model.Recipe;
import com.example.greenplate.model.Ingredient;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.RecipeScreenViewModel;

import java.util.ArrayList;

public class Sprint3Test {
    @Test
    public void testCanCook_EnoughIngredients() {
        Recipe recipe = new Recipe("Pasta");
        recipe.addIngredient(new Ingredient("Pasta", 2, "cups", 100));
        recipe.addIngredient(new Ingredient("Tomato Sauce", 1, "cup", 50));

        ArrayList<Ingredient> userPantry = new ArrayList<>();
        userPantry.add(new Ingredient("Pasta", 10, "cups", 100));
        userPantry.add(new Ingredient("Tomato Sauce", 2, "cup", 50));

        // Test if the user can cook the recipe
        assertTrue(recipe.canCook(userPantry));
    }

    @Test
    public void testCanCook_NotEnoughIngredients() {
        Recipe recipe = new Recipe("Pasta");
        recipe.addIngredient(new Ingredient("Pasta", 2, "cups", 100));
        recipe.addIngredient(new Ingredient("Tomato Sauce", 1, "cup", 50));

        ArrayList<Ingredient> userPantry = new ArrayList<>();
        userPantry.add(new Ingredient("Pasta", 1, "cup", 100));
        userPantry.add(new Ingredient("Tomato Sauce", 3, "cup", 50));

        // Test if the user can cook the recipe
        assertFalse(recipe.canCook(userPantry));
    }

    @Test
    public void testValidInputs() {
        String result = IngredientViewModel.validateIngredientInput("Tomato", "1.5 kg", "100");
        assertNull("Valid inputs should return null", result);
    }

    @Test
    public void testEmptyInputs() {
        String result = IngredientViewModel.validateIngredientInput("", "", "");
        assertEquals("Ingredient Name, Quantity, and Calories are required", result);
    }

    @Test
    public void testCaloriesContainWhitespace() {
        String result = IngredientViewModel.validateIngredientInput("Tomato", "1.5 kg", "10 0");
        assertEquals("Calories cannot contain whitespace", result);
    }

    @Test
    public void testNegativeCalories() {
        String result = IngredientViewModel.validateIngredientInput("Tomato", "1.5 kg", "-100");
        assertEquals("Calories cannot be negative", result);
    }

    @Test
    public void testNegativeQuantity() {
        String result = IngredientViewModel.validateIngredientInput("Tomato", "-1.5 kg", "100");
        assertEquals("Quantity cannot be negative", result);
    }

    @Test
    public void testCaloriesNotANumber() {
        String result = IngredientViewModel.validateIngredientInput("Tomato", "1.5 kg", "abc");
        assertEquals("Calories must be a valid number", result);
    }

    @Test
    public void whenRecipeNameIsEmpty_returnsErrorMessage() {
        String result = RecipeScreenViewModel.validateRecipeInput("", "2 cups flour, 1 cup water");
        assertEquals("Recipe Name and Ingredients List are required", result);
    }

    @Test
    public void whenIngredientDetailsAreEmpty_returnsErrorMessage() {
        String result = RecipeScreenViewModel.validateRecipeInput("Pizza", "");
        assertEquals("Recipe Name and Ingredients List are required", result);
    }

    @Test
    public void whenQuantityIsNegative_returnsErrorMessage() {
        String result = RecipeScreenViewModel.validateRecipeInput("Pizza", "-2 cups flour, 1 cup water");
        assertEquals("Quantities cannot be negative", result);
    }

    @Test
    public void whenUnitIsInvalid_returnsErrorMessage() {
        String result = RecipeScreenViewModel.validateRecipeInput("Pizza", "2 cups flour, 1 stone water");
        assertEquals("Units must be of valid type", result);
    }
    
}

