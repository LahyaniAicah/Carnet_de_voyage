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

//💡 Le ViewHolder représente chaque ligne dans la liste.
public class VoyageAdapter extends RecyclerView.Adapter<VoyageViewHolder> {
    private final List<Voyage> voyageList;
    private Context context;//utile pour lancer des Intent ou accéder aux ressources
    public VoyageAdapter(Context context, List<Voyage> voyageList) {
        this.context = context;
        this.voyageList = voyageList;
    }

    @NonNull
    @Override
    //Cette méthode est appelée pour créer une nouvelle “ligne” (ViewHolder) à l’écran
    public VoyageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voyage, parent, false);
        // inflate sert à charger le layout XML item_voyage.xml (design d'une ligne)
        return new VoyageViewHolder(itemView);
        //on retourne un VoyageViewHolder contenant cette vue
    }

    // Cette méthod est appelée pour remplir chaque ligne avec les données du voyage
    @Override
    public void onBindViewHolder(@NonNull VoyageViewHolder holder, int position) {
        Voyage voyage = voyageList.get(position);//récupère le Voyage à la ligne demandée
        holder.mettreAJourLigne(voyage);// remplit les champs (titre, dates, etc.)

        //Quand l’utilisateur clique sur une ligne:
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VoyageDetailActivity.class);
            intent.putExtra("voyage_id", voyage.getId());
            context.startActivity(intent);
        });
    }

    //retourne le nombre d’éléments à afficher
    @Override
    public int getItemCount() {
        return voyageList.size();
    }
}
