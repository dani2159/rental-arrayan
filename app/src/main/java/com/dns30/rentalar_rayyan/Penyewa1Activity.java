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
import android.widget.ImageButton;
import android.widget.ListView;

public class Penyewa1Activity extends AppCompatActivity {

    private String[] daftar;
    private ListView ls2;
    private DataHelper dbHelper;
    public static Penyewa1Activity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penyewa1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Data Penyewa");

        // Initialize components
        ImageButton fab = findViewById(R.id.btnA2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Penyewa1Activity.this, TambahPenyewaActivity.class);
                startActivity(intent);
            }
        });

        // Initialize DataHelper
        ma = this;
        dbHelper = new DataHelper(this);
        RefreshList1();
    }

    @SuppressLint("Range")
    protected void RefreshList1() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM penyewa", null);

        try {
            daftar = new String[cursor.getCount()];
            cursor.moveToFirst();
            for (int cc = 0; cc < cursor.getCount(); cc++) {
                cursor.moveToPosition(cc);
                daftar[cc] = cursor.getString(cursor.getColumnIndex("id")) + " - " + cursor.getString(cursor.getColumnIndex("nama"));
            }

            ls2 = findViewById(R.id.ls2);
            ls2.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, daftar));
            ls2.setSelected(true);
            ls2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final String selection = daftar[position];
                    final String penyewaId = selection.substring(0, selection.indexOf(" - "));
                    final CharSequence[] dialogItems = {"Lihat Data Penyewa", "Ubah Data Penyewa", "Hapus Data Penyewa"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(Penyewa1Activity.this);
                    builder.setTitle("Pilihan");
                    builder.setItems(dialogItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0: // View data
                                    Intent viewIntent = new Intent(getApplicationContext(), LihatPenyewaActivity.class);
                                    viewIntent.putExtra("id", penyewaId);
                                    startActivity(viewIntent);
                                    break;
                                case 1: // Edit data
                                    Intent editIntent = new Intent(getApplicationContext(), UbahPenyewaActivity.class);
                                    editIntent.putExtra("id", penyewaId);
                                    startActivity(editIntent);
                                    break;
                                case 2: // Delete data
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    db.execSQL("DELETE FROM penyewa WHERE id = ?", new String[]{penyewaId});
                                    RefreshList1();
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            });

        } finally {
            cursor.close();
            db.close();
        }

        ((ArrayAdapter<?>) ls2.getAdapter()).notifyDataSetInvalidated();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
