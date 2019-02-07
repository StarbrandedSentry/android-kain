package com.oddlid.karinderya;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class ActRequestActivity extends AppCompatActivity {
    //control init
    TextView id, name, owner, date, location;

    //firebase init
    FirebaseAuth fbAuth;
    DatabaseReference dbRef;
    StorageReference storeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_request);
        fbAuth = FirebaseAuth.getInstance();


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
                owner = findViewById(R.id.propOwnerView);
                date = findViewById(R.id.propDateView);
                location = findViewById(R.id.propLocationView);
                Request request = dataSnapshot.getValue(Request.class);

                id.setText(dataSnapshot.getKey());
                name.setText(request.getName());
                date.setText(request.getDate_made());
                location.setText(request.getLocation());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
