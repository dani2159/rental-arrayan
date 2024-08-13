package com.dns30.rentalar_rayyan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LihatPenyewaActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private TextView txt1, txt2, txt3, txt4;
    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_penyewa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List Data Penyewa");

        // Initialize UI components
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        txt4 = findViewById(R.id.txt4);
        btn2 = findViewById(R.id.btn1);

        // Initialize database helper
        dbHelper = new DataHelper(this);

        // Load data for the selected penyewa
        loadPenyewaDetails();

        // Set up button click listener
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("Range")
    private void loadPenyewaDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String penyewaId = getIntent().getStringExtra("id"); // Assuming the intent extra is "id"
        Cursor cursor = db.rawQuery("SELECT * FROM penyewa WHERE id = ?", new String[]{penyewaId});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                txt1.setText(cursor.getString(cursor.getColumnIndex("id")));
                txt2.setText(cursor.getString(cursor.getColumnIndex("nama")));
                txt3.setText(cursor.getString(cursor.getColumnIndex("alamat")));
                txt4.setText(cursor.getString(cursor.getColumnIndex("no_hp")));
            }
            cursor.close();
        }
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
