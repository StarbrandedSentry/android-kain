package com.oddlid.karinderya;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EntryImageAdapter extends RecyclerView.Adapter<EntryImageAdapter.EntryViewHolder>{
    //private ArrayList<EntryUpload> mUploads;
    private ArrayList<String> mUploads;

    public EntryImageAdapter(ArrayList<String> uploads)
    {
        this.mUploads = uploads;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.entryimage_layout, viewGroup, false);
        EntryViewHolder eih = new EntryViewHolder(v);
        return eih;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder entryViewHolder, int i) {
        Picasso.get()
                .load(mUploads.get(i))
                .placeholder(R.mipmap.ic_launcher)
                .into(entryViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class EntryViewHolder extends  RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.entryImageView);
        }
    }

}
