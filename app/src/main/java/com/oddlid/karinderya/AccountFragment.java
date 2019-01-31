package com.oddlid.karinderya;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    //pls allow sending data
    private static final int REQUEST_EXTERNAL_STORAGE = 1333;
    public static final int REQUEST_CODE=1531;

    //Other initializations
    String name, email, uid;
    final long ONE_MEGABYTE = 1024 * 1024;

    //initializations
    Button logoutBtn;
    Button registerBtn;
    Button openAdmin, uploadBtn;
    ImageView profileView;
    Button changeImageBtn;

    //firebase initializations
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;
    StorageReference storeRef;
    FirebaseDatabase fbDatabase;



    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account, container, false);
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase.getInstance();
        final String uid = fbAuth.getCurrentUser().getUid();
        String userID = fbAuth.getCurrentUser().getUid();
        storeRef = FirebaseStorage.getInstance().getReference().child("Users").child(userID).child("profile_picture");
        profileView = (ImageView) view.findViewById(R.id.profileView);
        storeRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if(bytes != null)
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profileView.setImageBitmap(bitmap);
                }
            }
        });
        //getting pictures
        changeImageBtn = (Button) view.findViewById(R.id.changeImageBtn);
        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                handlePermission();
            }
        });
        //end of that hell

        openAdmin = (Button) view.findViewById(R.id.openAdmin);
        openAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminActivity.class);
                startActivity(intent);
            }
        });

        //setting up
        final TextView nameText = (TextView) view.findViewById(R.id.nameText);
        final TextView emailText = (TextView) view.findViewById(R.id.emailText);
        final TextView leveluserText = (TextView) view.findViewById(R.id.userlevelText);
        profileView = (ImageView) view.findViewById(R.id.profileView);
        setDetails(nameText, emailText, leveluserText);
        switch(getArguments().getInt("userLevel"))
        {
            case 3:
                openAdmin.setVisibility(View.VISIBLE);
                break;
            default:
                    break;
        }

        //logout button logic
        logoutBtn = (Button) view.findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you wanted to logout?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        //register button logic
        registerBtn = (Button) view.findViewById(R.id.registerKarinderya);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        //uploading picture
        uploadBtn = (Button) view.findViewById(R.id.registerBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeRef = FirebaseStorage.getInstance().getReference().child("Users").child(uid).child("profile_picture");
                profileView.setDrawingCacheEnabled(true);
                profileView.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) profileView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storeRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        snackbarMessage(view, "Something went wrong! Try again");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadBtn.setVisibility(View.GONE);
                        snackbarMessage(view, "Profile picture updated!");
                    }
                });
            }
        });
        //done

        return view;
    }

    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

    //Set texts
    private void setDetails(TextView name, TextView email, TextView userType)
    {
        name.setText(getArguments().getString("name"));
        email.setText(getArguments().getString("email"));
        userType.setText(getArguments().getString("userType"));
    }


    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        fbAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }

        }
    };

    //GETTING IMAGE FROM GALLERY
    private void handlePermission()
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        {
            openImageChooser();
        }
        else
        {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
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
                    snackbarMessage(getView(), "Permission not granted. Good luck!");
                }
                return;
            }
        }
    }
    private void openImageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select new profile pictuer"), REQUEST_CODE);
    }
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && null != data) {
                    final Uri selectedImage = data.getData();
                    profileView.post(new Runnable() {
                        @Override
                        public void run() {
                            profileView.setImageURI(selectedImage);
                            uploadBtn = (Button) getActivity().findViewById(R.id.registerBtn);
                            uploadBtn.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }).start();

        super.onActivityResult(requestCode, resultCode, data);
    }
    //END
}

