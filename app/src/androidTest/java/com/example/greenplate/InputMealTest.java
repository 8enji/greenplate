package com.example.greenplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.greenplate.viewmodels.InputMealViewModel;

import org.junit.Test;

public class InputMealTest {
    @Test
    public void createMeal_WithEmptyMealNameAndCalories_FailureCallbackInvoked() {
        // Arrange
        InputMealViewModel viewModel = new InputMealViewModel();
        TestAuthCallback callback = new TestAuthCallback();

        // Act
        viewModel.createMeal("", "", callback);

        // Assert
        assertTrue(callback.failureCalled);
        assertEquals("Meal Name and Calories are required", callback.failureMessage);
    }

    @Test
    public void createMeal_WithInvalidCalories_FailureCallbackInvoked() {
        // Arrange
        InputMealViewModel viewModel = new InputMealViewModel();
        TestAuthCallback callback = new TestAuthCallback();

        // Act
        viewModel.createMeal("Salad", "-100", callback); // Negative calories as an invalid input

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

