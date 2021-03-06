package com.oddlid.karinderya;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{
    private ArrayList<String> names, namesFull;
    private ArrayList<String> banners, bannersFull;
    private ArrayList<String> locations, locationsFull;
    private ArrayList<String> ratings;
    private OnCardListener onCardListener;

    public HomeAdapter(ArrayList<String> names, ArrayList<String> locations, ArrayList<String> banners, ArrayList<String> ratings, OnCardListener onCardListener)
    {
        this.names = names;
        this.namesFull = new ArrayList<>(names);
        this.locations = locations;
        this.locationsFull = new ArrayList<>(locations);
        this.banners = banners;
        this.ratings = ratings;
        this.bannersFull = new ArrayList<>(banners);
        this.onCardListener = onCardListener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_home_view, viewGroup, false);
        return new HomeViewHolder(v, onCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder homeViewHolder, int i) {
            homeViewHolder.name.setText(names.get(i));
            homeViewHolder.location.setText(locations.get(i));
            homeViewHolder.rating.setText(ratings.get(i));

            Picasso.get()
                    .load(banners.get(i))
                    .fit().centerCrop()
                    .into(homeViewHolder.banner);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class HomeViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView name;
        public TextView location;
        public ImageView banner;
        public TextView rating;
        OnCardListener onCardListener;
        public HomeViewHolder(@NonNull View itemView, OnCardListener onCardListener) {
            super(itemView);
            name = itemView.findViewById(R.id.hv_name_text);
            location = itemView.findViewById(R.id.hv_location_text);
            banner = itemView.findViewById(R.id.hv_banner_view);
            rating = itemView.findViewById(R.id.hv_rating);
            this.onCardListener = onCardListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCardListener.onCardClick(getAdapterPosition());
        }

    }

    public interface OnCardListener
    {
        void onCardClick(int position);
    }


}
