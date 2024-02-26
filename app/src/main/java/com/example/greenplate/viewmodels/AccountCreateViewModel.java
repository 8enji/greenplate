package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class AccountCreateViewModel extends ViewModel {
    private FirebaseAuth mAuth;

    public AccountCreateViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void createUser(String email, String password, AuthCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onFailure("Email and password are required");
            return;
        }
        if (password.contains(" ")) {
            // Password contains whitespace
            callback.onFailure("Password cannot contain whitespace");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Account creation successful
                        callback.onSuccess();
                    } else {
                        // Account creation failed
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}

