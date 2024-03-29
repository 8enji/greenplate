package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.greenplate.model.Ingredient;
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

import java.util.List;

public class IngredientScreen extends AppCompatActivity {
    private IngredientViewModel viewModel;
    private BottomNavigationView bottomNavigation;

    private Button addIngredientButton;

    private IngredientViewModel ingredientViewModel;

    private RecyclerView recyclerView;
    private IngredientsAdapter adapter;

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

//        LiveData<List<Ingredient>> ingredients = ingredientViewModel.getAllIngredients();
        recyclerView = findViewById(R.id.ingredientsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientsAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getAllIngredients().observe(this, ingredients -> {
            adapter.setIngredients(ingredients);
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


}
