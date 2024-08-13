package com.dns30.rentalar_rayyan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageButton tvMotor, tvSewa, tvPenyewa, tvRental;
    private TextView tvini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views using View Binding
        tvMotor = findViewById(R.id.btnMotor);
        tvSewa = findViewById(R.id.btnSewa);
        tvPenyewa = findViewById(R.id.btnPenyewa);
        tvRental = findViewById(R.id.btnRental);
        tvini = findViewById(R.id.tvDate);

        // Set today's date
        displayTodayDate();

        // Set up button click listeners
        setupButtonListeners();
    }

    private void displayTodayDate() {
        LocalDate today = LocalDate.now();

        DayOfWeek dayOfWeek = today.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("id-ID"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        String formattedDate = today.format(formatter);

        String displayDate = String.format("%s, %s", dayName, formattedDate);
        tvini.setText(displayDate);
    }

    private void setupButtonListeners() {
        tvMotor.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, DataMotorActivity.class);
            startActivity(i);
        });

        tvSewa.setOnClickListener(v -> {
            Intent j = new Intent(MainActivity.this, SewaMotorActivity.class);
            startActivity(j);
        });

        tvPenyewa.setOnClickListener(v -> {
            Intent k = new Intent(MainActivity.this, Penyewa1Activity.class);
            startActivity(k);
        });

        tvRental.setOnClickListener(v -> {
            Intent l = new Intent(MainActivity.this, RentalActivity.class);
            startActivity(l);
        });
    }
}
