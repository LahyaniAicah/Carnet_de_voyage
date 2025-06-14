package fr.upjv.carnet_de_voyage.views;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.upjv.carnet_de_voyage.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String voyageId;
    private GoogleMap map;
    private List<LatLng> positions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        voyageId = getIntent().getStringExtra("voyage_id");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        chargerPositionsEtAfficher();
    }

    private void chargerPositionsEtAfficher() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("positions")
                .whereEqualTo("voyageId", voyageId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Double lat = doc.getDouble("latitude");
                        Double lon = doc.getDouble("longitude");
                        if (lat != null && lon != null) {
                            LatLng point = new LatLng(lat, lon);
                            positions.add(point);
                            map.addMarker(new MarkerOptions().position(point).title("Point GPS"));
                        }
                    }

                    if (!positions.isEmpty()) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positions.get(0), 15f));
                        map.addPolyline(new PolylineOptions()
                                .addAll(positions)
                                .color(0xFF4CAF50)
                                .width(5));
                    }
                })
                .addOnFailureListener(e -> Log.e("MapActivity", "Erreur chargement positions", e));
    }
}
