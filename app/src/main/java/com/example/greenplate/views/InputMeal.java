package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.example.greenplate.viewmodels.PersonalInfoViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Map;

public class InputMeal extends AppCompatActivity {
    private InputMealViewModel viewModel;
    private BottomNavigationView bottomNavigation;
    private Button buttonAdd;
    private EditText editTextMealName;
    private EditText editTextCalories;
    private TextView textViewPersonalInfo;
    private TextView textViewCalorieGoal;
    private TextView textViewCurrentCalorieIntake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_meal);

        viewModel = new ViewModelProvider(this).get(InputMealViewModel.class);

        editTextMealName = findViewById(R.id.editTextMealName);
        editTextCalories = findViewById(R.id.editTextCalories);
        buttonAdd = findViewById(R.id.buttonAdd);
        textViewPersonalInfo = findViewById(R.id.textViewPersonalInfo);
        textViewCalorieGoal = findViewById(R.id.textViewCalorieGoal);
        textViewCurrentCalorieIntake = findViewById(R.id.textViewCurrentCalorieIntake);
        loadPersonalInfo();
        calculateCalorieGoal();
        calculateCalorieIntake();


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

    private void calculateCalorieGoal() {
        viewModel.calculateCalorieGoal(new InputMealViewModel.CalculateCalorieGoalCallback() {
            @Override
            public void onSuccess(int calorieGoal) {
                //Handle successful calculation of calorie goal
                //update UI
                textViewCalorieGoal.setText(String.format("Daily Calorie Goal: %d", calorieGoal));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InputMeal.this,
                        "Load fail:  " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateCalorieIntake() {
        viewModel.calculateTotalCalories(new InputMealViewModel.CalculateCalorieIntakeCallback() {
            @Override
            public void onSuccess(int calorieIntake) {
                //Handle successful calculation of calorie goal
                //update UI
                textViewCurrentCalorieIntake.setText(String.format("Current Intake: %d", calorieIntake));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InputMeal.this,
                        "Load fail:  " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPersonalInfo() {
        viewModel.loadPersonalInfo(new InputMealViewModel.LoadPersonalInfoCallback() {
            @Override
            public void onSuccess(Map<String, Object> personalInfo) {
                // Handle successful loading of personal information
                // Update UI or perform any other actions
                String height = personalInfo.get("height").toString();
                String weight = personalInfo.get("weight").toString();
                String gender = personalInfo.get("gender").toString();
                textViewPersonalInfo.setText(String.format("Height: %s\" Weight: %slbs Gender: %s", height, weight, gender));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InputMeal.this,
                        "Load fail:  " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}