package com.example.greenplate.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.greenplate.R;

public class MainActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInputMeal();
            }
        });
    }

    public void openShoppingList() {
        Intent intent = new Intent(this, UserLogin.class);
        startActivity(intent);
    }

    public void openInputMeal() {
        Intent intent = new Intent(this, InputMeal.class);
        startActivity(intent);
    }


}