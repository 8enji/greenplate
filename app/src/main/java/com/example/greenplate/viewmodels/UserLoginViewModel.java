package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

public class UserLoginViewModel extends ViewModel {
    private FirebaseAuth mAuth;

    public UserLoginViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onFailure("Email and password are required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        callback.onSuccess();
                    } else {
                        // Login failed
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
