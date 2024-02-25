package com.example.greenplate.views;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.greenplate.R;

public class SplashScreen extends AppCompatActivity {

    // Splash Screen Implementation Variables
    Handler handler = new Handler();
    Intent intent = new Intent(SplashScreen.this, UserLogin.class);
    int milliDelay = 3000;

    // Splash Screen Implementation Function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, milliDelay);

    }
}