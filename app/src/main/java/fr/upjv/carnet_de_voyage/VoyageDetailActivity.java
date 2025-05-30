package fr.upjv.carnet_de_voyage;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileWriter;

public class VoyageDetailActivity extends AppCompatActivity {

    private int voyageId;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voyage_details);

        TextView titre = findViewById(R.id.titreVoyageDetail);
        TextView date = findViewById(R.id.dateVoyageDetail);
        TextView desc = findViewById(R.id.descVoyageDetail);
        Button btnExport = findViewById(R.id.btnExporterGPX);

        voyageId = getIntent().getIntExtra("voyage_id", -1);
        dbHelper = new DatabaseHelper(this);

        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_VOYAGE + " WHERE " + DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(voyageId)}
        );

        if (c.moveToFirst()) {
            titre.setText("Titre : " + c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TITRE)));
            desc.setText("Description : " + c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_DESC)));
            date.setText("Dates : du " + c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_DATED)) +
                    " au " + (c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_DATEF)) != null ?
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_DATEF)) : "--"));
        }
        c.close();
        TextView btnBack = findViewById(R.id.btnBackDetail);
        btnBack.setOnClickListener(view -> finish());
        btnExport.setOnClickListener(v -> exporterGpxEtEnvoyer());
    }

    private void exporterGpxEtEnvoyer() {
        try {
            File gpxFile = new File(getExternalFilesDir(null), "voyage_" + voyageId + ".gpx");
            FileWriter writer = new FileWriter(gpxFile);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<gpx version=\"1.1\" creator=\"CarnetDeVoyage\">\n");

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
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fichier GPX - Voyage");
            emailIntent.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(this, getPackageName() + ".provider", gpxFile));
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(emailIntent, "Envoyer GPX..."));

        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'export", Toast.LENGTH_SHORT).show();
        }
    }
}

