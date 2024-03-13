package com.example.greenplate;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.greenplate.viewmodels.AccountCreateViewModel;

public class AccountCreateViewModelTest {
    @Test
    public void createUser_WithEmptyEmailAndEmptyPassword_FailureCallbackInvoked() {
        // Arrange
        AccountCreateViewModel viewModel = new AccountCreateViewModel();
        TestAuthCallback callback = new TestAuthCallback();

        // Act
        viewModel.createUser("", "", callback);

        // Assert
        assertTrue(callback.failureCalled);
        assertEquals("Email and password are required", callback.failureMessage);
    }

    @Test
    public void createUser_WithWhitespaceInPassword_FailureCallbackInvoked() {
        // Arrange
        AccountCreateViewModel viewModel = new AccountCreateViewModel();
        TestAuthCallback callback = new TestAuthCallback();

        // Act
        viewModel.createUser("test@example.com", "pass word", callback);

        // Assert
        assertTrue(callback.failureCalled);
        assertEquals("Password cannot contain whitespace", callback.failureMessage);
    }

    // Define a test callback implementation to capture the callback results
    private static class TestAuthCallback implements AccountCreateViewModel.AuthCallback {
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
