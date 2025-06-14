package fr.upjv.carnet_de_voyage.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

import fr.upjv.carnet_de_voyage.R;
import fr.upjv.carnet_de_voyage.VoyageAdapter;
import fr.upjv.carnet_de_voyage.models.Voyage;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VoyageAdapter adapter;
    private List<Voyage> voyageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNouveauVoyage = findViewById(R.id.btnNewVoyage);
        btnNouveauVoyage.setOnClickListener(view -> {
            Intent intent = new Intent(this, VoyageActivity.class);
            startActivity(intent);
        });

        Button btnVoyagesUtilisateurs = findViewById(R.id.btnVoirVoyagesUtilisateurs);
        btnVoyagesUtilisateurs.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListeUtilisateursActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerVoyages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        voyageList = new ArrayList<>();
        adapter = new VoyageAdapter(this, voyageList);
        recyclerView.setAdapter(adapter);

        chargerVoyagesDepuisFirebase();
    }

    private void chargerVoyagesDepuisFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("voyages")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        voyageList.clear(); // vide l'ancienne liste
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Voyage voyage = document.toObject(Voyage.class);
                            voyageList.add(voyage);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firebase", "Erreur de chargement : ", task.getException());
                    }
                });
    }
}
