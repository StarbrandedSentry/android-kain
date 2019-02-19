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
    private ArrayList<String> propID;
    private OnNoteListener mOnNoteListener;

    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView requestIDView;
        public TextView storeNameView;
        public TextView dateMadeView;
        public CardView parentLayout;
        OnNoteListener onNoteListener;
        public RequestViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            requestIDView = itemView.findViewById(R.id.propID);
            storeNameView = itemView.findViewById(R.id.propStoreName);
            dateMadeView = itemView.findViewById(R.id.propDate);
            parentLayout = itemView.findViewById(R.id.pendingLayoutView);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public RequestAdapter(ArrayList<String> propID, ArrayList<String> propName, ArrayList<String> propDate, OnNoteListener onNoteListener)
    {
        this.propName = propName;
        this.propDate = propDate;
        this.propID = propID;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_pending, viewGroup, false);
        RequestViewHolder rvh = new RequestViewHolder(v, mOnNoteListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder requestViewHolder, int i) {
        requestViewHolder.requestIDView.setText(propID.get(i));
        requestViewHolder.storeNameView.setText(propName.get(i));
        requestViewHolder.dateMadeView.setText(propDate.get(i));

    }

    @Override
    public int getItemCount() {
        return propDate.size();
    }

    //interface for onclick
    public interface OnNoteListener
    {
        void onNoteClick(int position);
    }
}

