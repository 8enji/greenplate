package com.example.greenplate;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.greenplate.model.Recipe;
import com.example.greenplate.model.Ingredient;

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
}

