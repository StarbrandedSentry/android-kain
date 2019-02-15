package com.oddlid.karinderya;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AvailMenuAdapter extends RecyclerView.Adapter<AvailMenuAdapter.AvailHolder> {
    private ArrayList<String> images;
    private ArrayList<String> names;
    private OnMenuListener onMenuListener;

    public AvailMenuAdapter(ArrayList<String> images, ArrayList<String> names, OnMenuListener onMenuListener)
    {
        this.images = images;
        this.names = names;
        this.onMenuListener = onMenuListener;
    }

    @NonNull
    @Override
    public AvailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.avail_menu_layout, viewGroup, false);
        return new AvailHolder(v, onMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailHolder availHolder, int i) {
        availHolder.name.setText(names.get(i));

        Picasso.get()
                .load(images.get(i))
                .fit().centerCrop(Gravity.CENTER)
                .into(availHolder.image);

        
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class AvailHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public ImageView image;
        public TextView name;
        OnMenuListener onMenuListener;
        public AvailHolder(@NonNull View itemView, OnMenuListener onMenuListener) {
            super(itemView);
            image = itemView.findViewById(R.id.avail_menu_image);
            name = itemView.findViewById(R.id.avail_menu_name);
            this.onMenuListener = onMenuListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMenuListener.onMenuClick(getAdapterPosition());
        }
    }

    public interface OnMenuListener
    {
        void onMenuClick(int position);
    }


}
