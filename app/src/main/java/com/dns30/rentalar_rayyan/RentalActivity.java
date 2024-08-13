package com.dns30.rentalar_rayyan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class RentalActivity extends AppCompatActivity {

    private String[] daftar;
    private ListView ls3;
    private DataHelper dbHelper;
    public static RentalActivity mi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Data Rental");

        mi = this;
        dbHelper = new DataHelper(this);
        RefreshList2();
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    @SuppressLint("Range")
    public void RefreshList2() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT motor.id_motor, sewa.id_sewa, sewa.tgl, penyewa.nama, motor.namo, sewa.promo, sewa.lama, sewa.total " +
                    "FROM sewa " +
                    "JOIN penyewa ON sewa.id = penyewa.id " +
                    "JOIN motor ON sewa.id_motor = motor.id_motor " +
                    "ORDER BY sewa.id_sewa DESC", null);

            daftar = new String[cursor.getCount()];
            cursor.moveToFirst();
            for (int cc = 0; cc < cursor.getCount(); cc++) {
                cursor.moveToPosition(cc);
                String idMotor = cursor.getString(cursor.getColumnIndex("id_motor"));
                String idSewa = cursor.getString(cursor.getColumnIndex("id_sewa"));
                String tanggal = cursor.getString(cursor.getColumnIndex("tgl"));
                String namaPenyewa = cursor.getString(cursor.getColumnIndex("nama"));
                String namaMotor = cursor.getString(cursor.getColumnIndex("namo"));
                String promo = cursor.getString(cursor.getColumnIndex("promo"));
                String lama = cursor.getString(cursor.getColumnIndex("lama"));
                String total = formatRupiah(cursor.getDouble(cursor.getColumnIndex("total")));

                daftar[cc] = String.format("Kode Sewa: %s\nTanggal: %s\n%s Menyewa Motor %s\nSelama %s hari, dengan potongan Harga sebanyak %s%%\nTotal Pembayaran: %s",
                        idSewa, tanggal, namaPenyewa, namaMotor, lama, promo, total);
            }

            ls3 = findViewById(R.id.ls3);
            ls3.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, daftar));
            ls3.setSelected(true);
            ls3.setOnItemClickListener((parent, view, position, id) -> {
                final String selection = daftar[position];
                final String idSewa = extractIdSewa(selection);
                final String idMotor = extractIdMotor(selection);

                final CharSequence[] dialogItems = {"Motor Kembali", "Hapus Data"};
                AlertDialog.Builder builder = new AlertDialog.Builder(RentalActivity.this);
                builder.setTitle("Pilihan");
                builder.setItems(dialogItems, (dialog, item) -> {
                    SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                    switch (item) {
                        case 0: // Motor kembali
                            dbWrite.execSQL("UPDATE motor SET status = 'y' WHERE id_motor = ?", new String[]{idMotor});
                            RefreshList2();
                            Toast.makeText(getApplicationContext(), "Motor Telah Dikembalikan", Toast.LENGTH_SHORT).show();
                            break;

                        case 1: // Hapus data
                            dbWrite.execSQL("DELETE FROM sewa WHERE id_sewa = ?", new String[]{idSewa});
                            RefreshList2();
                            Toast.makeText(getApplicationContext(), "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    dbWrite.close();
                });
                builder.create().show();
            });

            ((ArrayAdapter<?>) ls3.getAdapter()).notifyDataSetInvalidated();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    private String extractIdSewa(String selection) {
        return selection.substring(selection.indexOf("Kode Sewa: ") + 12, selection.indexOf("\n", selection.indexOf("Kode Sewa: ") + 12));
    }

    private String extractIdMotor(String selection) {
        return selection.substring(selection.indexOf("Kode Motor: ") + 12, selection.indexOf("\n", selection.indexOf("Kode Motor: ") + 12));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean shouldRefresh = getIntent().getBooleanExtra("refresh", false);
        if (shouldRefresh) {
            RefreshList2();
        }
    }

}
