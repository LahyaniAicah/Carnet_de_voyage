package fr.upjv.carnet_de_voyage;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VoyageViewHolder extends RecyclerView.ViewHolder {
    private final TextView titre, dates;

    public VoyageViewHolder(@NonNull View itemView) {
        super(itemView);
        this.titre = itemView.findViewById(R.id.titreVoyage);
        this.dates = itemView.findViewById(R.id.dateVoyage);

    }

    public void mettreAJourLigne(Voyage voyage) {
        titre.setText("Description: " + voyage.getTitre());
        dates.setText("🕓 Du " + voyage.getDateDebut() + " au " + voyage.getDateFin());

    }
}
