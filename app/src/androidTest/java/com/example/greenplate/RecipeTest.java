package com.example.greenplate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.greenplate.model.Ingredient;
import com.example.greenplate.model.Recipe;

import org.junit.Test;

import java.util.ArrayList;

public class RecipeTest {
    public void testCanCook() {
        Ingredient ingredient1 = new Ingredient("Flour", 500, "grams", 0);
        Ingredient ingredient2 = new Ingredient("Sugar", 200, "grams", 0);
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);
        Recipe recipe = new Recipe("Cake", ingredients);

        Ingredient pantryItem1 = new Ingredient("Flour", 1000, "grams", 0);
        Ingredient pantryItem2 = new Ingredient("Sugar", 500, "grams", 0);
        ArrayList<Ingredient> pantry = new ArrayList<>();
        pantry.add(pantryItem1);
        pantry.add(pantryItem2);

        assertTrue(recipe.canCook(pantry));
    }

    @Test
    public void testCannotCook() {
        Ingredient ingredient1 = new Ingredient("Flour", 500, "grams", 0);
        Ingredient ingredient2 = new Ingredient("Sugar", 200, "grams", 0);
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);
        Recipe recipe = new Recipe("Cake", ingredients);

        Ingredient pantryItem1 = new Ingredient("Flour", 300, "grams", 0);
        Ingredient pantryItem2 = new Ingredient("Sugar", 100, "grams", 0);
        ArrayList<Ingredient> pantry = new ArrayList<>();
        pantry.add(pantryItem1);
        pantry.add(pantryItem2);

        assertFalse(recipe.canCook(pantry));
    }
}
