package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.greenplate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ShoppingList extends AppCompatActivity {
    private BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        bottom_navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.item_1) {
                    Intent intent = new Intent(ShoppingList.this, ShoppingList.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.item_2) {
                    Intent intent = new Intent(ShoppingList.this, RecipeScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.item_2) {
                    Intent intent = new Intent(ShoppingList.this, IngredientScreen.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ShoppingList.this, InputMeal.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }
}