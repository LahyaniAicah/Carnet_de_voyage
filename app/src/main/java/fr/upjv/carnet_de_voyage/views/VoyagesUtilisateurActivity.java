package fr.upjv.carnet_de_voyage.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import fr.upjv.carnet_de_voyage.R;
import fr.upjv.carnet_de_voyage.models.Voyage;

public class VoyagesUtilisateurActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView voyageListView;
    private ArrayList<Voyage> voyages = new ArrayList<>();
    private ArrayList<String> voyageTitles = new ArrayList<>();
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voyages_utilisateur);

        voyageListView = findViewById(R.id.voyageListView);
        db = FirebaseFirestore.getInstance();

        user = getIntent().getStringExtra("user");
        setTitle("Voyages de " + user); // Titre dynamique

        TextView btnBack = findViewById(R.id.btnBackUserVoyages);
        btnBack.setOnClickListener(v -> finish());

        chargerVoyagesUtilisateur();
    }

    private void chargerVoyagesUtilisateur() {
        db.collection("voyages")
                .whereEqualTo("user", user)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    voyages.clear();
                    voyageTitles.clear();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Voyage v = doc.toObject(Voyage.class);
                        if (v != null) {
                            voyages.add(v);
                            voyageTitles.add(v.getTitre() + " (" + v.getDateDebut() + ")");
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, voyageTitles);
                    voyageListView.setAdapter(adapter);

                    voyageListView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
                        Voyage selectedVoyage = voyages.get(position);
                        Intent intent = new Intent(this, MapActivity.class);
                        intent.putExtra("voyage_id", selectedVoyage.getId());
                        startActivity(intent);
                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur chargement voyages", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
