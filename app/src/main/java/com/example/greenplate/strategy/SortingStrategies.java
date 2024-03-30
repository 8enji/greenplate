package com.example.greenplate.strategy;
import com.example.greenplate.model.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortingStrategies {

    public interface RecipeSortingStrategy {
        void sort(ArrayList<Recipe> recipes);
    }

    public interface RecipeFilteringStrategy {
        ArrayList<Recipe> filter(ArrayList<Recipe> recipes, ArrayList<String> cookable);
    }

    public static class SortByNameStrategy implements RecipeSortingStrategy {
        @Override
        public void sort(ArrayList<Recipe> recipes) {
            Collections.sort(recipes, Comparator.comparing(Recipe::getName));
        }
    }

    public static class FilterByCookableStrategy implements RecipeFilteringStrategy {
        @Override
        public ArrayList<Recipe> filter(ArrayList<Recipe> recipes, ArrayList<String> cookable) {
            ArrayList<Recipe> filteredRecipes = new ArrayList<>();
            for (int i = 0; i < recipes.size(); i++) {
                if ("Yes".equals(cookable.get(i))) {
                    filteredRecipes.add(recipes.get(i));
                }
            }
            return filteredRecipes;
        }
}}
