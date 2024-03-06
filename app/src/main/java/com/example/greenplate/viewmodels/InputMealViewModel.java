package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class InputMealViewModel extends ViewModel {
    private FirebaseAuth mAuth;

    public InputMealViewModel() { mAuth = FirebaseAuth.getInstance();}

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
        //*******************BENJI WILL UNCOMMENT WHEN HE IMPLEMENTS FIREBASE**********
        /**try {
            // Attempt to parse the calories string to an integer
            int caloriesInt = Integer.parseInt(calories);
            mAuth.createMealWithMealNameAndCalories(mealName, caloriesInt)
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //Meal added successfully
                    callback.OnSuccess();
                } else {
                    //Meal add failed
                    callback.onFailure(task.getException().getMessage());
                }
            })

        } catch (NumberFormatException e) {
            // Parse Fails
            callback.onFailure("Calories must be a valid number");
            return;
        }**/
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
