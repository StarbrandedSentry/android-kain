package com.oddlid.karinderya;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    //pls allow sending data
    private static final int REQUEST_EXTERNAL_STORAGE = 1333;
    public static final int REQUEST_CODE=1531;
    private boolean changingPhoto;
    private Uri imageUri;

    //Other initializations
    String name, email, uid;
    final long ONE_MEGABYTE = 1024 * 1024;

    //initializations
    Button logoutBtn;
    Button registerBtn;
    Button openAdmin, uploadBtn;
    ImageView profileView;
    Button changeImageBtn, cancelUpload;
    String userID;
    TextView nameView, userLevelView, emailView, karinderyaCountView;
    ProgressBar uploadProgress;

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
        userID = fbAuth.getCurrentUser().getUid();
        uploadProgress = view.findViewById(R.id.f_account_upload_progress);

        //Setting up profile picture
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("profile_picture").exists())
                {
                    ImageView image = view.findViewById(R.id.profileView);
                    Picasso.get()
                            .load(dataSnapshot.child("profile_picture").getValue().toString())
                            .fit().centerCrop()
                            .into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //getting pictures
        changeImageBtn = view.findViewById(R.id.changeImageBtn);
        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1533);
                    return;
                }

                selectImage();
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
        setDetails();
        dbRef = fbDatabase.getInstance().getReference().child("Users").child(uid);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                switch(user.getUser_level())
                {
                    case 3:
                        openAdmin = (Button) view.findViewById(R.id.openAdmin);
                        openAdmin.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                snackbarMessage(getView(), databaseError.getMessage());
            }
        });


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

        //Uploading image logic
        uploadBtn = view.findViewById(R.id.f_account_change_photo);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logic for uploading photo
                if(imageUri == null)
                {
                    return;
                }
                cancelUpload = view.findViewById(R.id.f_account_cancel_change_photo);
                cancelUpload.setVisibility(View.GONE);
                uploadProgress.setVisibility(View.VISIBLE);
                final StorageReference userStore = FirebaseStorage.getInstance().getReference("Users").child(userID).child("profile_picture");
                userStore.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        userStore.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("profile_picture");
                                userDb.setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Profile Picture saved!", Toast.LENGTH_SHORT).show();
                                        uploadBtn.setVisibility(View.GONE);
                                        uploadProgress.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });

        //cancel upload logic
        cancelUpload = view.findViewById(R.id.f_account_cancel_change_photo);
        cancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                userDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("profile_picture").exists())
                        {
                            ImageView image = view.findViewById(R.id.profileView);
                            Picasso.get()
                                    .load(dataSnapshot.child("profile_picture").getValue().toString())
                                    .fit().centerCrop()
                                    .into(image);
                        }
                        else
                        {
                            DatabaseReference adminDb = FirebaseDatabase.getInstance().getReference().child("Admin");
                            adminDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ImageView image = view.findViewById(R.id.profileView);
                                    Picasso.get()
                                            .load(dataSnapshot.child("default_profile").getValue().toString())
                                            .fit().centerCrop()
                                            .into(image);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        cancelUpload.setVisibility(View.GONE);
                        uploadBtn = view.findViewById(R.id.f_account_change_photo);
                        uploadBtn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
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

        startActivityForResult(chooserIntent, 0533);
    }

    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

    //Set account details
    private void setDetails()
    {
        //Settings names and stuffs
        String uid = fbAuth.getUid();
        dbRef = fbDatabase.getInstance().getReference().child("Users").child(uid);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userLevelView = getView().findViewById(R.id.f_account_user_level);
                nameView = getView().findViewById(R.id.f_account_name);
                emailView = getView().findViewById(R.id.f_account_email);
                karinderyaCountView = getView().findViewById(R.id.f_account_karinderya_count);
                User user = dataSnapshot.getValue(User.class);
                //setting names
                nameView.setText(user.getName());
                emailView.setText(fbUser.getEmail());
                if(user.getUser_level() == 1)
                {
                    userLevelView.setText("Normal");
                }
                else if(user.getUser_level() == 2)
                {
                    userLevelView.setText("Owner");
                }
                else if(user.getUser_level() == 3) {
                    userLevelView.setText("Admin");
                }

                if(dataSnapshot.child("store_count").exists())
                {
                    karinderyaCountView.setText(dataSnapshot.child("store_count").getValue().toString());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                snackbarMessage(getView(), databaseError.getMessage());
            }
        });
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

    public void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch(requestCode)
        {
            case 1533:{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    selectImage();
                }
                else
                {
                    snackbarMessage(getView(), "Permission not granted. Good luck!");
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if(requestCode == 0533)
        {
            if(resultCode == RESULT_OK && data!=null)
            {
                imageUri = data.getData();
                profileView = getView().findViewById(R.id.profileView);
                Picasso.get()
                        .load(imageUri)
                        .fit().centerCrop()
                        .into(profileView);
                uploadBtn.setVisibility(View.VISIBLE);
                cancelUpload = getView().findViewById(R.id.f_account_cancel_change_photo);
                cancelUpload.setVisibility(View.VISIBLE);
            }
        }
    }
    //END
}

