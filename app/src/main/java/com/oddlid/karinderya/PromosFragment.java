package com.oddlid.karinderya;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PromosFragment extends Fragment implements PromoAdapter.OnMenuListener{
    private RecyclerView recyclerView, promoRecycler;
    private RecyclerView.Adapter recyclerAdapter, promoAdapter;
    private RecyclerView.LayoutManager layoutManager, promoLayout;

    public PromosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initPromo();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_promos, container, false);
    }

    private void initPromo()
    {
        final String id = "";
        DatabaseReference promoDB = FirebaseDatabase.getInstance().getReference("Promos");
        promoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> store_ids = new ArrayList<>();
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                ArrayList<String> stores = new ArrayList<>();
                ArrayList<String> types = new ArrayList<>();
                ArrayList<String> times = new ArrayList<>();
                ArrayList<String> time_frames = new ArrayList<>();
                ArrayList<String> promo_ids = new ArrayList<>();
                //boolean key = getIntent().getExtras().getBoolean("byOwner");
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    promo_ids.add(data.getKey());
                    store_ids.add(data.child("store_id").getValue(String.class));
                    stores.add(data.child("store_name").getValue(String.class));
                    names.add(data.child("name").getValue(String.class));
                    descriptions.add(data.child("description").getValue(String.class));
                    types.add(data.child("type").getValue(String.class));
                    times.add(data.child("time_type").getValue(String.class));
                    if(!data.child("time_from").equals("") || !data.child("time_to").equals(""))
                    {
                        time_frames.add("");
                        continue;
                    }
                    time_frames.add(data.child("time_from").getValue(String.class) + " - " + data.child("time_to").getValue(String.class));

                }
                promoRecycler = getView().findViewById(R.id.f_promo_view);
                promoRecycler.setHasFixedSize(true);
                promoLayout = new LinearLayoutManager(getContext());
                promoAdapter = new PromoAdapter(promo_ids, store_ids, stores, names, descriptions, types, times, time_frames,
                        PromosFragment.this, id, false);

                promoRecycler.setLayoutManager(promoLayout);
                promoRecycler.setAdapter(promoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMenuClick(int position, String id) {

    }

    @Override
    public void onRemove(int position, ArrayList<String> promo_ids) {

    }
}
