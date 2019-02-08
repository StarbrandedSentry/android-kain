package com.oddlid.karinderya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ActRequestActivity extends AppCompatActivity {
    //control init
    TextView id, name, owner, date, location;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<EntryUpload> mUploads;

    //firebase init
    FirebaseAuth fbAuth;
    DatabaseReference dbRef;
    StorageReference storeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_request);
        fbAuth = FirebaseAuth.getInstance();

        initRequest();
    }

    private void initRequest()
    {
        Intent intent = getIntent();
        String reqID = intent.getStringExtra("propID");

        dbRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(reqID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id = findViewById(R.id.propIDView);
                name = findViewById(R.id.propNameView);
                date = findViewById(R.id.propDateView);
                location = findViewById(R.id.propLocationView);
                Request request = dataSnapshot.getValue(Request.class);

//settings textviews
                dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(request.getUid()).child("name");
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        owner = findViewById(R.id.propOwnerView);
                        owner.setText("Owner: " + dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                id.setText("Request ID: " + dataSnapshot.getKey());
                name.setText("Karinderya Name: " + request.getName());
                date.setText("Date Made: " + request.getDate_made());
                location.setText("Location: " + request.getLocation());

                DatabaseReference imgRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(dataSnapshot.getKey()).child("entry_images");
                imgRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> mUploads = new ArrayList<>();
                        for(DataSnapshot data : dataSnapshot.getChildren())
                        {
                            mUploads.add(data.child("image_url").getValue(String.class));
                        }
                        mRecyclerView = findViewById(R.id.requestImageRecycler);
                        mRecyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new EntryImageAdapter(mUploads);

                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //communicating to the user
    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }
    private void toastMessage(Context c, String message)
    {
        Toast toast = Toast.makeText(c, message, Toast.LENGTH_LONG);
        toast.show();
    }


}
