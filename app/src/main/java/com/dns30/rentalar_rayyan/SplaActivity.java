package com.dns30.rentalar_rayyan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spla);

        // Using Handler with a lambda expression for a cleaner look
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplaActivity.this, MainActivity.class));
            finish();
        }, 3000);
    }
}

