package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.greenplate.model.Ingredient;
import com.example.greenplate.model.Recipe;
import com.example.greenplate.viewmodels.IngredientViewModel;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.example.greenplate.viewmodels.RecipeDetailsScreenViewModel;
import com.example.greenplate.viewmodels.RecipeScreenViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeDetailsScreen extends AppCompatActivity {
    private RecipeDetailsScreenViewModel viewModel;
    private RecipeScreenViewModel viewModel4;
    private InputMealViewModel viewModel3;
    private BottomNavigationView bottomNavigation;
    private TextView recipeName;
    private Button buttonBack;
    private Button buttonCook;
    private IngredientViewModel viewModel2;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details_screen);
        viewModel = new ViewModelProvider(this).get(RecipeDetailsScreenViewModel.class);
        viewModel2 = new ViewModelProvider(this).get(IngredientViewModel.class);
        viewModel3 = new ViewModelProvider(this).get(InputMealViewModel.class);
        viewModel4 = new ViewModelProvider(this).get(RecipeScreenViewModel.class);
        recipeName = findViewById(R.id.textViewRecipeName);
        buttonBack = findViewById(R.id.buttonBack);
        buttonCook = findViewById(R.id.buttonCook);
        recyclerView = findViewById(R.id.ingredientsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Retrieve the recipe name passed as an extra
        String recipeNameString = intent.getStringExtra("RECIPE_NAME");
        recipeName.setText(recipeNameString);
        loadRecipe(recipeNameString);

        buttonCook.setOnClickListener(v -> {
            cookRecipe(recipeNameString);
            Intent intentNew = new Intent(RecipeDetailsScreen.this, HomeScreen.class);
            startActivity(intentNew);
        });

        buttonBack.setOnClickListener(v -> {
            finish();
        });

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(RecipeDetailsScreen.this, ShoppingListScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recipenav) {
                    Intent intent = new Intent(RecipeDetailsScreen.this, RecipeScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.ingredientnav) {
                    Intent intent = new Intent(RecipeDetailsScreen.this, IngredientScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.inputmealnav) {
                    Intent intent = new Intent(RecipeDetailsScreen.this, InputMeal.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.personalinfonav) {
                    Intent intent = new Intent(RecipeDetailsScreen.this, PersonalInfoScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void loadRecipe(String recipeNameString) {
        viewModel.loadRecipe(recipeNameString, new RecipeDetailsScreenViewModel.LoadRecipeCallback() {
            //Handle successful loading of recipe and update UI
            @Override
            public void onSuccess(ArrayList<Ingredient> ingredients) {
                //handle setting recylcer view elements to all the recipes
                RecipeIngredientAdapter adapter = new RecipeIngredientAdapter(ingredients, viewModel2);
                recyclerView.setAdapter(adapter);
            }

            //Handle failure loading of recipes and update UI
            @Override
            public void onFailure(String error) {
                Toast.makeText(RecipeDetailsScreen.this,
                        "Failed to load recipe: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cookRecipe(String recipeNameString) {
        viewModel.loadRecipe(recipeNameString, new RecipeDetailsScreenViewModel.LoadRecipeCallback() {
            //Handle successful loading of recipe and update UI
            @Override
            public void onSuccess(ArrayList<Ingredient> ingredients) {
                //handle cooking of recipe once we have all the ingredients
                getPantry(ingredients, recipeNameString);
            }

            //Handle failure loading of recipes and update UI
            @Override
            public void onFailure(String error) {
                Toast.makeText(RecipeDetailsScreen.this,
                        "Failed to load recipe: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPantry(ArrayList<Ingredient> ingredients, String recipeNameString) {
        viewModel.getPantry(new RecipeDetailsScreenViewModel.GetPantryCallBack() {
            @Override
            public void onSuccess(HashMap<String, Ingredient> pantry) {
                removeIngredientsAndAddMeal(pantry, ingredients, recipeNameString);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(RecipeDetailsScreen.this,
                        "Failed to load pantry: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeIngredientsAndAddMeal(HashMap<String, Ingredient> pantry, ArrayList<Ingredient> ingredients, String recipeNameString) {
        viewModel.removeIngredients(pantry, ingredients, new RecipeDetailsScreenViewModel.PantryCallback() {
            @Override
            public void onSuccess(int totalCalories) {
                addMeal(totalCalories, recipeNameString);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(RecipeDetailsScreen.this,
                        "Failed to access pantry: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMeal(int totalCalories, String recipeNameString) {
        viewModel3.createMeal(recipeNameString, Integer.toString(totalCalories), new InputMealViewModel.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(RecipeDetailsScreen.this,
                        "Meal added, Ingredients removed", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(RecipeDetailsScreen.this,
                        "Meal add failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
