package com.example.greenplate.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.AccountCreateViewModel;

public class AccountCreateActivity extends AppCompatActivity {
    private AccountCreateViewModel viewModel;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonCreate;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create);

        viewModel = new ViewModelProvider(this).get(AccountCreateViewModel.class);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonBack = findViewById(R.id.buttonBack);

        buttonCreate.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            createUser(email, password);
        });

        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void createUser(String email, String password) {
        viewModel.createUser(email, password, new AccountCreateViewModel.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AccountCreateActivity.this,
                        "Account created", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AccountCreateActivity.this,
                        "Account creation failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

