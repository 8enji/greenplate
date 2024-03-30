package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.model.Ingredient;
import com.example.greenplate.model.SortingStrategies;
import com.example.greenplate.viewmodels.RecipeScreenViewModel;
import com.example.greenplate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

import com.example.greenplate.model.Recipe;

public class RecipeScreen extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private Button buttonAdd;
    private EditText editTextRecipeName;
    private EditText editTextInputDetails;
    private RecyclerView recyclerView;
    private RecipeScreenViewModel viewModel;
    private ArrayList<Ingredient> globalPantry;
    private Button buttonSortByName;
    private Button buttonFilterByCookable;

    private ArrayList<Recipe> recipes;
    private ArrayList<String> cookable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_screen);
        viewModel = new ViewModelProvider(this).get(RecipeScreenViewModel.class);

        buttonAdd = findViewById(R.id.buttonAdd);
        editTextInputDetails = findViewById(R.id.editTextInputDetails);
        editTextRecipeName = findViewById(R.id.editTextRecipeName);
        recyclerView = findViewById(R.id.recipesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonSortByName = findViewById(R.id.buttonSortByName);
        buttonFilterByCookable = findViewById(R.id.buttonFilterByCookable);

        buttonSortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setSortingStrategy(new SortingStrategies.SortByNameStrategy());
                sortRecipes();
            }
        });

        buttonFilterByCookable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setFilteringStrategy(new SortingStrategies.FilterByCookableStrategy());
                filterRecipes(); // Call the correct method here
            }
        });



        buttonAdd.setOnClickListener(v -> {
            String recipeName = editTextRecipeName.getText().toString();
            String inputDetails = editTextInputDetails.getText().toString();
            createRecipe(recipeName, inputDetails);

        });

        loadPantry();

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(RecipeScreen.this, ShoppingList.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recipenav) {
                    Intent intent = new Intent(RecipeScreen.this, RecipeScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.ingredientnav) {
                    Intent intent = new Intent(RecipeScreen.this, IngredientScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.inputmealnav) {
                    Intent intent = new Intent(RecipeScreen.this, InputMeal.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.personalinfonav) {
                    Intent intent = new Intent(RecipeScreen.this, PersonalInfoScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });




    }

    private void sortRecipes() {
        viewModel.sortRecipes(recipes);
        RecipeAdapter adapter = new RecipeAdapter(recipes, cookable);
        recyclerView.setAdapter(adapter);
    }

    // Method to be called when the "Filter by Cookable" button is pressed
    private void filterRecipes() {
        ArrayList<Recipe> filteredRecipes = viewModel.filterRecipes(recipes, cookable);
        ArrayList<String> filteredCookable = new ArrayList<>();
        for (Recipe r : filteredRecipes) {
            filteredCookable.add("Yes");
        }

        // Update the UI with filtered recipes
        RecipeAdapter adapter = new RecipeAdapter(filteredRecipes, filteredCookable);
        recyclerView.setAdapter(adapter);

        // Notify the user if no recipes are found after filtering
        if (filteredRecipes.isEmpty()) {
            Toast.makeText(this, "No recipes found after filtering", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadPantry() {
        viewModel.getPantry(new RecipeScreenViewModel.GetPantryCallBack() {
            @Override
            public void onSuccess(ArrayList<Ingredient> pantry) {
                globalPantry = pantry;
                loadRecipes();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(RecipeScreen.this,
                        "Failed to access pantry: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRecipes() {
        viewModel.loadRecipes(globalPantry, new RecipeScreenViewModel.LoadRecipesCallback() {
            @Override
            public void onSuccess(ArrayList<Recipe> loadedRecipes, ArrayList<String> loadedCookable) {
                // Update the member variables 'recipes' and 'cookable' with the loaded data
                RecipeScreen.this.recipes = loadedRecipes;
                RecipeScreen.this.cookable = loadedCookable;

                // Update the RecyclerView with the new data
                RecipeAdapter adapter = new RecipeAdapter(recipes, cookable);
                recyclerView.setAdapter(adapter);
            }

            //Handle failure loading of recipes and update UI
            @Override
            public void onFailure(String error) {
                Toast.makeText(RecipeScreen.this, "Failed to load recipes: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createRecipe(String recipeName, String inputDetails) {
        viewModel.createRecipe(recipeName, inputDetails, new RecipeScreenViewModel.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(RecipeScreen.this,
                        "Recipe added", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(RecipeScreen.this,
                        "Recipe add failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
