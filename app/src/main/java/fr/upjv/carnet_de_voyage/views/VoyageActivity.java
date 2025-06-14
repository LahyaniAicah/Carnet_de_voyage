package fr.upjv.carnet_de_voyage.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import fr.upjv.carnet_de_voyage.R;
import fr.upjv.carnet_de_voyage.controllers.VoyageController;
import fr.upjv.carnet_de_voyage.models.Voyage;

public class VoyageActivity extends AppCompatActivity {

    private String[] intervalles = {
            "30 sec", "1 min", "2 min", "5 min", "10 min", "15 min", "30 min", "1h", "2h"
    };
    private int selectedMs = 30000;
    private VoyageController controller;

    String email = "Heva@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voyage);

        controller = new VoyageController(); // instancier le contrôleur

        RadioGroup radioGroup = findViewById(R.id.radioGroupMode);
        LinearLayout sliderLayout = findViewById(R.id.sliderLayout);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioManuel) {
                sliderLayout.setVisibility(View.GONE);
                selectedMs = -1;
            } else {
                sliderLayout.setVisibility(View.VISIBLE);
            }
        });

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> finish());

        SeekBar seekBar = findViewById(R.id.seekIntervalle);
        TextView intervalleLabel = findViewById(R.id.intervalleLabel);
        intervalleLabel.setText(intervalles[0]);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intervalleLabel.setText(intervalles[progress]);
                selectedMs = convertirIntervalleEnMs(intervalles[progress]);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void CreerLancerVoyage(View view) {
        EditText titreInput = findViewById(R.id.inputTitre);
        EditText descriptionInput = findViewById(R.id.inputDescription);
        TextView intervalleGPS = findViewById(R.id.intervalleLabel);

        String titre = titreInput.getText().toString().trim();
        String desc = descriptionInput.getText().toString().trim();
        String dateDebut = getCurrentDateTime(); // ou une date choisie
        String dateFin = null; // voyage en cours
        String intervalleStr = intervalleGPS.getText().toString();

        if (!titre.isEmpty()) {
            Voyage voyage = new Voyage(null, titre, dateDebut, dateFin);
            controller.addVoyage(voyage, email, this, voyageId -> {
                // ✅ Ici tu reçois le vrai ID généré par Firebase

                Intent intent = new Intent(this, TrackingActivity.class);
                intent.putExtra("voyage_id", voyageId); // important pour stopVoyage()
                intent.putExtra("interval", selectedMs);
                startActivity(intent);
                finish();
            });

            controller.testerConnexionFirebase(this); // test instantané de connexion Firebase

            Toast.makeText(this, "Voyage enregistré sur Firebase", Toast.LENGTH_SHORT).show();

            // On lance l'activité de suivi
            Intent intent = new Intent(this, TrackingActivity.class);
            intent.putExtra("interval", selectedMs);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Veuillez entrer un titre", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDateTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    private int convertirIntervalleEnMs(String label) {
        switch (label) {
            case "30 sec": return 30 * 1000;
            case "1 min": return 60 * 1000;
            case "2 min": return 2 * 60 * 1000;
            case "5 min": return 5 * 60 * 1000;
            case "10 min": return 10 * 60 * 1000;
            case "15 min": return 15 * 60 * 1000;
            case "30 min": return 30 * 60 * 1000;
            case "1h": return 60 * 60 * 1000;
            case "2h": return 2 * 60 * 60 * 1000;
            default: return 60 * 1000;
        }
    }
}
