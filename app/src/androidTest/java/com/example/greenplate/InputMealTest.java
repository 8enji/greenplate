package com.example.greenplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.greenplate.viewmodels.InputMealViewModel;

import org.junit.Test;

public class InputMealTest {
    @Test
    public void createMeal_NoPrevMeals() {
        // Arrange
        InputMealViewModel viewModel = new InputMealViewModel();
        TestAuthCallback callback = new TestAuthCallback();

        // Act
        viewModel.createMeal("", "", callback);

        // Assert
        assertTrue(callback.failureCalled);
        assertEquals("Failed to calculate total calories: ", callback.failureMessage);
    }

    @Test
    public void createMeal_WithNegativeValidCalories() {
        // Arrange
        InputMealViewModel viewModel = new InputMealViewModel();
        TestAuthCallback callback = new TestAuthCallback();

        // Act
        viewModel.createMeal("Chicken", "-300", callback); // Negative calories as an invalid input

        // Assert
        assertTrue(callback.failureCalled);
        assertEquals("Calories cannot be negative", callback.failureMessage);
    }

    // Define a test callback implementation to capture the callback results
    private static class TestAuthCallback implements InputMealViewModel.AuthCallback {
        boolean successCalled = false;
        boolean failureCalled = false;
        String failureMessage;

        @Override
        public void onSuccess() {
            successCalled = true;
        }

        @Override
        public void onFailure(String error) {
            failureCalled = true;
            failureMessage = error;
        }
    }
}