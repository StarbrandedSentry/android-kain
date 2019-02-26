package com.oddlid.karinderya;


import android.content.Intent;
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
public class HomeFragment extends Fragment implements HomeAdapter.OnCardListener {
    //init arrays
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initStores();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void initStores()
    {
        DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference().child("Stores");
        storeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> locations = new ArrayList<>();
                ArrayList<String> banners = new ArrayList<>();
                ArrayList<String> ratings = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    if(data.child("status").getValue().equals("accepted"))
                    {
                        locations.add(data.child("street_address").getValue(String.class) + ", " + data.child("city").getValue(String.class));
                        names.add(data.child("name").getValue(String.class));
                        banners.add(data.child("banner").getValue(String.class));
                        if(!data.child("rating").exists())
                        {
                            ratings.add("No rating");
                            continue;
                        }

                        ratings.add(String.format("%.2f", data.child("rating").getValue(float.class)));
                    }

                }
                recyclerView = getView().findViewById(R.id.f_home_recycler);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerAdapter = new HomeAdapter(names, locations, banners, ratings, HomeFragment.this);

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onCardClick(final int position) {
        DatabaseReference requestDB = FirebaseDatabase.getInstance().getReference().child("Stores");
        requestDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> mRequestID = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    mRequestID.add(data.getKey());
                }
                final String id = mRequestID.get(position);

                Intent intent = new Intent(getActivity(), ActiveStoreActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("byOwner", false);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
