package fr.upjv.carnet_de_voyage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.upjv.carnet_de_voyage.models.Voyage;
import fr.upjv.carnet_de_voyage.views.VoyageDetailActivity;

public class VoyageAdapter extends RecyclerView.Adapter<VoyageViewHolder> {
    private final List<Voyage> voyageList;
    private Context context;
    public VoyageAdapter(Context context, List<Voyage> voyageList) {
        this.context = context;
        this.voyageList = voyageList;
    }

    @NonNull
    @Override
    public VoyageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voyage, parent, false);
        return new VoyageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VoyageViewHolder holder, int position) {
        Voyage voyage = voyageList.get(position);
        holder.mettreAJourLigne(voyage);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VoyageDetailActivity.class);
            intent.putExtra("voyage_id", voyage.getId()); // Assure-toi que Voyage a un getId()
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return voyageList.size();
    }
}
