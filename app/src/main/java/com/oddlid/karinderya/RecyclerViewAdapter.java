package com.oddlid.karinderya;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mStoreName = new ArrayList<>();
    private Context mContext;
    private ArrayList<String> mDateMade = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView store_name;
        TextView date_made;
        RecyclerView recyclerLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            store_name = itemView.findViewById(R.id.storeNameText);
            date_made = itemView.findViewById(R.id.dateMadeText);
            recyclerLayout = itemView.findViewById(R.id.recyclerLayout);
        }
    }

    public RecyclerViewAdapter(ArrayList<String> mStoreName, ArrayList<String> mDateMade) {
        this.mStoreName = mStoreName;
        this.mDateMade = mDateMade;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_pending, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.store_name.setText(mStoreName.get(i));
        viewHolder.date_made.setText(mDateMade.get(i));

        viewHolder.recyclerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mStoreName.size();
    }

}
