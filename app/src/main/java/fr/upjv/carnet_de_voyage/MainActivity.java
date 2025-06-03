package fr.upjv.carnet_de_voyage;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNouveauVoyage = findViewById(R.id.btnNewVoyage);
        btnNouveauVoyage.setOnClickListener(view -> {
            Intent intent = new Intent(this, VoyageActivity.class);
            startActivity(intent);
        });


        RecyclerView recyclerView = findViewById(R.id.recyclerVoyages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Voyage> voyages = new ArrayList<>();

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_VOYAGE + " WHERE date_fin IS NOT NULL", null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                String titre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITRE));
                String debut = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATED));
                String fin = cursor.getString(cursor.getColumnIndexOrThrow("date_fin"));
                voyages.add(new Voyage(id, titre, debut, fin));
            } while (cursor.moveToNext());
        }
        cursor.close();

        VoyageAdapter adapter = new VoyageAdapter(this,voyages);
        recyclerView.setAdapter(adapter);
    }


}
