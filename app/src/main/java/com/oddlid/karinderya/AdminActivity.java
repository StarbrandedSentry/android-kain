package com.oddlid.karinderya;

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

public class AdminActivity extends AppCompatActivity {
    //init arrays

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
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    mDateMade.add(data.child("date_made").getValue(String.class));
                    mStoreNames.add(data.child("name").getValue(String.class));
                }
                RecyclerView recyclerView = findViewById(R.id.pendingRecycler);
                RecyclerView.Adapter adapter = new RecyclerViewAdapter(mStoreNames, mDateMade);
                RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
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
}
