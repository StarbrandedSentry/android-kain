package com.oddlid.karinderya.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oddlid.karinderya.AddItemActivity;
import com.oddlid.karinderya.AvailMenuAdapter;
import com.oddlid.karinderya.ItemAddActivity;
import com.oddlid.karinderya.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreMenuFragment extends Fragment implements AvailMenuAdapter.OnMenuListener{
    Bundle oldBundle;
    Button addItem, add;
    ImageView zoomed;
    ImageButton closeDialog;
    Dialog imageZoom;
    private RecyclerView recyclerView, promoRecycler;
    private RecyclerView.Adapter recyclerAdapter, promoAdapter;
    private RecyclerView.LayoutManager layoutManager, promoLayout;

    FirebaseAuth fbAuth;
    DatabaseReference storeDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_menu, container, false);
        fbAuth = FirebaseAuth.getInstance();
        oldBundle = getArguments();
        storeDb = FirebaseDatabase.getInstance().getReference("Stores").child(oldBundle.getString("id"));
        addItem = view.findViewById(R.id.f_store_menu_set_available);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ItemAddActivity.class);
                intent.putExtra("id", oldBundle.getString("id"));
                startActivity(intent);
            }
        });
        add = view.findViewById(R.id.f_store_menu_add_item);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddItemActivity.class);
                intent.putExtra("id", oldBundle.getString("id"));
                startActivity(intent);
            }
        });

        initAvailMenu(view);
        setView(view);

        return view;
    }

    private void setView(View view)
    {
        storeDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(oldBundle.getBoolean("byOwner") && dataSnapshot.child("uid").getValue(String.class).equals(fbAuth.getUid()))
                {
                    addItem.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initAvailMenu(final View view) {
        String id = oldBundle.getString("id");
        DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(id)
                .child("menu");
        storeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();
                ArrayList<String> prices = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("availability").getValue().equals("available")) {
                        names.add(data.child("name").getValue(String.class));
                        images.add(data.child("image_url").getValue(String.class));
                        prices.add(data.child("price").getValue(String.class));

                        boolean key = oldBundle.getBoolean("byOwner");

                        recyclerView = view.findViewById(R.id.f_store_menu_view);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getContext());
                        recyclerAdapter = new AvailMenuAdapter(images, names, prices, StoreMenuFragment.this, key, data.getKey(), data.child("image_url").getValue(String.class));

                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(recyclerAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMenuClick(int position, String url) {
        imageZoom = new Dialog(getActivity());
        imageZoom.setContentView(R.layout.dialog_image_view);

        closeDialog = imageZoom.findViewById(R.id.d_image_close_btn);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageZoom.dismiss();
            }
        });
        zoomed = imageZoom.findViewById(R.id.d_addPromo_image);
        Picasso.get()
                .load(url)
                .into(zoomed);

        imageZoom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        imageZoom.show();
    }

    @Override
    public void onUnavailableClick(int position, String id) {

    }

    @Override
    public void onDeleteClick(int position, String id) {

    }
}
