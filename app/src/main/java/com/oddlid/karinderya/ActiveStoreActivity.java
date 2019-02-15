package com.oddlid.karinderya;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActiveStoreActivity extends AppCompatActivity implements  AvailMenuAdapter.OnMenuListener{
    private static final int PICK_IMAGE_REQUEST = 1;

    //control init
    Dialog imageZoom;
    TextView location;
    TextView name;
    ImageView banner;
    ProgressBar uploadProg;
    ImageView zoomed;
    ImageButton closeDialog;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Uri imageUri;

    //firebase init
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("Stores");
    DatabaseReference itemDB = FirebaseDatabase.getInstance().getReference("Stores");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_store);
        Intent intent = getIntent();
        //IF ACTIVITY WAS OPENED BY OWNER
        if(intent.getExtras().getBoolean("byOwner"))
        {
            //add to show
            Button addAvailable = findViewById(R.id.a_activeStore_addAvailable_btn);
            addAvailable.setVisibility(View.VISIBLE);
            Button changeBanner = findViewById(R.id.a_activeStore_changeBanner_btn);
            changeBanner.setVisibility(View.VISIBLE);
            Button cancelChange = findViewById(R.id.a_activeStore_cancelChangeBanner_btn);
            cancelChange.setVisibility(View.VISIBLE);
            Button chooseImage = findViewById(R.id.a_activeStore_choosePhoto_btn);
            chooseImage.setVisibility(View.VISIBLE);
            Button addPromo = findViewById(R.id.a_activeStore_addPromo_btn);
            addPromo.setVisibility(View.VISIBLE);
        }
        banner = findViewById(R.id.a_activeStore_banner);

        initInfo();
        initAvailMenu();
    }

    private void initInfo()
    {
        String id = getIntent().getStringExtra("id");
        DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(id);
        storeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                location = findViewById(R.id.a_activeStore_location_text);
                name = findViewById(R.id.a_activeStore_name_text);
                name.setText(dataSnapshot.child("name").getValue().toString());
                location.setText(dataSnapshot.child("location").getValue().toString());
                banner = findViewById(R.id.a_activeStore_banner);
                Picasso.get()
                        .load(dataSnapshot.child("banner").getValue().toString())
                        .into(banner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initAvailMenu()
    {
        String id = getIntent().getStringExtra("id");
        DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(id)
                .child("menu");
        storeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    if(data.child("availability").getValue().equals("available"))
                    {
                        names.add(data.child("name").getValue(String.class));
                        images.add(data.child("image_url").getValue(String.class));

                        boolean key = getIntent().getExtras().getBoolean("byOwner");

                        recyclerView = findViewById(R.id.a_actStore_availMenu);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new GridLayoutManager(ActiveStoreActivity.this, 2);
                        recyclerAdapter = new AvailMenuAdapter(images, names, ActiveStoreActivity.this, key, data.getKey(), data.child("image_url").getValue(String.class));

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

    public void uploadImage(View view)
    {
        if(imageUri == null)
        {
            Snackbar.make(findViewById(android.R.id.content), "No picture picked!", Snackbar.LENGTH_LONG).show();
            return;
        }
        uploadProg = findViewById(R.id.a_activeStore_banner_pb);
        uploadProg.setVisibility(View.VISIBLE);

        final String id = getIntent().getStringExtra("id");
        final StorageReference bannerRef = storageRef.child(id).child("banner");
        bannerRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                bannerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference bannerDB = FirebaseDatabase.getInstance().getReference("Stores").child(id).child("banner");
                        bannerDB.setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                uploadProg.setVisibility(View.GONE);

                                Toast.makeText(getApplicationContext(), "Banner changed!", Toast.LENGTH_LONG);
                            }
                        });
                    }
                });

            }
        });
    }

    public void addAvailableItem(View view)
    {
        String id = getIntent().getStringExtra("id");
        Intent intent = new Intent(ActiveStoreActivity.this, AddItemActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void chooseImage(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            Picasso.get()
                    .load(imageUri)
                    .into(banner);
        }
    }

    @Override
    public void onMenuClick(int position, String url) {
        imageZoom = new Dialog(this);
        imageZoom.setContentView(R.layout.dialog_add_promo);

        closeDialog = imageZoom.findViewById(R.id.d_addPromo_close_btn);
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
        String itemID = getIntent().getStringExtra("id");
        itemDB = itemDB.child(itemID).child("menu").child(id);

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("availability", "unavailable");
        itemDB.updateChildren(statusMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                initAvailMenu();
            }
        });
    }

    @Override
    public void onDeleteClick(int position, String id) {
        String itemID = getIntent().getStringExtra("id");
        itemDB = itemDB.child(itemID).child("menu").child(id);

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("availability", "removed");
        itemDB.updateChildren(statusMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                initAvailMenu();
            }
        });
    }


}
