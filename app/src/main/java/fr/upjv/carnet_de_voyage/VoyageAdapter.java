package fr.upjv.carnet_de_voyage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VoyageAdapter extends RecyclerView.Adapter<VoyageAdapter.VoyageViewHolder> {
    private List<Voyage> voyageList;

    public VoyageAdapter(List<Voyage> voyages) {
        this.voyageList = voyages;
    }

    @NonNull
    @Override
    public VoyageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voyage, parent, false);
        return new VoyageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoyageViewHolder holder, int position) {
        Voyage voyage = voyageList.get(position);
        holder.titre.setText("📍 " + voyage.getTitre());
        holder.dates.setText("🕓 Du " + voyage.getDateDebut() + " au " + voyage.getDateFin());
        holder.intervalle.setText("📌 Intervalle : " + voyage.getIntervalle());
    }

    @Override
    public int getItemCount() {
        return voyageList.size();
    }

    public static class VoyageViewHolder extends RecyclerView.ViewHolder {
        TextView titre, dates, intervalle;

        public VoyageViewHolder(View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.titreVoyage);
            dates = itemView.findViewById(R.id.dateVoyage);
            intervalle = itemView.findViewById(R.id.intervalleGPS);
        }
    }
}

