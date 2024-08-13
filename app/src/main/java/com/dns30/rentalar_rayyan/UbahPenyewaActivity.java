package com.dns30.rentalar_rayyan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.text.TextUtils.isEmpty;

public class UbahPenyewaActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private Button btnSave, btnCancel;
    private EditText etId, etNama, etAlamat, etNoHp;
    private String penyewaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_penyewa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Ubah Data Penyewa");

        dbHelper = new DataHelper(this);

        initializeViews();
        loadPenyewaData();
        setupListeners();
    }

    private void initializeViews() {
        etId = findViewById(R.id.etA);
        etNama = findViewById(R.id.etB);
        etAlamat = findViewById(R.id.etC);
        etNoHp = findViewById(R.id.etD);

        btnSave = findViewById(R.id.btnA1);
        btnCancel = findViewById(R.id.btnA2);
    }

    private void loadPenyewaData() {
        penyewaId = getIntent().getStringExtra("nama");

        if (penyewaId == null || penyewaId.isEmpty()) {
            Toast.makeText(this, "ID Penyewa tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM penyewa WHERE id = ?", new String[]{penyewaId});

        if (cursor.moveToFirst()) {
            etId.setText(cursor.getString(0));
            etNama.setText(cursor.getString(1));
            etAlamat.setText(cursor.getString(2));
            etNoHp.setText(cursor.getString(3));
        } else {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }

        cursor.close();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            if (isInputValid()) {
                updatePenyewaData();
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

    private void updatePenyewaData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nama", etNama.getText().toString());
        values.put("alamat", etAlamat.getText().toString());
        values.put("no_hp", etNoHp.getText().toString());

        int rowsUpdated = db.update("penyewa", values, "id = ?", new String[]{etId.getText().toString()});

        if (rowsUpdated > 0) {
            Toast.makeText(getApplicationContext(), "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
            Penyewa1Activity.ma.RefreshList1();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Data Gagal diubah", Toast.LENGTH_SHORT).show();
        }
    }
}
