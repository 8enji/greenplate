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

import com.example.greenplate.model.Ingredient;
import com.example.greenplate.viewmodels.IngredientViewModel;
import android.widget.Toast;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.RecipeScreenViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class IngredientScreen extends AppCompatActivity {
    private IngredientViewModel viewModel;
    private BottomNavigationView bottomNavigation;

    private Button addIngredientButton;
    private RecyclerView recyclerViewIngredients;
    private IngredientAdapter adapter;
    private ArrayList<Ingredient> globalPantry;

    private IngredientViewModel ingredientViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_screen);
        viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        addIngredientButton = findViewById(R.id.ingredientButton);

        addIngredientButton.setOnClickListener(v -> {
            Intent intent = new Intent(IngredientScreen.this, IngredientsFormScreen.class);
            startActivity(intent);
        });

        recyclerViewIngredients = findViewById(R.id.ingredientsRecyclerView);
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));
        loadPantry();


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

    private void loadPantry() {
        viewModel.getPantry(new IngredientViewModel.GetPantryCallBack() {
            @Override
            public void onSuccess(ArrayList<Ingredient> pantry) {
                globalPantry = pantry;
                IngredientAdapter adapter = new IngredientAdapter(pantry, viewModel);
                recyclerViewIngredients.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(IngredientScreen.this,
                        "Failed to access pantry: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }



}
