package fr.upjv.carnet_de_voyage.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import fr.upjv.carnet_de_voyage.models.Position;
import fr.upjv.carnet_de_voyage.models.Voyage;

public class VoyageController {

    private final FirebaseFirestore db;

    public VoyageController() {
        db = FirebaseFirestore.getInstance();
    }

    public void addVoyage(Voyage voyage, Context context, java.util.function.Consumer<String> onIdGenerated) {
        db.collection("voyages")
                .add(voyage)
                .addOnSuccessListener(documentReference -> {
                    String docId = documentReference.getId();
                    voyage.setId(docId);
                    documentReference.update("id", docId);

                    Log.d("Firebase", "Voyage ajouté avec ID : " + docId);
                    onIdGenerated.accept(docId); // 🔁 retourne l’ID au caller
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erreur ajout voyage", e);
                    Toast.makeText(context, "Erreur ajout Firebase", Toast.LENGTH_SHORT).show();
                });
    }

    public void stopVoyage(String voyageId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String dateFin = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());

        db.collection("voyages")
                .document(voyageId)
                .update("dateFin", dateFin)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Voyage terminé."))
                .addOnFailureListener(e -> Log.e("Firebase", "Erreur de mise à jour", e));
    }


    public void addPosition(Position position) {
        db.collection("positions")
                .add(position)
                .addOnSuccessListener(docRef -> Log.d("Firebase", "Position enregistrée"))
                .addOnFailureListener(e -> Log.e("Firebase", "Erreur ajout position", e));
    }


    public void testerConnexionFirebase(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> testData = new HashMap<>();
        testData.put("test", true);
        testData.put("timestamp", System.currentTimeMillis());

        db.collection("test_connexion")
                .add(testData)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(context, "✅ Connexion Firebase OK", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "❌ Connexion Firebase échouée", Toast.LENGTH_LONG).show()
                );
    }

}
