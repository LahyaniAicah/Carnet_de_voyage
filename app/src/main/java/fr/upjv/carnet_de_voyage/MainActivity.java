package fr.upjv.carnet_de_voyage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNouveauVoyage = findViewById(R.id.btnNewVoyage);
        btnNouveauVoyage.setOnClickListener(view -> {
            Intent intent = new Intent(this, VoyageActivity.class);
            startActivity(intent);
        });

        Button btnPremierVoyage = findViewById(R.id.btnFirstDrip);
        btnPremierVoyage.setOnClickListener(view -> {
            Intent intent = new Intent(this, VoyageActivity.class);
            startActivity(intent);
        });
    }
}
