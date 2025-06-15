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
                sliderLayout.setVisibility(View.GONE);//Si l’utilisateur coche Manuel → cacher le slider
                selectedMs = -1;
            } else {
                sliderLayout.setVisibility(View.VISIBLE);
            }
        });

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> finish());

        //Configuration du SeekBar (curseur d’intervalle)
        SeekBar seekBar = findViewById(R.id.seekIntervalle);
        TextView intervalleLabel = findViewById(R.id.intervalleLabel);
        intervalleLabel.setText(intervalles[0]);//Affiche le premier intervalle ("30 sec") par défaut.

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //À chaque déplacement du curseur :
                intervalleLabel.setText(intervalles[progress]);//met à jour le texte (1 min, 2h, etc.)
                selectedMs = convertirIntervalleEnMs(intervalles[progress]);//met à jour la valeur selectedMs en millisecondes
            }

            // obligatoires lorsque tu implémentes un OnSeekBarChangeListener
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void CreerLancerVoyage(View view) {
        EditText titreInput = findViewById(R.id.inputTitre);
        EditText descriptionInput = findViewById(R.id.inputDescription);
        String titre = titreInput.getText().toString().trim();
        String desc = descriptionInput.getText().toString().trim();
        String dateDebut = getCurrentDateTime(); // ou une date choisie
        String dateFin = null; // voyage en cours

        if (!titre.isEmpty()) {
            Voyage voyage = new Voyage(null, titre, dateDebut, dateFin , desc);
            controller.addVoyage(voyage, email, this, voyageId -> { // Firebase retourne l'ID
                Intent intent = new Intent(this, TrackingActivity.class);
                intent.putExtra("voyage_id", voyageId); // important pour stopVoyage()
                intent.putExtra("interval", selectedMs);
                startActivity(intent);
                finish();
            });

            controller.testerConnexionFirebase(this); // test instantané de connexion Firebase
            Toast.makeText(this, "Voyage enregistré sur Firebase", Toast.LENGTH_SHORT).show();


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
