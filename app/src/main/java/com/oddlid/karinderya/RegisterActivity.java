package com.oddlid.karinderya;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    //other initializations
    private final int REQUEST_CODE = 1553, REQUEST_EXTERNAL_STORAGE = 1;
    private int imageCount;
    private Date currentDate;
    private List<byte[]> pulledImages;
    private boolean success, flag;
    Button registerBtn;
    private Uri pdfUri;
    EditText editName, editLocation;
    ProgressBar pb;

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

    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

    //register button logic
    public void registerBtn(final View view)
    {
        pb = findViewById(R.id.a_register_pb);
        pb.setVisibility(View.VISIBLE);
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
                        final Request request = new Request(editName.getText().toString(), uid, editLocation.getText().toString(), getCurrentDate(), "pending");

                        requestDB.setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull final Task<Void> task) {
                                final StorageReference fileRef = FirebaseStorage.getInstance().getReference("Stores").child(id).child("certificate");
                                fileRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String url = uri.toString();

                                                requestDB = FirebaseDatabase.getInstance().getReference("Stores").child(id).child("certificate_url");
                                                requestDB.setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getApplicationContext(), "Registered! Wait until it gets approved!", Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                });
                                            }
                                        });


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                });
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

    //get current date
    private String getCurrentDate()
    {
        currentDate = Calendar.getInstance().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String date = sdf.format(currentDate);
        return date;
    }

    //getting files
    public void chooseFile(View view)
    {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1531);
        }

        selectPdf();
    }

    private void selectPdf()
    {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0531);
    }

    //called after every request permission is answered
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1531)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                selectPdf();
                registerBtn = (Button) findViewById(R.id.registerBtn);
                registerBtn.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 0531) //checks if request code is the same for getting file
        {
            if(resultCode == RESULT_OK && data != null)
            {
                pdfUri = data.getData();
                //File name = new File(pdfUri.getPath());
                //String dir = name.getParent();
                TextView uriName = findViewById(R.id.a_register_certificate_text);
                uriName.setText(pdfUri.getLastPathSegment());

                Button register = findViewById(R.id.registerBtn);
                register.setEnabled(true);

                Button remove = findViewById(R.id.a_register_removeFile_btn);
                remove.setVisibility(View.VISIBLE);
            }
        }
    }

    public void removeFile(View view)
    {
        TextView uriName = findViewById(R.id.a_register_certificate_text);
        uriName.setText("");
        pdfUri = null;

        Button remove = findViewById(R.id.a_register_removeFile_btn);
        remove.setVisibility(View.GONE);
    }

}
