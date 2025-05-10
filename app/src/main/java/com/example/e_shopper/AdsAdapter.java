package com.example.e_shopper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.AdViewHolder> {
    private ArrayList<AdModel> adsList;

    public AdsAdapter(ArrayList<AdModel> adsList) {
        this.adsList = adsList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        AdModel ad = adsList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(ad.getImageUrl())
                .into(holder.adImage);
    }

    @Override
    public int getItemCount() {
        return adsList.size();
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        public ImageView adImage;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            adImage = itemView.findViewById(R.id.adImage);

        }
    }
}

