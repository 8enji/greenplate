package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.greenplate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class PersonalInfoScreen extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;

    private EditText editTextHeight;
    private EditText editTextWeight;

    private EditText editTextGender;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_screen);


        // Initialize the views
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextGender = findViewById(R.id.editTextGender);
        buttonSave = findViewById(R.id.buttonSave);

        loadPersonalInfo();

        // Set up the button click listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePersonalInfo();
            }
        });


        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(PersonalInfoScreen.this, ShoppingList.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recipenav) {
                    Intent intent = new Intent(PersonalInfoScreen.this, RecipeScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.ingredientnav) {
                    Intent intent = new Intent(PersonalInfoScreen.this, IngredientScreen.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.inputmealnav) {
                    Intent intent = new Intent(PersonalInfoScreen.this, InputMeal.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.personalinfonav) {
                    Intent intent = new Intent(PersonalInfoScreen.this, PersonalInfoScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void savePersonalInfo() {
        String height = editTextHeight.getText().toString();
        String weight = editTextWeight.getText().toString();
        String gender = editTextGender.getText().toString();

        // Using SharedPreferences to save the user's personal information
        SharedPreferences sharedPreferences = getSharedPreferences("UserPersonalInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("height", height);
        editor.putString("weight", weight);
        editor.putString("gender", gender);
        editor.apply();

        Toast.makeText(this, "Information saved!", Toast.LENGTH_SHORT).show();
    }

    private void loadPersonalInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPersonalInfo", MODE_PRIVATE);
        String height = sharedPreferences.getString("height", "");
        String weight = sharedPreferences.getString("weight", "");
        String gender = sharedPreferences.getString("gender", "");

        editTextHeight.setText(height);
        editTextWeight.setText(weight);
        editTextGender.setText(gender);
    }

}