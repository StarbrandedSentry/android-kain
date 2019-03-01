package com.oddlid.karinderya;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

import java.util.Random;

public class AddItemActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST_CODE = 300;

    ProgressBar pb;
    private Uri imageUri;
    public ImageView image;
    EditText name, price;
    Boolean flag;

    DatabaseReference storeDB = FirebaseDatabase.getInstance().getReference("Stores");
    StorageReference storeST = FirebaseStorage.getInstance().getReference("Stores");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        name = findViewById(R.id.a_addItem_name);
        price = findViewById(R.id.a_addItem_price);

        pb = findViewById(R.id.a_addItem_pb);
        image = findViewById(R.id.a_addItem_image);
    }

    public void chooseImage(View view)
    {
        if(ContextCompat.checkSelfPermission(AddItemActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(AddItemActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1531);
        }

        selectImage();
    }

    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(intent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST_CODE);
    }


    public void uploadImage(final View view)
    {
        final String id = getIntent().getStringExtra("id");
        flag = false;
        DatabaseReference key = storeDB.child(id).child("menu");
        key.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                while(!flag)
                {
                    final String itemID = random();
                    if(!dataSnapshot.hasChild(itemID))
                    {
                        pb.setVisibility(View.VISIBLE);
                        //start uploading!
                        if(imageUri != null)
                        {
                            pb.setVisibility(View.VISIBLE);
                            final StorageReference menuST = storeST.child(id).child("menu").child(itemID);
                            menuST.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    menuST.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            MenuItem menu = new MenuItem(name.getText().toString(), uri.toString(), "unavailable", price.getText().toString());
                                            DatabaseReference menuDB = storeDB.child(id).child("menu").child(itemID);
                                            menuDB.setValue(menu).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    pb.setVisibility(View.GONE);
                                                    Snackbar.make(view, "Done!", Snackbar.LENGTH_SHORT).show();

                                                    name.setText("");
                                                    price.setText("");
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }


                        flag = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1531)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                selectImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                imageUri = data.getData();

                Picasso.get()
                        .load(imageUri)
                        .fit().centerCrop()
                        .into(image);
            }
        }
    }

    protected String random() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890qwerty";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 3) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
