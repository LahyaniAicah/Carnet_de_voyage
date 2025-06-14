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
import java.util.HashSet;
import java.util.Set;

import fr.upjv.carnet_de_voyage.R;

public class ListeUtilisateursActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView userListView;
    private ArrayList<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_utilisateurs);

        userListView = findViewById(R.id.userListView);
        userList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        TextView btnBack = findViewById(R.id.btnBackUserList);
        btnBack.setOnClickListener(v -> finish());

        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        Set<String> usersSet = new HashSet<>();

        db.collection("voyages")
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String user = doc.getString("user");
                        if (user != null) usersSet.add(user);
                    }

                    userList.clear();
                    userList.addAll(usersSet);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
                    userListView.setAdapter(adapter);

                    userListView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
                        String selectedUser = userList.get(position);
                        Intent intent = new Intent(this, VoyagesUtilisateurActivity.class);
                        intent.putExtra("user", selectedUser);
                        startActivity(intent);
                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur de récupération utilisateurs", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
