package com.example.greenplate.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.lifecycle.ViewModelProvider;

import com.anychart.APIlib;
import com.anychart.charts.Cartesian;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.data.Set;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Align;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

public class InputMeal extends AppCompatActivity {
    private InputMealViewModel viewModel;
    private BottomNavigationView bottomNavigation;
    private Button buttonAdd;
    private Button viewCaloriesDone;
    private Button buttonLineChart;
    private EditText editTextMealName;
    private EditText editTextCalories;
    private TextView textViewPersonalInfo;
    private TextView textViewCalorieGoal;
    private TextView textViewCurrentCalorieIntake;
    private AnyChartView anyChartView;
    private AnyChartView lineChartView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_meal);
        viewModel = new ViewModelProvider(this).get(InputMealViewModel.class);

        editTextMealName = findViewById(R.id.editTextMealName);
        editTextCalories = findViewById(R.id.editTextCalories);
        buttonAdd = findViewById(R.id.buttonAdd);
        viewCaloriesDone = findViewById((R.id.viewCaloriesDone));
        buttonLineChart = findViewById((R.id.buttonLineChart));
        textViewPersonalInfo = findViewById(R.id.textViewPersonalInfo);
        textViewCalorieGoal = findViewById(R.id.textViewCalorieGoal);
        textViewCurrentCalorieIntake = findViewById(R.id.textViewCurrentCalorieIntake);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        anyChartView = findViewById(R.id.any_chart_view);
        lineChartView = findViewById(R.id.line_chart_view);

        loadPersonalInfo();
        calculateCalorieIntake();
        calculateCalorieGoal();

        viewCaloriesDone.setOnClickListener(v -> {
            anyChartView.setVisibility(View.VISIBLE);
            lineChartView.setVisibility(View.GONE);
            loadPieChart();
        });

        buttonLineChart.setOnClickListener(v -> {
            anyChartView.setVisibility(View.GONE);
            lineChartView.setVisibility(View.VISIBLE);
            loadLineGraph();
        });

        buttonAdd.setOnClickListener(v -> {
            String mealName = editTextMealName.getText().toString();
            String caloriesString = editTextCalories.getText().toString();
            createMeal(mealName, caloriesString);
        });

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shoppingnav) {
                    Intent intent = new Intent(InputMeal.this, ShoppingListScreen.class);
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
                textViewCurrentCalorieIntake.setText(
                        String.format("Current Intake: %d", calorieIntake));
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
                textViewPersonalInfo.setText(String.format("Height: %s\" Weight: %slbs Gender: %s",
                        height, weight, gender));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InputMeal.this,
                        "Load fail:  " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPieChart() {
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
        Pie pie = AnyChart.pie();
        pie.animation(true);
        int calorieGoal = viewModel.getCalorieGoal();
        int totalCalories = viewModel.getTotalCalories();
        int remainingCalories = calorieGoal - totalCalories;
        remainingCalories = Math.max(remainingCalories, 0);
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Calories Eaten", totalCalories));
        data.add(new ValueDataEntry("Calories Remaining", remainingCalories));
        pie.data(data);
        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Calories Eaten Today")
                .padding(0d, 0d, 5d, 0d);
        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);
    }

    private void loadLineGraph() {
        APIlib.getInstance().setActiveAnyChartView(lineChartView);
        QuerySnapshot meals = viewModel.getMeals();

        Cartesian cartesian = AnyChart.line();

        // Set animation for the chart
        cartesian.animation(true);

        // Set padding for the chart
        cartesian.padding(10d, 20d, 5d, 20d);

        // Set chart title
        cartesian.title("Total Calories");

        // Set y-axis title
        cartesian.yAxis(0).title("Total Calories");

        // Set x-axis title
        cartesian.xAxis(0).title("Time");

        // Define series data for the chart
        List<DataEntry> seriesData = new ArrayList<>();
        // Everyone starts at 0 calories
        seriesData.add(new ValueDataEntry("00:00", 0));

        // Initialize a list to store tuples of time and total calories
        List<Pair<String, Integer>> timeCaloriesList = new ArrayList<>();

        // Iterate through each meal document
        for (DocumentSnapshot document : meals) {
            Map<String, Object> mealData = document.getData();
            if (mealData != null && mealData.containsKey("calories") && mealData.containsKey("time")) {
                String time = mealData.get("time").toString();
                int calories = Integer.parseInt(mealData.get("calories").toString());

                // Add the tuple (time, calories) to the list
                timeCaloriesList.add(new Pair<>(time, calories));
            }
        }

        // Sort the list of tuples based on the time
        Collections.sort(timeCaloriesList, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> pair1, Pair<String, Integer> pair2) {
                return pair1.first.compareTo(pair2.first);
            }
        });

        // Iterate through the sorted list and populate the seriesData list
        int totalCalories = 0;
        for (Pair<String, Integer> pair : timeCaloriesList) {
            totalCalories += pair.second; // Add calories to the total
            seriesData.add(new ValueDataEntry(pair.first, totalCalories));
        }

        // Create a set of data
        Set set = Set.instantiate();
        set.data(seriesData);

        // Map the data to x and value properties
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

        // Add the series to the chart
        Line series1 = cartesian.line(series1Mapping);
        series1.name("Calories");

        lineChartView.setChart(cartesian);
        }

}