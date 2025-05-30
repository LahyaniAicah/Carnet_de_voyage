package fr.upjv.carnet_de_voyage;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VoyageActivity extends AppCompatActivity {

    private String[] intervalles = {
            "30 sec", "1 min", "2 min", "5 min", "10 min", "15 min", "30 min", "1h", "2h"
    };

    private int selectedMs = 30000; // valeur par défaut 30s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voyage);
        RadioGroup radioGroup = findViewById(R.id.radioGroupMode);
        LinearLayout sliderLayout = findViewById(R.id.sliderLayout);

        // Afficher ou masquer le slider selon le mode sélectionné
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
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intervalleLabel.setText(intervalles[progress]);
                selectedMs = convertirIntervalleEnMs(intervalles[progress]);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private String getCurrentDateTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    private long enregistrerVoyage(String titre, String description, String intervalle) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_TITRE, titre);
        values.put(DatabaseHelper.COL_DESC, description);
        values.put(DatabaseHelper.COL_DATED, getCurrentDateTime());
        values.put(DatabaseHelper.COL_DATEF, (String) null);
        values.put(DatabaseHelper.COL_ENRGPS, intervalle);

        long newRowId = db.insert(DatabaseHelper.TABLE_VOYAGE, null, values);
        db.close();

        if (newRowId == -1) {
            Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Voyage enregistré avec succès", Toast.LENGTH_SHORT).show();
        }
        return newRowId;
    }

    public void CreerLancerVoyage(View view) {
        EditText titre = findViewById(R.id.inputTitre);
        EditText description = findViewById(R.id.inputDescription);
        TextView intervalleGPS = findViewById(R.id.intervalleLabel);

        String titreStr = titre.getText().toString().trim();
        String descStr = description.getText().toString().trim();
        String intervalleStr = intervalleGPS.getText().toString().trim();

        if (!titreStr.isEmpty()) {
            long idVoyage = enregistrerVoyage(titreStr, descStr, intervalleStr);
            if (idVoyage != -1) {
                Intent intent = new Intent(this, TrackingActivity.class);
                intent.putExtra("voyage_id", (int) idVoyage);
                intent.putExtra("interval", selectedMs);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, "Veuillez entrer un titre", Toast.LENGTH_SHORT).show();
        }
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