package com.dns30.rentalar_rayyan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailMotorActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private TextView tMotor, tHarga, tNopol;
    private ImageView iGambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_motor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detail Motor");

        // Initialize UI components
        tMotor = findViewById(R.id.Tmotor);
        tHarga = findViewById(R.id.Tharga);
        tNopol = findViewById(R.id.Tnopol);
        iGambar = findViewById(R.id.Imotor);

        // Get motor name from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String motorName = extras.getString("namo");

            // Initialize database helper
            dbHelper = new DataHelper(this);
            displayMotorDetails(motorName);
        }
    }

    @SuppressLint("Range")
    private void displayMotorDetails(String motorName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM motor WHERE namo = ?", new String[]{motorName});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                 String sMotor = cursor.getString(cursor.getColumnIndex("namo"));
                String sHarga = cursor.getString(cursor.getColumnIndex("harga"));
                String sNopol = cursor.getString(cursor.getColumnIndex("nopol"));

                // Determine image resource based on motor name
                String imageName = getImageName(sMotor);

                tMotor.setText(sMotor);
                tNopol.setText(sNopol);
                tHarga.setText("Rp. " + sHarga);
                iGambar.setImageResource(getResources().getIdentifier(imageName, "drawable", getPackageName()));
            }
            cursor.close();
        }
    }

    private String getImageName(String motorName) {
        switch (motorName) {
            case "Supra X 125":
                return "supra";
            case "Vario 150 FI":
                return "vario";
            case "Genio":
                return "genio";
            case "Beat":
                return "beat";
            case "Mio":
                return "mio";
            case "Xabre":
                return "xabre";
            case "Sonic":
                return "sonic";
            case "Mega Pro":
                return "megapro";
            case "Piagio":
                return "piagio";
            case "Revo":
                return "revo";
            default:
                return "default_image"; // A default image if none of the names match
        }
    }
}
