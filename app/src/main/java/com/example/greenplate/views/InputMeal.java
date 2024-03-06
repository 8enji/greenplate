package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class InputMeal extends AppCompatActivity {
    private InputMealViewModel viewModel;
    private BottomNavigationView bottomNavigation;
    private Button buttonAdd;
    private EditText editTextMealName;
    private EditText editTextCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_meal);

        viewModel = new ViewModelProvider(this).get(InputMealViewModel.class);

        editTextMealName = findViewById(R.id.editTextMealName);
        editTextCalories = findViewById(R.id.editTextCalories);
        buttonAdd = findViewById(R.id.buttonAdd);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        buttonAdd.setOnClickListener(v -> {
            String mealName = editTextMealName.getText().toString();
            String caloriesString = editTextCalories.getText().toString();
            createMeal(mealName, caloriesString);
        });
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(InputMeal.this, ShoppingList.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recipenav) {
                    Intent intent = new Intent(InputMeal.this, RecipeScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.ingredientnav) {
                    Intent intent = new Intent(InputMeal.this, IngredientScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.inputmealnav) {
                    Intent intent = new Intent(InputMeal.this, InputMeal.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.personalinfonav) {
                    Intent intent = new Intent(InputMeal.this, PersonalInfoScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void createMeal(String mealName, String caloriesString) {
        viewModel.createMeal(mealName, caloriesString, new InputMealViewModel.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(InputMeal.this,
                        "Meal added", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InputMeal.this,
                        "Meal add failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}