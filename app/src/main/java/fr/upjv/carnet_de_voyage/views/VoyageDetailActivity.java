package fr.upjv.carnet_de_voyage.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileWriter;

import fr.upjv.carnet_de_voyage.R;

public class VoyageDetailActivity extends AppCompatActivity {

    private String voyageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voyage_details);

        TextView titre = findViewById(R.id.titreVoyageDetail);
        TextView date = findViewById(R.id.dateVoyageDetail);
        TextView desc = findViewById(R.id.descVoyageDetail);
        Button btnExport = findViewById(R.id.btnExporterGPX);
        TextView btnBack = findViewById(R.id.btnBackDetail);

        Button btnCarte = findViewById(R.id.btnAfficherCarte);  // ce bouton doit exister dans ton layout XML
        btnCarte.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("voyage_id", voyageId); // envoie l'ID du voyage Firebase
            startActivity(intent);
        });

        voyageId = getIntent().getStringExtra("voyage_id");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("voyages").document(voyageId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String titreStr = documentSnapshot.getString("titre");
                        String descStr = documentSnapshot.getString("description");
                        String dated = documentSnapshot.getString("dateDebut");
                        String datef = documentSnapshot.getString("dateFin");

                        titre.setText("Titre : " + titreStr);
                        desc.setText("Description : " + descStr);
                        date.setText("Dates : du " + dated + " au " + (datef != null ? datef : "--"));
                    } else {
                        Toast.makeText(this, "Voyage non trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur de lecture Firebase", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });

        btnExport.setOnClickListener(v -> {
            Log.d("GPX", "✅ Bouton export cliqué");
            Toast.makeText(this, "Export en cours...", Toast.LENGTH_SHORT).show();
            exporterGpxEtEnvoyer();
        });

        btnBack.setOnClickListener(view -> finish());
    }

    private void exporterGpxEtEnvoyer() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("positions")
                .whereEqualTo("voyageId", voyageId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    try {
                        File gpxFile = new File(getExternalFilesDir(null), "voyage_" + voyageId + ".gpx");
                        FileWriter writer = new FileWriter(gpxFile);

                        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                        writer.write("<gpx version=\"1.1\" creator=\"CarnetDeVoyage\">\n");

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            double lat = doc.getDouble("latitude");
                            double lon = doc.getDouble("longitude");
                            String time = doc.getString("datetime");

                            writer.write("<trkpt lat=\"" + lat + "\" lon=\"" + lon + "\"><time>" + time + "</time></trkpt>\n");
                        }

                        writer.write("</gpx>");
                        writer.close();

                        Log.d("GPX", "✅ Fichier GPX généré : " + gpxFile.getAbsolutePath());

                        Uri fileUri = FileProvider.getUriForFile(
                                this,
                                getPackageName() + ".provider",
                                gpxFile
                        );

                        Log.d("GPX", "✅ URI du fichier : " + fileUri.toString());

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("application/gpx+xml");
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fichier GPX - Voyage");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        if (emailIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(Intent.createChooser(emailIntent, "Envoyer GPX..."));
                        } else {
                            Log.e("GPX", "❌ Aucune application disponible pour l'envoi");
                            Toast.makeText(this, "Aucune application disponible pour envoyer le fichier.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("GPX", "❌ Erreur lors de l'écriture du fichier GPX", e);
                        Toast.makeText(this, "Erreur GPX : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GPX", "❌ Erreur lors de la récupération des positions Firebase", e);
                    Toast.makeText(this, "Impossible de récupérer les positions", Toast.LENGTH_SHORT).show();
                });
    }
}
