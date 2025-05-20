package fr.upjv.carnet_de_voyage;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class VoyageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voyage);

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            finish(); // Ferme l'activité et retourne à MainActivity
        });
        SeekBar seekBar = findViewById(R.id.seekIntervalle);
        TextView intervalleLabel = findViewById(R.id.intervalleLabel);

        // Liste des valeurs lisibles
        String[] intervalles = {
                "30 sec", "1 min", "2 min", "5 min", "10 min", "15 min", "30 min", "1h", "2h"
        };

        // Initialiser texte affiché
        intervalleLabel.setText(intervalles[0]); // Par défaut : "1 min"

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    intervalleLabel.setText(intervalles[progress]);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

}
