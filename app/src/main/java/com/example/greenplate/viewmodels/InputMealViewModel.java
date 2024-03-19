package com.example.greenplate.viewmodels;

import com.example.greenplate.model.FirebaseDB;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class InputMealViewModel extends ViewModel {
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;

    private int calorieGoal;
    private int totalCalories;
    private QuerySnapshot meals;
    public InputMealViewModel() {
        db = FirebaseDB.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        userRef = db.collection("users").document(email);
    }

    public void createMeal(String mealName, String calories, AuthCallback callback) {
        if (mealName.isEmpty() || calories.isEmpty()) {
            //null checking
            callback.onFailure("Meal Name and Calories are required");
            return;
        }

        if (calories.contains(" ")) {
            //calories will not parse to an int
            callback.onFailure("Calories cannot contain whitespace");
            return;
        }
        if (calories.contains("-")) {
            //calories cannot be negative
            callback.onFailure("Calories cannot be negative");
            return;
        }
        try {
            // Attempt to parse the calories string to an integer
            int caloriesInt = Integer.parseInt(calories);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(" HH:mm");
            LocalDateTime now = LocalDateTime.now();
            String time = dtf.format(now);

            Map<String, Object> meal = new HashMap<>();
            meal.put("name", mealName);
            meal.put("calories", caloriesInt);
            meal.put("time", time);

            userRef.collection("meals")
                    .add(meal)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Success
                            callback.onSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failure
                            callback.onFailure("Failed to add meal: " + e.getMessage());
                        }
                    });



        } catch (NumberFormatException e) {
            // Parse Fails
            callback.onFailure("Calories must be a valid number");
            return;
        }
    }

    public void loadPersonalInfo(InputMealViewModel.LoadPersonalInfoCallback callback) {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> personalInfo = document.getData();
                    if (personalInfo.get("height") != null) {
                        callback.onSuccess(personalInfo);
                    }
                } else {
                    callback.onFailure("Document does not exist");
                }
            } else {
                callback.onFailure("Failed to load personal info: "
                        + task.getException().getMessage());
            }
        });
    }

    public void calculateCalorieGoal(InputMealViewModel.CalculateCalorieGoalCallback callback) {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> personalInfo = document.getData();
                    if (personalInfo.get("height") != null) {
                        String height = personalInfo.get("height").toString();
                        String weight = personalInfo.get("weight").toString();
                        String gender = personalInfo.get("gender").toString();
                        if (gender.equalsIgnoreCase("Male")) {
                            //activity level constant 1.5 (middle)
                            //BMR calculated with Mifflin-St Jeor Equation, 1.5 * BMR
                            double bmr = (10 * Integer.parseInt(weight))
                                    + (6.25 * Integer.parseInt(height))
                                    - (5 * 20) + 5;
                            //uses age 20
                            calorieGoal = (int) (1.5 * bmr);
                        } else {
                            double bmr = (10 * Integer.parseInt(weight))
                                    + (6.25 * Integer.parseInt(height))
                                    - (5 * 20) - 161;  //uses age 20
                            calorieGoal = (int) (1.5 * bmr);
                        }
                        callback.onSuccess(calorieGoal);
                    }
                } else {
                    callback.onFailure("Document does not exist");
                }
            } else {
                callback.onFailure("Failed to load personal info: "
                        + task.getException().getMessage());
            }
        });
    }

    public void calculateTotalCalories(CalculateCalorieIntakeCallback callback) {
        userRef.collection("meals").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                totalCalories = 0;
                meals = task.getResult();
                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Object> mealData = document.getData();
                    // Traverses through each meal document
                    if (mealData != null && mealData.containsKey("calories")) {
                        int calories = Integer.parseInt(mealData.get("calories").toString());
                        totalCalories += calories;
                    }
                }
                callback.onSuccess(totalCalories);
            } else {
                callback.onFailure("Failed to calculate total calories: "
                        + task.getException().getMessage());
            }
        });
    }

    public int getCalorieGoal() {
        return  calorieGoal;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public QuerySnapshot getMeals() {
        return meals;
    }

    public interface LoadPersonalInfoCallback {
        void onSuccess(Map<String, Object> personalInfo);
        void onFailure(String error);
    }

    public interface CalculateCalorieGoalCallback {
        void onSuccess(int calorieGoal);
        void onFailure(String error);
    }

    public interface CalculateCalorieIntakeCallback {
        void onSuccess(int calorieIntake);
        void onFailure(String error);
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
