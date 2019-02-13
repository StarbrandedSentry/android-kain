package com.oddlid.karinderya;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class ActiveStoreActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    //control init
    TextView location;
    TextView name;
    ImageView banner;
    ProgressBar uploadProg;

    private Uri imageUri;

    //firebase init
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("Stores");


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
        }
        banner = findViewById(R.id.a_activeStore_banner);
        initInfo();
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
}
