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
import com.example.greenplate.viewmodels.ShoppingListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListScreen extends AppCompatActivity {
    private ShoppingListViewModel viewModel;
    private BottomNavigationView bottomNavigation;

    private Button addIngredientButton;
    private RecyclerView recyclerViewShopping;
    private ShoppingAdapter adapter; //new
    private Button buyButton; // mew

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        viewModel = new ViewModelProvider(this).get(ShoppingListViewModel.class);

        addIngredientButton = findViewById(R.id.itemButton);

        //add ingredient button, action with Intent

        addIngredientButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingListScreen.this, ShoppingListFormScreen.class);
            startActivity(intent);
        });

        recyclerViewShopping = findViewById(R.id.shoppingRecyclerView);
        recyclerViewShopping.setLayoutManager(new LinearLayoutManager(this));
        loadList();

        // Initialize the buy button and set its onClickListener
        buyButton = findViewById(R.id.buyButton);
        buyButton.setEnabled(false); // Initially disabled
        buyButton.setOnClickListener(v -> buySelectedItems());

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(ShoppingListScreen.this, ShoppingListScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recipenav) {
                    Intent intent = new Intent(ShoppingListScreen.this, RecipeScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.ingredientnav) {
                    Intent intent = new Intent(ShoppingListScreen.this, IngredientScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.inputmealnav) {
                    Intent intent = new Intent(ShoppingListScreen.this, InputMeal.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.personalinfonav) {
                    Intent intent = new Intent(ShoppingListScreen.this, PersonalInfoScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void loadList() {
        viewModel.getShoppingList(new ShoppingListViewModel.GetListCallback() {
            @Override
            public void onSuccess(ArrayList<Ingredient> ingredients) {
                adapter = new ShoppingAdapter(ingredients, viewModel);
                recyclerViewShopping.setAdapter(adapter);
                buyButton.setEnabled(true); // Enable the button only after the adapter is ready
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ShoppingListScreen.this,
                        "Failed to access shopping list: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buySelectedItems() {
        if (adapter != null) { // Check if the adapter is not null
            List<Ingredient> selectedIngredients = adapter.getSelectedIngredients();
            if (!selectedIngredients.isEmpty()) {
                viewModel.buySelectedIngredients(selectedIngredients, new ShoppingListViewModel.IngredientUpdateCallback() {
                    @Override
                    public void onIngredientUpdated(boolean success, String message) {
                        if (success) {
                            Toast.makeText(ShoppingListScreen.this, "Items successfully moved to pantry", Toast.LENGTH_SHORT).show();
                            loadList(); // Refresh the list to remove bought items
                        } else {
                            Toast.makeText(ShoppingListScreen.this, "Failed to move items: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "No items selected to buy", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Shopping list is still loading or empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
