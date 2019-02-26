package com.oddlid.karinderya;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ActRequestActivity extends AppCompatActivity {
    //control init
    TextView id, name, owner, date, street_address, city, contact_number;
    TextView capacity, date_started, operating_hours, hours;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView banner;
    Button acceptBtn;
    File certificate, validId;

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

        dbRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(reqID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id = findViewById(R.id.propIDView);
                name = findViewById(R.id.propNameView);
                date = findViewById(R.id.propDateView);
                street_address = findViewById(R.id.propLocationView);
                city = findViewById(R.id.a_act_request_city);
                contact_number = findViewById(R.id.a_act_request_contact_number);
                capacity = findViewById(R.id.a_act_request_capacity);
                date_started = findViewById(R.id.a_act_request_date_started);
                operating_hours = findViewById(R.id.a_act_request_operating_hours);
                hours = findViewById(R.id.a_act_request_hours);
                banner = findViewById(R.id.a_act_request_banner);

                Request request = dataSnapshot.getValue(Request.class);

                //settings textviews
                dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(request.getUid()).child("name");
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                date.setText("Request Date: " + request.getDate_made());
                street_address.setText("Street Address: " + request.getStreet_address());
                city.setText("City: " + request.getCity());
                contact_number.setText("Contact Number: " + request.getContact_number());
                capacity.setText("Capacity: " + request.getCapacity());
                date_started.setText("Date Started: " + request.getDate_started());
                operating_hours.setText("Operating Hours: " + request.getOperating_hours());
                Picasso.get()
                        .load(dataSnapshot.child("banner").getValue(String.class))
                        .fit().centerInside()
                        .into(banner);
                if(dataSnapshot.child("time_from").exists())
                {
                    hours.setText("Open Hours: " + request.getTime_from() + " - " + request.getTime_to());
                }
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

    public void AcceptRequest(View view)
    {
        AlertDialog.Builder accept = new AlertDialog.Builder(this);
        accept.setMessage("Are you sure to accept this request?").setPositiveButton("Yes", acceptClickListener)
                .setNegativeButton("No!", acceptClickListener).show();
    }

    private DialogInterface.OnClickListener acceptClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = getIntent();
                    final String reqID = intent.getStringExtra("propID");
                    Map<String, Object> statusMap = new HashMap<>();
                    statusMap.put("status", "accepted");
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(reqID).child("status");
                    dbRef.setValue("accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            final DatabaseReference ddRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fbAuth.getUid());
                            ddRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child("store_count").exists())
                                    {
                                        int count = dataSnapshot.child("store_count").getValue(int.class) + 1;
                                        ddRef.child("store_count").setValue(count).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                                                finish();
                                                /*Intent open = new Intent(ActRequestActivity.this, AdminActivity.class);
                                                open.putExtra("message", "Request has been accepted!");
                                                open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(open);*/
                                            }
                                        });
                                    }
                                    else
                                    {
                                        ddRef.child("store_count").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                                                finish();
                                                /*Intent open = new Intent(ActRequestActivity.this, AdminActivity.class);
                                                open.putExtra("message", "Request has been accepted!");
                                                open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(open);*/
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    });

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public void DenyRequest(View view)
    {
        AlertDialog.Builder deny = new AlertDialog.Builder(this);
        deny.setMessage("Are you sure you want to deny this request?").setPositiveButton("Yes!", denyClickListener)
                .setNegativeButton("No! Wait", denyClickListener).show();
    }

    private DialogInterface.OnClickListener denyClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = getIntent();
                    String reqID = intent.getStringExtra("propID");
                    Map<String, Object> statusMap = new HashMap<>();
                    statusMap.put("status", "denied");
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(reqID).child("status");
                    dbRef.setValue("denied").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };


    public void fetchCertificate(View view)
    {
        Intent intent = getIntent();
        final String reqID = intent.getStringExtra("propID");
        File storagePath = new File(Environment.getExternalStorageDirectory(), "directory_name");
        // Create direcorty if not exists
        if(!storagePath.exists()) {
            storagePath.mkdirs();
        }
        certificate = new File(storagePath, "certificate");

        /*StorageReference certRef = FirebaseStorage.getInstance().getReference("Stores").child(reqID).child("certificate");
        certRef.getFile(certificate).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

            }
        });*/

        DatabaseReference cerRef = FirebaseDatabase.getInstance().getReference("Stores").child(reqID).child("barangay_permit");
        cerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue().toString();
                Uri uri = Uri.parse(url);

                DownloadManager dManager = (DownloadManager) ActRequestActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(ActRequestActivity.this, DIRECTORY_DOWNLOADS, "barangay permit.pdf");

                dManager.enqueue(request);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fetchValidId(View view)
    {
        Intent intent = getIntent();
        final String reqID = intent.getStringExtra("propID");
        File storagePath = new File(Environment.getExternalStorageDirectory(), "directory_name");
        // Create direcorty if not exists
        if(!storagePath.exists()) {
            storagePath.mkdirs();
        }
        validId = new File(storagePath, "valid id");


        DatabaseReference cerRef = FirebaseDatabase.getInstance().getReference("Stores").child(reqID).child("valid_id");
        cerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue().toString();
                Uri uri = Uri.parse(url);

                DownloadManager dManager = (DownloadManager) ActRequestActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setDestinationInExternalFilesDir(ActRequestActivity.this, DIRECTORY_DOWNLOADS, "valid id.pdf");

                dManager.enqueue(request);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
