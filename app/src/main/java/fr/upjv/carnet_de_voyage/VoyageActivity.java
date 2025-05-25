package fr.upjv.carnet_de_voyage;

import static java.sql.Types.NULL;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
        intervalleLabel.setText(intervalles[0]); //

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

    private String getCurrentDateTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    private void enregistrerVoyage(String titre, String description, String intervalle) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_TITRE, titre);
        values.put(DatabaseHelper.COL_DESC, description);
        values.put(DatabaseHelper.COL_DATED, getCurrentDateTime());
        values.put(DatabaseHelper.COL_DATEF, NULL);
        values.put(DatabaseHelper.COL_ENRGPS, intervalle);

        long newRowId = db.insert(DatabaseHelper.TABLE_VOYAGE, null, values);
        db.close();

        if (newRowId != -1) {
            Toast.makeText(this, "Voyage enregistré avec succès", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
        }
    }

    public void CreerLancerVoyage(View view) {
        EditText titre = findViewById(R.id.inputTitre);
        EditText description = findViewById(R.id.inputDescription);
        TextView intervalleGPS = findViewById(R.id.intervalleLabel);

        String titreStr = titre.getText().toString().trim();
        String descStr = description.getText().toString().trim();
        String intervalleStr = intervalleGPS.getText().toString().trim();

        if (!titreStr.isEmpty()) {
            enregistrerVoyage(titreStr, descStr, intervalleStr);
            finish(); // revenir à MainActivity
        } else {
            Toast.makeText(this, "Veuillez entrer un titre", Toast.LENGTH_SHORT).show();
        }
    }
}

