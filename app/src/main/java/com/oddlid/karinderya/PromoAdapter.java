package com.oddlid.karinderya;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.PromoHolder> {
    ArrayList<String> promo_ids, store_ids, names, descriptions, types, times, time_frames;
    OnMenuListener onMenuListener;
    String id;
    boolean key;

    public PromoAdapter(ArrayList<String> promo_ids, ArrayList<String> store_ids, ArrayList<String> names, ArrayList<String> descriptions, ArrayList<String> types,
        ArrayList<String> times, ArrayList<String> time_frames, OnMenuListener onMenuListener, String id, boolean key)
    {
        this.store_ids = store_ids;
        this.names = names;
        this.descriptions = descriptions;
        this.types = types;
        this.times = times;
        this. time_frames = time_frames;
        this.onMenuListener = onMenuListener;
        this.id = id;
        this.key = key;
        this.promo_ids = promo_ids;
    }

    @NonNull
    @Override
    public PromoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_promo_view, viewGroup, false);
        return new PromoHolder(v, onMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoHolder promoHolder, int i) {
        promoHolder.name.setText(names.get(i));
        promoHolder.description.setText(descriptions.get(i));
        promoHolder.type.setText(types.get(i));
        promoHolder.time_type.setText(times.get(i));
        promoHolder.time_frame.setText(time_frames.get(i));

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class PromoHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView name, description, type, time_type, time_frame;
        public PromoHolder(@NonNull View itemView, OnMenuListener onMenuListener) {
            super(itemView);
            name = itemView.findViewById(R.id.l_promo_name);
            description = itemView.findViewById(R.id.l_promo_description);
            type = itemView.findViewById(R.id.l_promo_type);
            time_type = itemView.findViewById(R.id.l_promo_time);
            time_frame = itemView.findViewById(R.id.l_promo_time_frame);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {
                case 1:
                    onMenuListener.onRemove(getAdapterPosition(), promo_ids);
                    break;
            }

            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(key)
            {
                menu.setHeaderTitle("Select action");

                MenuItem remove = menu.add(Menu.NONE, 1, 1, "Remove promo");

                remove.setOnMenuItemClickListener(this);
            }
        }
    }

    public interface OnMenuListener
    {
        void onMenuClick(int position, String id);

        void onRemove(int position, ArrayList<String> promo_ids);
    }
}
