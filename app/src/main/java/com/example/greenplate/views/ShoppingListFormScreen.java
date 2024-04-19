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
import android.widget.Toast;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.ShoppingListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ShoppingListFormScreen extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private ShoppingListViewModel viewModel;

    private EditText editTextShoppingIngredient;
    private EditText editTextQuantity, editTextCalories;
    private RecyclerView recyclerViewIngredients;

    private Button buttonSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_form_screen);
        viewModel = new ViewModelProvider(this).get(ShoppingListViewModel.class);

        editTextShoppingIngredient = findViewById(R.id.editTextShoppingIngredient);
        editTextQuantity = findViewById(R.id.editTextShopQuantity);
        editTextCalories = findViewById(R.id.editTextShopCalories);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> {
            String ingredientName = editTextShoppingIngredient.getText().toString();
            String quantity = editTextQuantity.getText().toString();
            String calories = editTextCalories.getText().toString();
            addIngredient(ingredientName, quantity, calories);
            Intent intent = new Intent(ShoppingListFormScreen.this, HomeScreen.class);
            startActivity(intent);
        });

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(ShoppingListFormScreen.this, ShoppingListScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recipenav) {
                    Intent intent = new Intent(ShoppingListFormScreen.this, RecipeScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.ingredientnav) {
                    Intent intent = new Intent(ShoppingListFormScreen.this, IngredientScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.inputmealnav) {
                    Intent intent = new Intent(ShoppingListFormScreen.this, InputMeal.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.personalinfonav) {
                    Intent intent = new Intent(ShoppingListFormScreen.this, PersonalInfoScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void addIngredient(String ingredientName, String quantity, String calories) {
        viewModel.addIngredient(ingredientName, quantity, calories, new ShoppingListViewModel.IngredientUpdateCallback() {
            @Override
            public void onIngredientUpdated(boolean success, String error) {
                if (success) {
                    Toast.makeText(ShoppingListFormScreen.this,
                            "Add to Shopping List: " + error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShoppingListFormScreen.this,
                            "Failed to add to Shopping list: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}