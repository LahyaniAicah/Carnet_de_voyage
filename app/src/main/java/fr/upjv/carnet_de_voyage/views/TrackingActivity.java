package fr.upjv.carnet_de_voyage.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.upjv.carnet_de_voyage.R;
import fr.upjv.carnet_de_voyage.controllers.VoyageController;
import fr.upjv.carnet_de_voyage.models.Position;

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

    private String voyageIdFirebase;


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
        voyageIdFirebase = getIntent().getStringExtra("voyage_id");

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
            stopTracking();
            // ✅ Mise à jour Firebase
            VoyageController controller = new VoyageController();
            controller.stopVoyage(voyageIdFirebase);

            // ➕ Export GPX (facultatif ici)
            // exportToGPX();

            // ⤴️ Retour à l'accueil
            Intent intent = new Intent(this, MainActivity.class);
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
        VoyageController controller = new VoyageController();
        String dateTime = getCurrentDateTime();

        Position position = new Position(
                location.getLatitude(),
                location.getLongitude(),
                dateTime,
                voyageIdFirebase // le string transmis dans l’intent
        );
        controller.addPosition(position);

    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

}
