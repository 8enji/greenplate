package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.greenplate.viewmodels.IngredientViewModel;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.example.greenplate.viewmodels.RecipeScreenViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class IngredientScreen extends AppCompatActivity {
    private IngredientViewModel viewModel;
    private BottomNavigationView bottomNavigation;

    private EditText editTextIngredientName, editTextQuantity, editTextCalories;
    private Button buttonSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_screen);
        viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        editTextIngredientName = findViewById(R.id.editTextIngredientName);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextCalories = findViewById(R.id.editTextCaloriesPerServing);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> {
            String ingredientName = editTextIngredientName.getText().toString();
            String quantity = editTextQuantity.getText().toString();
            String calories = editTextCalories.getText().toString();
            createIngredient(ingredientName, quantity, calories);
        });

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(IngredientScreen.this, ShoppingList.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recipenav) {
                    Intent intent = new Intent(IngredientScreen.this, RecipeScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.ingredientnav) {
                    Intent intent = new Intent(IngredientScreen.this, IngredientScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.inputmealnav) {
                    Intent intent = new Intent(IngredientScreen.this, InputMeal.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.personalinfonav) {
                    Intent intent = new Intent(IngredientScreen.this, PersonalInfoScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void createIngredient(String ingredientName, String quantity, String calories) {
        viewModel.createIngredient(ingredientName, quantity, calories, new IngredientViewModel.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(IngredientScreen.this,
                        "Ingredient added", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(IngredientScreen.this,
                        "Ingredient add failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
