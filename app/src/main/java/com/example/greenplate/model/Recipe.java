package com.example.greenplate.model;

import java.util.ArrayList;
import java.util.Comparator;

public class Recipe {
    private String name;
    private ArrayList<Ingredient> ingredients;
    public Recipe(String name) {
        this.name = name;
        ingredients = new ArrayList<>();
    }
    public Recipe(String name, ArrayList<Ingredient> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public boolean canCook(ArrayList<Ingredient> userPantry) {
        for (Ingredient recipeIngredient : ingredients) {
            boolean verified = false;
            for (Ingredient userIngredient : userPantry) {
                if (recipeIngredient.getName().equals(getName())) {
                    if (userIngredient.getQuantity() >= recipeIngredient.getQuantity()) {
                        verified = true;
                    }
                }
            }
            if (!verified) {
                return false;
            }
        }

        return true;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public interface SortingStrategy {
        ArrayList<Recipe> sort(ArrayList<Recipe> recipes);
    }

    public interface FilteringStrategy {
        ArrayList<Recipe> filter(ArrayList<Recipe> recipes, String criterion);
    }

    // In com.example.greenplate.model.strategy
    public class SortByName implements SortingStrategy {
        @Override
        public ArrayList<Recipe> sort(ArrayList<Recipe> recipes) {
            recipes.sort(Comparator.comparing(Recipe::getName));
            return recipes;
        }
    }

    public class FilterByIngredient implements FilteringStrategy {
        @Override
        public ArrayList<Recipe> filter(ArrayList<Recipe> recipes, String ingredient) {
            // Filtering logic
        }
    }

}
