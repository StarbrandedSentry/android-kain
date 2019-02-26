package com.oddlid.karinderya;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemAddAdapter extends RecyclerView.Adapter<ItemAddAdapter.ItemHolder> {
    private ArrayList<String> images;
    private ArrayList<String> names;
    private ArrayList<String> prices;
    private ArrayList<String> ids;
    private ArrayList<String> availabilities;
    private OnMenuListener onMenuListener;

    public ItemAddAdapter(ArrayList<String> ids, ArrayList<String> names, ArrayList<String> prices, ArrayList<String> availabilities, ArrayList<String> images, OnMenuListener onMenuListener)
    {
        this.ids = ids;
        this.names = names;
        this.prices = prices;
        this.availabilities = availabilities;
        this.images = images;
        this.onMenuListener = onMenuListener;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_menu_items, viewGroup, false);
        return new ItemHolder(v, onMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        itemHolder.name.setText(names.get(i));
        itemHolder.price.setText(prices.get(i));
        Picasso.get()
                .load(images.get(i))
                .fit().centerCrop()
                .into(itemHolder.image);

        if(availabilities.get(i).equals("available"))
        {
            itemHolder.aSwitch.setChecked(true);
        }
        else if(availabilities.get(i).equals("unavailable"))
        {
            itemHolder.aSwitch.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ItemHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        public TextView name;
        public TextView price;
        public ImageView image;
        public Switch aSwitch;
        OnMenuListener onMenuListener;

        public ItemHolder(@NonNull View itemView, OnMenuListener onMenuListener) {
            super(itemView);
            name = itemView.findViewById(R.id.l_menu_item_name);
            price = itemView.findViewById(R.id.l_menu_item_price);
            aSwitch = itemView.findViewById(R.id.l_menu_item_switch);
            image = itemView.findViewById(R.id.l_menu_item_image);
            this.onMenuListener = onMenuListener;

            itemView.setOnClickListener(this);
            aSwitch.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            onMenuListener.onMenuClick(getAdapterPosition(), ids.get(getAdapterPosition()), availabilities.get(getAdapterPosition()));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            onMenuListener.onSwitchClick(getAdapterPosition(), ids.get(getAdapterPosition()), isChecked);
        }
    }

    public interface OnMenuListener
    {
        void onMenuClick(int position, String id, String availabilty);
        void onSwitchClick(int position, String id, boolean isChecked);

    }
}
