package com.example.greenplate.model;

import java.util.ArrayList;

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
}
