package com.dns30.rentalar_rayyan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TambahPenyewaActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private Button btnSave, btnCancel;
    private EditText etId, etNama, etAlamat, etNoHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_penyewa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah Data Penyewa");

        dbHelper = new DataHelper(this);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etId = findViewById(R.id.et1);
        etNama = findViewById(R.id.et2);
        etAlamat = findViewById(R.id.et3);
        etNoHp = findViewById(R.id.et4);

        btnSave = findViewById(R.id.btnTambah);
        btnCancel = findViewById(R.id.btnB);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            if (isInputValid()) {
                saveData();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private boolean isInputValid() {
        String id = etId.getText().toString();
        String nama = etNama.getText().toString();
        String alamat = etAlamat.getText().toString();
        String noHp = etNoHp.getText().toString();

        if (isEmpty(nama) || isEmpty(alamat) || isEmpty(noHp)) {
            Toast.makeText(this, "Data Tidak Boleh Kosong!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (id.length() < 5) {
            Toast.makeText(this, "ID Penyewa harus diisi 5 Karakter Angka!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", etId.getText().toString());
        values.put("nama", etNama.getText().toString());
        values.put("alamat", etAlamat.getText().toString());
        values.put("no_hp", etNoHp.getText().toString());

        long result = db.insert("penyewa", null, values);

        if (result != -1) {
            Toast.makeText(getApplicationContext(), "Data Berhasil dimasukan", Toast.LENGTH_SHORT).show();
            Penyewa1Activity.ma.RefreshList1();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Data Gagal dimasukan", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
