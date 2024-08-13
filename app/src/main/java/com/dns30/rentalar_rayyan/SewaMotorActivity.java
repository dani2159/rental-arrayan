package com.dns30.rentalar_rayyan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SewaMotorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView lama;
    private RadioGroup promo;
    private RadioButton weekday, weekend;
    private Button sewa, tambah, kurang;

    private String idMotor, idPenyewa, tanggal, sPromo;
    private Spinner spinnerMotor, spinnerPenyewa;
    private DataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewa_motor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sewa Motor");

        dbHelper = new DataHelper(this);

        initializeViews();
        setupListeners();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        tanggal = df.format(c.getTime());

        loadSpinnerDataMotor();
        loadSpinnerDataPenyewa();
    }

    private void initializeViews() {
        spinnerMotor = findViewById(R.id.spinner);
        spinnerPenyewa = findViewById(R.id.spinner1);
        sewa = findViewById(R.id.btnSewa);
        promo = findViewById(R.id.promoGroup);
        weekday = findViewById(R.id.rbWeekDay);
        weekend = findViewById(R.id.rbWeekEnd);
        lama = findViewById(R.id.lmsw);
        tambah = findViewById(R.id.bttambah);
        kurang = findViewById(R.id.btkurang);
    }

    private void setupListeners() {
        tambah.setOnClickListener(v -> adjustLama(1));
        kurang.setOnClickListener(v -> adjustLama(-1));
        sewa.setOnClickListener(v -> handleSewaClick());

        spinnerMotor.setOnItemSelectedListener(this);
        spinnerPenyewa.setOnItemSelectedListener(this);
    }

    private void adjustLama(int amount) {
        try {
            int currentLama = Integer.parseInt(lama.getText().toString());
            int newLama = Math.max(currentLama + amount, 1); // Ensure lama is at least 1
            lama.setText(String.valueOf(newLama));
        } catch (NumberFormatException e) {
            lama.setText("1"); // Default to 1 if parsing fails
        }
    }

    private void handleSewaClick() {
        if (Integer.parseInt(lama.getText().toString()) < 1) {
            Toast.makeText(SewaMotorActivity.this, "Lama Sewa harus lebih dari 0", Toast.LENGTH_SHORT).show();
            return;
        }

        sPromo = weekday.isChecked() ? "0.25" : weekend.isChecked() ? "0.1" : "0.0";

        Intent sw = new Intent(getApplicationContext(), CetakSewaActivity.class);
        sw.putExtra("idp", idPenyewa);
        sw.putExtra("idm", idMotor);
        sw.putExtra("tgl", tanggal);
        sw.putExtra("po", sPromo);
        sw.putExtra("m", lama.getText().toString());
        startActivity(sw);
    }

    private void loadSpinnerDataMotor() {
        List<String> motorList = dbHelper.semuaMotor();
        ArrayAdapter<String> motorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, motorList);
        motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMotor.setAdapter(motorAdapter);
    }

    private void loadSpinnerDataPenyewa() {
        List<String> penyewaList = dbHelper.semuaPenyewa();
        ArrayAdapter<String> penyewaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, penyewaList);
        penyewaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPenyewa.setAdapter(penyewaAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner) {
            idMotor = extractId(parent.getItemAtPosition(position).toString());
        } else if (parent.getId() == R.id.spinner1) {
            idPenyewa = extractId(parent.getItemAtPosition(position).toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle case where nothing is selected, if needed
    }

    private String extractId(String item) {
        // Extract ID from the item string (assuming ID is the first 6 characters)
        return item.length() >= 6 ? item.substring(0, 6) : "";
    }
}
