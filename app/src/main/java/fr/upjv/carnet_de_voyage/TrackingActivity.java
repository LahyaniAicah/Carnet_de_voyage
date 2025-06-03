package fr.upjv.carnet_de_voyage;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrackingActivity extends AppCompatActivity {

    private FusedLocationProviderClient locationClient;
    private Handler handler;
    private Runnable periodicTask;
    private Runnable chronoTask;
    private int interval;
    private int voyageId;
    private long chronoStart;

    private TextView txtPosition;
    private TextView txtIntervalle;
    private TextView chronoText;
    private Button btnCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_activity);

        voyageId = getIntent().getIntExtra("voyage_id", -1);
        interval = getIntent().getIntExtra("interval", 60000);

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        txtPosition = findViewById(R.id.txtPosition);
        txtIntervalle = findViewById(R.id.txtIntervalle);
        chronoText = findViewById(R.id.chronoText);
        btnCapture = findViewById(R.id.btnCaptureNow);

        txtIntervalle.setText("Intervalle GPS : " + (interval > 0 ? interval / 1000 + "s" : "manuel"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        handler = new Handler(Looper.getMainLooper());

        Button btnTerminer = findViewById(R.id.btnTerminer);


        if (interval > 0) {
            btnCapture.setVisibility(Button.GONE);
            startPeriodicTracking();
        } else {
            btnCapture.setOnClickListener(v -> capturePosition());
        }

        chronoStart = System.currentTimeMillis();
        startChrono();

        btnTerminer.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            stopTracking();
            setDateFinVoyage();
            startActivity(intent);
            finish();
        });


    }

    private void startPeriodicTracking() {
        periodicTask = new Runnable() {
            @Override
            public void run() {
                capturePosition();
                handler.postDelayed(this, interval);
            }
        };
        handler.post(periodicTask);
    }

    private void stopTracking() {
        if (handler != null) {
            if (periodicTask != null) handler.removeCallbacks(periodicTask);
            if (chronoTask != null) handler.removeCallbacks(chronoTask);
        }
    }

    private void startChrono() {
        chronoTask = new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - chronoStart;
                int seconds = (int) (elapsed / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                chronoText.setText(String.format(Locale.getDefault(), "Durée : %02d:%02d", minutes, seconds));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(chronoTask);
    }

    private void capturePosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                saveToDatabase(location);
                txtPosition.setText("Latitude: " + location.getLatitude()
                        + "\nLongitude: " + location.getLongitude()
                        + "\nHeure: " + getCurrentTime());
            }
        });
    }

    private void saveToDatabase(Location location) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_LAT, location.getLatitude());
        values.put(DatabaseHelper.COL_LON, location.getLongitude());
        values.put(DatabaseHelper.COL_DATE, getCurrentDateTime());
        values.put(DatabaseHelper.COL_VID, voyageId);
        dbHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_POSITION, null, values);
        dbHelper.close();
    }

    private void setDateFinVoyage() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_DATEF, getCurrentDateTime());
        dbHelper.getWritableDatabase().update(DatabaseHelper.TABLE_VOYAGE, values, DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(voyageId)});
        dbHelper.close();
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private void exportToGPX() {
        try {
            File gpxFile = new File(getExternalFilesDir(null), "voyage_" + voyageId + ".gpx");
            FileWriter writer = new FileWriter(gpxFile);

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<gpx version=\"1.1\" creator=\"CarnetDeVoyage\">\n");

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT * FROM " + DatabaseHelper.TABLE_POSITION + " WHERE " + DatabaseHelper.COL_VID + "=?",
                    new String[]{String.valueOf(voyageId)}
            );

            while (cursor.moveToNext()) {
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LAT));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LON));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));
                writer.write("<trkpt lat=\"" + lat + "\" lon=\"" + lon + "\"><time>" + time + "</time></trkpt>\n");
            }
            cursor.close();
            writer.write("</gpx>");
            writer.close();

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("application/gpx+xml");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fichier GPX Voyage");
            emailIntent.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(this, getPackageName() + ".provider", gpxFile));
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(emailIntent, "Envoyer fichier GPX..."));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur export GPX", Toast.LENGTH_SHORT).show();
        }
    }
}
