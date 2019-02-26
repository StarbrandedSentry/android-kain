package com.oddlid.karinderya;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AvailMenuAdapter extends RecyclerView.Adapter<AvailMenuAdapter.AvailHolder> {
    private ArrayList<String> images;
    private ArrayList<String> names;
    private ArrayList<String> prices;
    private OnMenuListener onMenuListener;
    private boolean key;
    private String id;
    private String url;

    public AvailMenuAdapter(ArrayList<String> images, ArrayList<String> names, ArrayList<String> prices, OnMenuListener onMenuListener, boolean key, String id, String url)
    {
        this.images = images;
        this.names = names;
        this.onMenuListener = onMenuListener;
        this.key = key;
        this.id = id;
        this.url = url;
        this.prices = prices;
    }

    @NonNull
    @Override
    public AvailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_menu, viewGroup, false);
        return new AvailHolder(v, onMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailHolder availHolder, int i) {
        availHolder.name.setText(names.get(i));
        availHolder.price.setText(prices.get(i));

        Picasso.get()
                .load(images.get(i))
                .fit().centerCrop(Gravity.CENTER)
                .into(availHolder.image);


    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class AvailHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, android.view.MenuItem.OnMenuItemClickListener
    {
        public ImageView image;
        public TextView name;
        public TextView price;
        OnMenuListener onMenuListener;
        public AvailHolder(@NonNull View itemView, OnMenuListener onMenuListener) {
            super(itemView);
            image = itemView.findViewById(R.id.avail_menu_image);
            name = itemView.findViewById(R.id.avail_menu_name);
            price = itemView.findViewById(R.id.avail_menu_price);
            this.onMenuListener = onMenuListener;

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            onMenuListener.onMenuClick(getAdapterPosition(), images.get(getAdapterPosition()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(key)
            {
                menu.setHeaderTitle("Select action");

                MenuItem unavailable = menu.add(Menu.NONE, 1, 1, "Set unavailable");
                android.view.MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete item");

                unavailable.setOnMenuItemClickListener(this);
                delete.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(android.view.MenuItem item) {
            switch (item.getItemId())
            {
                case 1:
                    onMenuListener.onUnavailableClick(getAdapterPosition(), id);
                    return true;
                case 2:
                    onMenuListener.onDeleteClick(getAdapterPosition(), id);
                    return true;
            }
            return false;
        }
    }

    public interface OnMenuListener
    {
        void onMenuClick(int position, String url);

        void onUnavailableClick(int position, String id);
        void onDeleteClick(int position, String id);
    }


    public void setOnMenuListener(OnMenuListener listener)
    {
        onMenuListener = listener;
    }
}
