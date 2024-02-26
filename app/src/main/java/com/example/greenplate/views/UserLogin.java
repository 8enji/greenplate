package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.UserLoginViewModel;

import androidx.lifecycle.ViewModelProvider;


public class UserLogin extends AppCompatActivity {
    private UserLoginViewModel viewModel;
    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        viewModel = new ViewModelProvider(this).get(UserLoginViewModel.class);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonCreate = findViewById(R.id.buttonCreate);

        buttonCreate.setOnClickListener(v -> {
            Intent intent = new Intent(UserLogin.this, AccountCreateActivity.class);
            startActivity(intent);
        });

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        viewModel.loginUser(email, password, new UserLoginViewModel.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserLogin.this, HomeScreen.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(UserLogin.this, "Authentication failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
