package com.dns30.rentalar_rayyan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_rental.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_PENYEWA = "penyewa";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAMA = "nama";
    private static final String COLUMN_ALAMAT = "alamat";
    private static final String COLUMN_NO_HP = "no_hp";

    private static final String TABLE_MOTOR = "motor";
    private static final String COLUMN_ID_MOTOR = "id_motor";
    private static final String COLUMN_NAMA_MOTOR = "namo";
    private static final String COLUMN_HARGA = "harga";
    private static final String COLUMN_NOPOL = "nopol";
    private static final String COLUMN_STATUS = "status";

    private static final String TABLE_SEWA = "sewa";
    private static final String COLUMN_ID_SEWA = "id_sewa";
    private static final String COLUMN_TGL = "tgl";
    private static final String COLUMN_ID_PENYEWA = "id";
    private static final String COLUMN_ID_MOTOR_FK = "id_motor";
    private static final String COLUMN_PROMO = "promo";
    private static final String COLUMN_LAMA = "lama";
    private static final String COLUMN_TOTAL = "total";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");

        // Create penyewa table
        String createPenyewaTable = "CREATE TABLE " + TABLE_PENYEWA + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAMA + " TEXT, "
                + COLUMN_ALAMAT + " TEXT, "
                + COLUMN_NO_HP + " TEXT);";
        Log.d("DataHelper", "onCreate: " + createPenyewaTable);
        db.execSQL(createPenyewaTable);

        // Create motor table
        String createMotorTable = "CREATE TABLE " + TABLE_MOTOR + " ("
                + COLUMN_ID_MOTOR + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAMA_MOTOR + " TEXT, "
                + COLUMN_HARGA + " INTEGER, "
                + COLUMN_NOPOL + " TEXT, "
                + COLUMN_STATUS + " TEXT);";
        Log.d("DataHelper", "onCreate: " + createMotorTable);
        db.execSQL(createMotorTable);

        // Create sewa table
        String createSewaTable = "CREATE TABLE " + TABLE_SEWA + " ("
                + COLUMN_ID_SEWA + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TGL + " TEXT, "
                + COLUMN_ID_PENYEWA + " INTEGER, "
                + COLUMN_ID_MOTOR_FK + " INTEGER, "
                + COLUMN_PROMO + " INTEGER, "
                + COLUMN_LAMA + " INTEGER, "
                + COLUMN_TOTAL + " DOUBLE, "
                + "FOREIGN KEY(" + COLUMN_ID_PENYEWA + ") REFERENCES " + TABLE_PENYEWA + "(" + COLUMN_ID + "), "
                + "FOREIGN KEY(" + COLUMN_ID_MOTOR_FK + ") REFERENCES " + TABLE_MOTOR + "(" + COLUMN_ID_MOTOR + "));";
        Log.d("DataHelper", "onCreate: " + createSewaTable);
        db.execSQL(createSewaTable);

        // Insert initial data
        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Supra X 125', 100000, 'D 6127 JJ', 'y');");
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Vario 150 FI', 90000, 'D 6128 JK', 'y');");
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Genio', 80000, 'D 6129 JL', 'y');");
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Beat', 80000, 'D 6130 JM', 'y');");
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Mio', 80000, 'D 6131 JN', 'y');");
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Revo', 90000, 'D 6132 JO', 'y');");
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Xabre', 120000, 'D 6133 JP', 'y');");
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Piagio', 100000, 'D 6134 JQ', 'y');");
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Mega Pro', 110000, 'D 6134 JR', 'y');");
        db.execSQL("INSERT INTO " + TABLE_MOTOR + " VALUES (null, 'Sonic', 100000, 'D 6135 JS', 'y');");

        db.execSQL("INSERT INTO " + TABLE_PENYEWA + " VALUES (null, 'Dani Setiawan', 'Bandung', '089616169308');");
    }

    @SuppressLint("Range")
    public List<String> semuaMotor() {
        List<String> motors = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MOTOR + " WHERE " + COLUMN_STATUS + " = 'y'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                motors.add(cursor.getString(cursor.getColumnIndex(COLUMN_ID_MOTOR)) + " - " + cursor.getString(cursor.getColumnIndex(COLUMN_NAMA_MOTOR)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return motors;
    }

    @SuppressLint("Range")
    public List<String> semuaPenyewa() {
        List<String> penyewas = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PENYEWA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                penyewas.add(cursor.getString(cursor.getColumnIndex(COLUMN_ID)) + " - " + cursor.getString(cursor.getColumnIndex(COLUMN_NAMA)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return penyewas;
    }

    @SuppressLint("Range")
    public List<String> findMotorByName(String name) {
        List<String> motors = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MOTOR + " WHERE " + COLUMN_NAMA_MOTOR + " LIKE ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + name + "%"});

        if (cursor.moveToFirst()) {
            do {
                motors.add(cursor.getString(cursor.getColumnIndex(COLUMN_ID_MOTOR)) + " - " + cursor.getString(cursor.getColumnIndex(COLUMN_NAMA_MOTOR)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return motors;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENYEWA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEWA);

        // Create tables again
        onCreate(db);
    }
}
