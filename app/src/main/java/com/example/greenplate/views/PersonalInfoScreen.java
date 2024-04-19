package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.PersonalInfoViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Map;

public class PersonalInfoScreen extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private PersonalInfoViewModel viewModel;
    private EditText editTextHeight;
    private EditText editTextWeight;
    private EditText editTextGender;
    private Button buttonSave;

    // Initialization of Personal Info Screen and UI Actions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_screen);
        viewModel = new ViewModelProvider(this).get(PersonalInfoViewModel.class);

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

        // Bottom Navigation
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(PersonalInfoScreen.this, ShoppingListScreen.class);
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

    // Saving of User Personal Info
    private void savePersonalInfo() {
        String height = editTextHeight.getText().toString();
        String weight = editTextWeight.getText().toString();
        String gender = editTextGender.getText().toString();

        viewModel.savePersonalInfo(height, weight, gender, new PersonalInfoViewModel.AuthCallback() {
            // Handle successful saving of User Personal Info
            @Override
            public void onSuccess() {
                Toast.makeText(PersonalInfoScreen.this,
                        "Information saved", Toast.LENGTH_SHORT).show();
                finish();
            }

            // Handle failure to save User Personal Info
            @Override
            public void onFailure(String error) {
                Toast.makeText(PersonalInfoScreen.this,
                        "Save fail:  " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Loading of User Personal Info
    private void loadPersonalInfo() {
        viewModel.loadPersonalInfo(new PersonalInfoViewModel.LoadPersonalInfoCallback() {
            // Handle successful loading of User Personal Info and Update UI
            @Override
            public void onSuccess(Map<String, Object> personalInfo) {
                editTextHeight.setText(personalInfo.get("height").toString());
                editTextWeight.setText(personalInfo.get("weight").toString());
                editTextGender.setText(personalInfo.get("gender").toString());
                Toast.makeText(PersonalInfoScreen.this,
                        "Information saved", Toast.LENGTH_SHORT).show();
            }

            // Handle failure to load User Personal Info and Update UI
            @Override
            public void onFailure(String error) {
                Toast.makeText(PersonalInfoScreen.this,
                        "Load fail:  " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}