package com.oddlid.karinderya;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private ArrayList<String> propName;
    private ArrayList<String> propDate;
    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        public TextView storeNameView;
        public TextView dateMadeView;
        public CardView parentLayout;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            storeNameView = itemView.findViewById(R.id.propStoreName);
            dateMadeView = itemView.findViewById(R.id.propDate);
            parentLayout = itemView.findViewById(R.id.pendingLayoutView);
        }
    }

    public RequestAdapter(ArrayList<String> propName, ArrayList<String> propDate)
    {
        this.propName = propName;
        this.propDate = propDate;

    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pending_layout, viewGroup, false);
        RequestViewHolder rvh = new RequestViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder requestViewHolder, int i) {
        requestViewHolder.storeNameView.setText(propName.get(i));
        requestViewHolder.dateMadeView.setText(propDate.get(i));

        requestViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return propDate.size();
    }

    //interface for onclick
    public interface OnNoteListener
    {
        void onNoteListener(int position);
    }
}

