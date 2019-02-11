package com.oddlid.karinderya;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class WorkingStoreAdapter extends RecyclerView.Adapter<WorkingStoreAdapter.StoreViewHolder> {
    private ArrayList<String> names;
    private ArrayList<String> ids;
    private ArrayList<String> locations;

    public WorkingStoreAdapter(ArrayList<String> names, ArrayList<String> ids, ArrayList<String> locations)
    {
        this.names = names;
        this.ids = ids;
        this.locations = locations;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.active_layout, viewGroup, false);
        return new StoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder storeViewHolder, int i) {
        storeViewHolder.name.setText(names.get(i));
        storeViewHolder.id.setText(ids.get(i));
        storeViewHolder.location.setText(locations.get(i));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder
    {
        public TextView id;
        public TextView name;
        public TextView location;
        public CardView parentLayout;
        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.active_store_id);
            name = itemView.findViewById(R.id.active_store_name);
            location = itemView.findViewById(R.id.active_store_location);
            parentLayout = itemView.findViewById(R.id.active_store_layout);
        }
    }
}
