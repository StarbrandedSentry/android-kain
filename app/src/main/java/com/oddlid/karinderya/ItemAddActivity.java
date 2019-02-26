package com.oddlid.karinderya;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemAddActivity extends AppCompatActivity implements ItemAddAdapter.OnMenuListener{
    Intent oldIntent;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    DatabaseReference storeDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add);
        oldIntent = getIntent();
        storeDb = FirebaseDatabase.getInstance().getReference("Stores");

        initItems();
    }

    private void initItems()
    {
        DatabaseReference menuDb = storeDb.child(oldIntent.getStringExtra("id")).child("menu");
        menuDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> ids = new ArrayList<>();
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> prices = new ArrayList<>();
                ArrayList<String> availabilities = new ArrayList<>();
                ArrayList<String> image_urls = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    ids.add(data.getKey() + "");
                    names.add(data.child("name").getValue(String.class));
                    image_urls.add(data.child("image_url").getValue(String.class));
                    availabilities.add(data.child("availability").getValue(String.class));
                    prices.add(data.child("price").getValue(String.class) + "Php");
                }

                recyclerView = findViewById(R.id.a_item_add_view);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerAdapter = new ItemAddAdapter(ids, names, prices, availabilities, image_urls, ItemAddActivity.this);

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*menuDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> ids = new ArrayList<>();
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> prices = new ArrayList<>();
                ArrayList<String> availabilities = new ArrayList<>();
                ArrayList<String> image_urls = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    ids.add(data.getKey() + "");
                    names.add(data.child("name").getValue(String.class));
                    image_urls.add(data.child("image_url").getValue(String.class));
                    availabilities.add(data.child("availability").getValue(String.class));
                    prices.add(data.child("price").getValue(String.class) + "Php");
                }

                recyclerView = findViewById(R.id.a_item_add_view);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerAdapter = new ItemAddAdapter(ids, names, prices, availabilities, image_urls, ItemAddActivity.this);

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    public void onMenuClick(int position, String id, String availabilty) {
        return;
    }

    @Override
    public void onSwitchClick(int position, String id, boolean isChecked) {
        DatabaseReference menuDb = FirebaseDatabase.getInstance().getReference("Stores").child(oldIntent.getStringExtra("id")).child("menu")
                .child(id);
        if(!isChecked)
        {
            menuDb.child("availability").setValue("unavailable").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(isChecked)
        {
            menuDb.child("availability").setValue("available").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //initItems();
    }

}
