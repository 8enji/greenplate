package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.ShoppingListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ShoppingList extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private ShoppingListViewModel viewModel;

    private EditText editTextShoppingIngredient;
    private EditText editTextQuantity, editTextCalories;
    private Button buttonSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        editTextShoppingIngredient = findViewById(R.id.editTextShoppingIngredient);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextCalories = findViewById(R.id.editTextCaloriesPerServing);
        buttonSave = findViewById(R.id.buttonSave);


        buttonSave.setOnClickListener(v -> {
            String ingredientName = editTextShoppingIngredient.getText().toString();
            String quantity = editTextQuantity.getText().toString();
            String calories = editTextCalories.getText().toString();
            addIngredient(ingredientName, quantity, calories);
            Intent intent = new Intent(ShoppingList.this, HomeScreen.class);
            startActivity(intent);
        });

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(ShoppingList.this, ShoppingList.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recipenav) {
                    Intent intent = new Intent(ShoppingList.this, RecipeScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.ingredientnav) {
                    Intent intent = new Intent(ShoppingList.this, IngredientScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.inputmealnav) {
                    Intent intent = new Intent(ShoppingList.this, InputMeal.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.personalinfonav) {
                    Intent intent = new Intent(ShoppingList.this, PersonalInfoScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void addIngredient(String ingredientName, String quantity, String calories) {
        viewModel.addIngredient(ingredientName, quantity, calories, new ShoppingListViewModel.IngredientUpdateCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ShoppingList.this,
                        "Ingredient added", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(ShoppingList.this,
                        "Ingredient add failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}