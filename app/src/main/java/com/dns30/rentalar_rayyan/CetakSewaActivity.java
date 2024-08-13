package com.dns30.rentalar_rayyan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CetakSewaActivity extends AppCompatActivity {

    private TextView idpenyewa, nama, notelp, alamat, idmotor, namo, lama, promo, total, tgl, harga, prtotal;
    private Button cetak;
    private String sTot, sTot1, sHarga, sPromo, sLama, sprTot;
    private int iLama, iHarga, iSub, iPromo;
    private double dPromo, dTotal, dsubP, dLama, dHarga;
    private Cursor cursor, cursor1;
    private DataHelper dbHelper;

    private Bitmap bitmap, scaleBitmap;
    private static final int PAGE_WIDTH = 1200;
    private Date dateTime;
    private DateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cetak_sewa);

        initializeViews();
        setupActionBar();
        initializeDatabase();
        loadDataFromIntent();
        calculateTotals();
        requestPermissions();
        createPDF();
    }

    private void initializeViews() {
        idpenyewa = findViewById(R.id.tvidPenyewa);
        nama = findViewById(R.id.tvnmPenyewa);
        notelp = findViewById(R.id.tvnoPenyewa);
        alamat = findViewById(R.id.tvalPenyewa);
        idmotor = findViewById(R.id.tvidMotor);
        namo = findViewById(R.id.tvMotor);
        lama = findViewById(R.id.tvLama);
        promo = findViewById(R.id.tvPromo);
        prtotal = findViewById(R.id.tvPrTotal);
        total = findViewById(R.id.tvTotal);
        tgl = findViewById(R.id.tvTgl);
        harga = findViewById(R.id.tvHarga);
        cetak = findViewById(R.id.btnPrint);
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Cetak Nota Pembayaran");
        }
    }

    private void initializeDatabase() {
        dbHelper = new DataHelper(this);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        tgl.setText(intent.getStringExtra("tgl"));
        idpenyewa.setText(intent.getStringExtra("idp"));
        idmotor.setText(intent.getStringExtra("idm"));
        lama.setText(intent.getStringExtra("m") + " Hari");
        sPromo = intent.getStringExtra("po");

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM motor WHERE id_motor = ?", new String[]{intent.getStringExtra("idm")});
        if (cursor.moveToFirst()) {
            namo.setText(cursor.getString(1));
            sHarga = cursor.getString(2);
            harga.setText(formatRupiah(Double.parseDouble(sHarga)));
        }
        cursor.close();

        cursor1 = db.rawQuery("SELECT * FROM penyewa WHERE id = ?", new String[]{intent.getStringExtra("idp")});
        if (cursor1.moveToFirst()) {
            nama.setText(cursor1.getString(1));
            alamat.setText(cursor1.getString(2));
            notelp.setText(cursor1.getString(3));
        }
        cursor1.close();
    }

    private void calculateTotals() {
        try {
            sHarga = sHarga != null ? sHarga.replaceAll("[\\D]", "") : "0";
            iHarga = Integer.parseInt(sHarga);
            dHarga = Double.parseDouble(sHarga);

            sLama = lama.getText().toString().replaceAll("[\\D]", "");
            iLama = Integer.parseInt(sLama);
            dLama = Double.parseDouble(sLama);

            iSub = iHarga * iLama;
            dPromo = sPromo != null ? Double.parseDouble(sPromo) : 0.0;
            dsubP = dPromo * dHarga * dLama;

            dTotal = iSub - dsubP;
            sprTot = formatRupiah(dsubP);
            sTot = formatRupiah(dTotal);

            prtotal.setText(sprTot);
            total.setText(sTot);

            iPromo = (int) (dPromo * 100);
            promo.setText(iPromo + "%");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error calculating totals: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }

    private void createPDF() {
        cetak.setOnClickListener(v -> {
            if (areFieldsValid()) {
                dateTime = new Date();

                PdfDocument pdfDocument = new PdfDocument();
                Paint paint = new Paint();
                Paint titlePaint = new Paint();

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, 2010, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                Canvas canvas = page.getCanvas();

                if (scaleBitmap != null && !scaleBitmap.isRecycled()) {
                    canvas.drawBitmap(scaleBitmap, 0, 0, paint);
                }

                titlePaint.setTextAlign(Paint.Align.CENTER);
                titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                titlePaint.setColor(Color.WHITE);
                titlePaint.setTextSize(70);
                canvas.drawText("Nota Pembayaran", PAGE_WIDTH / 2, 500, titlePaint);

                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.BLACK);
                paint.setTextSize(35f);
                canvas.drawText("Nama Penyewa: " + nama.getText(), 20, 590, paint);
                canvas.drawText("Nomor Tlp: " + notelp.getText(), 20, 640, paint);

                dateFormat = new SimpleDateFormat("yyMMddHHmm", Locale.getDefault());
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("No. Pesanan: " + dateFormat.format(dateTime), PAGE_WIDTH - 20, 590, paint);

                dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                canvas.drawText("Tanggal: " + dateFormat.format(dateTime), PAGE_WIDTH - 20, 640, paint);

                dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                canvas.drawText("Pukul: " + dateFormat.format(dateTime), PAGE_WIDTH - 20, 690, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                canvas.drawRect(20, 780, PAGE_WIDTH - 20, 860, paint);

                paint.setTextAlign(Paint.Align.LEFT);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText("ID Motor.", 40, 830, paint);
                canvas.drawText("Motor", 200, 830, paint);
                canvas.drawText("Harga", 700, 830, paint);
                canvas.drawText("Lama", 900, 830, paint);
                canvas.drawText("Total", 1050, 830, paint);

                canvas.drawLine(180, 790, 180, 840, paint);
                canvas.drawLine(680, 790, 680, 840, paint);
                canvas.drawLine(880, 790, 880, 840, paint);
                canvas.drawLine(1030, 790, 1030, 840, paint);

                canvas.drawText(idmotor.getText().toString(), 40, 950, paint);
                canvas.drawText(namo.getText().toString(), 200, 950, paint);
                canvas.drawText(harga.getText().toString(), 700, 950, paint);
                canvas.drawText(String.valueOf(iLama), 900, 950, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(formatRupiah(iSub), PAGE_WIDTH - 40, 950, paint);
                paint.setTextAlign(Paint.Align.LEFT);

                canvas.drawLine(400, 1200, PAGE_WIDTH - 20, 1200, paint);
                canvas.drawText("Sub Total", 700, 1250, paint);
                canvas.drawText(":", 900, 1250, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(formatRupiah(iSub), PAGE_WIDTH - 40, 1250, paint);

                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("PPN (" + sPromo + "%)", 700, 1300, paint);
                canvas.drawText(":", 900, 1300, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(prtotal.getText().toString(), PAGE_WIDTH - 40, 1300, paint);

                paint.setColor(Color.rgb(247, 147, 30));
                canvas.drawRect(680, 1350, PAGE_WIDTH - 20, 1450, paint);

                paint.setColor(Color.BLACK);
                paint.setTextSize(50f);
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Total", 700, 1415, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(sTot, PAGE_WIDTH - 40, 1415, paint);

                pdfDocument.finishPage(page);

                savePDF(pdfDocument);

                SQLiteDatabase dbH = dbHelper.getWritableDatabase();
                dbH.execSQL("INSERT INTO sewa (id_sewa, tgl, id, id_motor, promo, lama, total) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        new String[]{
                                dateFormat.format(dateTime),
                                tgl.getText().toString(),
                                getIntent().getStringExtra("idp"),
                                getIntent().getStringExtra("idm"),
                                sPromo,
                                String.valueOf(iLama),
                                sTot
                        });
                dbH.execSQL("UPDATE motor SET status = 'n' WHERE id_motor = ?", new String[]{getIntent().getStringExtra("idm")});
                RentalActivity.mi.RefreshList2();
                Toast.makeText(getApplicationContext(), "Penyewaan Motor Berhasil, dan Nota Pembayaran sudah dibuat", Toast.LENGTH_LONG).show();
                startActivity(new Intent(CetakSewaActivity.this, MainActivity.class));
            }
        });
    }

    private boolean areFieldsValid() {
        return !(idmotor.getText().toString().isEmpty() ||
                idpenyewa.getText().toString().isEmpty() ||
                lama.getText().toString().isEmpty() ||
                promo.getText().toString().isEmpty());
    }

    private void savePDF(PdfDocument pdfDocument) {
        dateTime = new Date();
        dateFormat = new SimpleDateFormat("yyMMddHHmm", Locale.getDefault());
        File file = new File(Environment.getExternalStorageDirectory(), "/" + dateFormat.format(dateTime) + " Rental Motor.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        pdfDocument.close();
    }

    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}

