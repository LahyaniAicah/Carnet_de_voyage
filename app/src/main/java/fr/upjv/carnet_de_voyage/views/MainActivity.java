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

        //afficher une liste d’éléments
        recyclerView = findViewById(R.id.recyclerVoyages);

        // Afficher les éléments en colonne verticale (par défaut, comme une liste classique).
        //LinearLayoutManager(this) gère ça.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        voyageList = new ArrayList<>();
        // crées un adapter personnalisé pour afficher les éléments
        adapter = new VoyageAdapter(this, voyageList);
        recyclerView.setAdapter(adapter);

        chargerVoyagesDepuisFirebase();
    }

    private void chargerVoyagesDepuisFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("voyages")
                .get()
                .addOnCompleteListener(task -> {//un écouteur qui sera appelé quand la requête est terminée
                    if (task.isSuccessful()) {
                        voyageList.clear(); // vide l'ancienne liste
                        for (QueryDocumentSnapshot document : task.getResult()) { //Parcourir chaque document Firestore (chaque voyage)
                            Voyage voyage = document.toObject(Voyage.class);//transforme automatiquement le document en objet Java Voyage
                            voyageList.add(voyage);
                        }
                        adapter.notifyDataSetChanged();//La liste a changé ! Mets à jour l’écran.
                    } else {
                        Log.e("Firebase", "Erreur de chargement : ", task.getException());
                    }
                });
    }
}
