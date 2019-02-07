package com.oddlid.karinderya;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements RequestAdapter.OnNoteListener {
    //init arrays
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //firebase init
    FirebaseAuth fbAuth;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        fbAuth = FirebaseAuth.getInstance();


        initRecyclerView();
    }

    private void initRecyclerView()
    {
        DatabaseReference requestDB = FirebaseDatabase.getInstance().getReference().child("Requests");
        requestDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> mStoreNames = new ArrayList<>();
                ArrayList<String> mDateMade = new ArrayList<>();
                ArrayList<String> mRequestID = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    mDateMade.add(data.child("date_made").getValue(String.class));
                    mStoreNames.add(data.child("name").getValue(String.class));
                }
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    mRequestID.add(data.getKey());
                }
                recyclerView = findViewById(R.id.pendingRecycler);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerAdapter = new RequestAdapter(mRequestID, mStoreNames, mDateMade, AdminActivity.this);

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                snackbarMessage(findViewById(android.R.id.content), databaseError.getMessage());
            }
        });

    }

    //snackbar
    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

    @Override
    public void onNoteClick(final int position) {
        DatabaseReference requestDB = FirebaseDatabase.getInstance().getReference().child("Requests");
        requestDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> mRequestID = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    mRequestID.add(data.getKey());
                }

                //new intents
                Intent intent = new Intent(getApplicationContext(), ActRequestActivity.class);
                intent.putExtra("propID", mRequestID.get(position));
                startActivity(intent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
