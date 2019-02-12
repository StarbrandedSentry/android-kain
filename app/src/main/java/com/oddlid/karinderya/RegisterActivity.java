package com.oddlid.karinderya;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    //other initializations
    private final int REQUEST_CODE = 1553, REQUEST_EXTERNAL_STORAGE = 1;
    private int imageCount;
    //String id;
    private Date currentDate;
    private List<byte[]> pulledImages;
    private boolean success, flag;
    Button selectImageBtn, removeImageBtn, registerBtn;
    LinearLayout gallery;
    EditText editName, editLocation;

    //firebase initializations
    FirebaseAuth fbAuth;
    FirebaseDatabase fbDatabase;
    FirebaseStorage fbStorage;
    DatabaseReference storeDB, requestDB;
    StorageReference storeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fbAuth = FirebaseAuth.getInstance();
        //id = "";
        success = true;
        pulledImages  = new ArrayList<>();

        selectImageBtn = (Button) findViewById(R.id.selectImageBtn);

    }


    //changing the back press behavior
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Go back?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    //creating an event catcher for the alert
    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    finish();

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }

        }
    };

    //random ID generator
    protected String random() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890qwerty";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    //GETTING IMAGE FROM GALLERY
    private void handlePermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            openImageChooser();
        }
        else
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            }
            else
            {
                openImageChooser();
            }
        }
    }
    public void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch(requestCode)
        {
            case REQUEST_EXTERNAL_STORAGE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    openImageChooser();
                }
                else
                {
                    snackbarMessage(findViewById(android.R.id.content), "You need to permit access to storage. Good luck!");
                }
                return;
            }
        }
    }
    private void openImageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select images"), REQUEST_CODE);
    }
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        ClipData clipData = data.getClipData();
        if (null != data) {
            imageCount = clipData.getItemCount();
            gallery = (LinearLayout) findViewById(R.id.gallery);

            for(int i = 0; i < clipData.getItemCount(); i++)
            {
                ImageView storeImage = new ImageView(this);
                //storeImage.setMaxWidth((int) getResources().getDimension(R.dimen.squareImage));
                //storeImage.setMaxHeight((int) getResources().getDimension(R.dimen.squareImage));
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
                {
                    storeImage.setBackground(getDrawable(R.drawable.round_border_btn));
                }
                storeImage.setPadding(25, 25, 25, 25);
                storeImage.setAdjustViewBounds(true);
                storeImage.setImageURI(clipData.getItemAt(i).getUri());

                //save bytes
                //pulledImages.add(getBytes(clipData.getItemAt(i).getUri()));

                try
                {
                    InputStream iStream =   getContentResolver().openInputStream(clipData.getItemAt(i).getUri());
                    pulledImages.add(getBytes(iStream));
                }catch (FileNotFoundException ex)
                {
                    snackbarMessage(findViewById(android.R.id.content), ex.getMessage());
                }
                catch (IOException ex)
                {
                    snackbarMessage(findViewById(android.R.id.content), ex.getMessage());
                }
                gallery.addView(storeImage);
            }
            removeImageBtn = (Button) findViewById(R.id.removeImageBtn);
            removeImageBtn.setVisibility(View.VISIBLE);

            Button register = findViewById(R.id.registerBtn);
            register.setEnabled(true);
        }
    }
    //END

    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

    //select image button logic
    public void selectImage(View view)
    {
        handlePermission();
    }

    //remove image button logic
    public void removeImage(View view)
    {
        gallery = (LinearLayout) findViewById(R.id.gallery);
        gallery.removeAllViews();
        pulledImages.clear();

        Button register = findViewById(R.id.registerBtn);
        register.setEnabled(false);

        removeImageBtn = (Button) findViewById(R.id.removeImageBtn);
        removeImageBtn.setVisibility(View.GONE);
    }

    //register button logic
    public void registerBtn(final View view)
    {
        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setEnabled(false);
        boolean unique = false;
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Stores");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                while(!flag)
                {
                    final String id = random();
                    if(!dataSnapshot.hasChild(id))
                    {
                        requestDB = FirebaseDatabase.getInstance().getReference().child("Stores").child(id);
                        //save request
                        editName = (EditText) findViewById(R.id.editStoreName);
                        final String uid = fbAuth.getUid();
                        editLocation = (EditText) findViewById(R.id.editLocation);
                        Request request = new Request(editName.getText().toString(), uid, editLocation.getText().toString(), getCurrentDate(), "pending");

                        requestDB.setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull final Task<Void> task) {
                                //int m = 1;
                                UploadTask uploadTask;
                                for(int i = 0; i < imageCount; i++)
                                {
                                    storeRef = FirebaseStorage.getInstance().getReference().child("Stores").child(id).child("entry_images").child(""+i);
                                    uploadTask = storeRef.putBytes(pulledImages.get(i));
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            snackbarMessage(view, "Oh no! something went wrong! Try again!");
                                            return;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String SALTCHARS = "1234567890";
                                                    StringBuilder salt = new StringBuilder();
                                                    Random rnd = new Random();
                                                    while (salt.length() < 3) { // length of the random string.
                                                        int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                                                        salt.append(SALTCHARS.charAt(index));
                                                    }
                                                    String saltStr = salt.toString();
                                                    final String downloadUrl = uri.toString();

                                                    //EntryUpload upload = new EntryUpload(downloadUrl);
                                                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference()
                                                            .child("Stores").child(id).child("entry_images").child(saltStr).child("image_url");
                                                    newRef.setValue(downloadUrl);
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    Toast.makeText(getApplicationContext(), "Registered! Wait until it gets approved!", Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                            });
                                        }

                                    });
                                }
                            }
                        });
                        flag = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        registerBtn.setEnabled(true);
    }

    //bytes factory
    public byte[] getBytes(InputStream inputStream) throws  IOException{
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    //get current date
    private String getCurrentDate()
    {
        currentDate = Calendar.getInstance().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String date = sdf.format(currentDate);
        return date;
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}
