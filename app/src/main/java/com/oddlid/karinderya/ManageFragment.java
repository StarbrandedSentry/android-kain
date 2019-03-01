package com.oddlid.karinderya;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageFragment extends Fragment implements RequestAdapter.OnNoteListener, WorkingStoreAdapter.OnCardListener {
    //init arrays
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    DatabaseReference cardDb;
    DatabaseReference noteDb;
    DatabaseReference storeDb;
    DatabaseReference pendingDb;

    //firebase init
    FirebaseAuth fbAuth;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public ManageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fbAuth = FirebaseAuth.getInstance();
        initPendingView();
        initActiveStores();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage, container, false);
    }

    private void initPendingView()
    {
        pendingDb = FirebaseDatabase.getInstance().getReference().child("Stores");
        pendingDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> mStoreNames = new ArrayList<>();
                ArrayList<String> mDateMade = new ArrayList<>();
                ArrayList<String> mRequestID = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    if(data.child("status").getValue().equals("pending") && data.child("uid").getValue().equals(fbAuth.getUid()))
                    {
                        mDateMade.add(data.child("street_address").getValue(String.class) + ", " + data.child("city").getValue(String.class));
                        mStoreNames.add(data.child("date_made").getValue(String.class));
                        mRequestID.add(data.child("name").getValue(String.class));
                    }
                }
                /*for(DataSnapshot data : dataSnapshot.getChildren())
                {

                    mRequestID.add(data.getKey());
                }*/
                recyclerView = getView().findViewById(R.id.f_manage_pendingRecycler);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerAdapter = new RequestAdapter(mRequestID, mStoreNames, mDateMade, ManageFragment.this);

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //snackbar
    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }


    private void initActiveStores()
    {
        storeDb = FirebaseDatabase.getInstance().getReference().child("Stores");
        storeDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> ids = new ArrayList<>();
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> locations = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    if(data.child("status").getValue().equals("accepted") && data.child("uid").getValue().equals(fbAuth.getUid()))
                    {
                        locations.add(data.child("street_address").getValue(String.class) + ", " + data.child("city").getValue(String.class));
                        names.add(data.child("name").getValue(String.class));
                        ids.add(data.child("date_made").getValue(String.class));
                    }
                }
                recyclerView = getView().findViewById(R.id.f_manage_activeRecycler);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerAdapter = new WorkingStoreAdapter(ids, names, locations, ManageFragment.this);

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onCardClick(final int position)
    {
        cardDb = FirebaseDatabase.getInstance().getReference().child("Stores");
        cardDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> mRequestID = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    if(data.child("status").getValue().equals("accepted") && data.child("uid").getValue().equals(fbAuth.getUid()))
                    {
                        mRequestID.add(data.getKey());
                    }
                }
                final String id = mRequestID.get(position);

                Intent intent = new Intent(getActivity(), ActiveStoreActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("byOwner", true);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNoteClick(final int position, String id) {
        noteDb = FirebaseDatabase.getInstance().getReference().child("Stores");
        noteDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> mRequestID = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    if(data.child("status").getValue().equals("pending") && data.child("uid").getValue().equals(fbAuth.getUid()))
                    {
                        mRequestID.add(data.getKey());
                    }
                }
                final String id = mRequestID.get(position);
                AlertDialog.Builder remove = new AlertDialog.Builder(getContext());
                remove.setMessage("Do you want to remove your registration?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(id);
                                Map<String, Object> statusMap = new HashMap<>();
                                statusMap.put("status", "removed");
                                dbRef.updateChildren(statusMap);
                            }
                        });
                remove.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
