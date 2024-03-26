package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.viewmodels.RecipeScreenViewModel;
import com.example.greenplate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class RecipeScreen extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private Button buttonAdd;
    private EditText editTextRecipeName;
    private EditText editTextInputDetails;
    private RecipeScreenViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_screen);
        viewModel = new ViewModelProvider(this).get(RecipeScreenViewModel.class);

        buttonAdd = findViewById(R.id.buttonAdd);
        editTextInputDetails = findViewById(R.id.editTextInputDetails);
        editTextRecipeName = findViewById(R.id.editTextRecipeName);

        buttonAdd.setOnClickListener(v -> {
            String recipeName = editTextRecipeName.getText().toString();
            String inputDetails = editTextInputDetails.getText().toString();
            createRecipe(recipeName, inputDetails);
        });

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
