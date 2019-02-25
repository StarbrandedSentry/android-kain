package com.oddlid.karinderya;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oddlid.karinderya.models.DatePickerFragment;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {
    //other initializations
    private final int REQUEST_CODE = 1553, REQUEST_EXTERNAL_STORAGE = 1;
    private Date currentDate;
    private boolean success, flag;
    private String uid;
    String promoFrom, promoTo, period, mTimeFrom, mTimeTo, requestId, mDateStarted, mTimeType;
    private int timeType;
    private String time;
    Button registerBtn;
    private Uri pdfUri, idUri, bannerUri;
    private EditText editName, editLocation, editCity, editContactNumber, editCapacity;
    ProgressBar pb;
    TextView dateStarted;
    DatePickerDialog.OnDateSetListener dateStartedSetListener;
    Request request;

    //firebase initializations
    FirebaseAuth fbAuth;
    FirebaseDatabase fbDatabase;
    FirebaseStorage fbStorage;
    DatabaseReference storeDB, requestDB;
    StorageReference storeRef;
    StorageReference fileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fbAuth = FirebaseAuth.getInstance();
        uid = fbAuth.getUid();
        fileRef = FirebaseStorage.getInstance().getReference("Stores");

        dateStarted = findViewById(R.id.a_register_date_started);
        dateStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Calender cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);*/

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        //Time Spinner Logic
        Spinner timeSpinner = findViewById(R.id.a_register_operating_hours);
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.operating_hours, R.layout.dark_flat_spinner);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);
        timeSpinner.setOnItemSelectedListener(this);
        TextView timeFrom = findViewById(R.id.a_register_from_time);
        timeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = 1;
                Bundle promo = new Bundle();
                promo.putInt("picker_id", 1);
                DialogFragment timePicker = new PromoTimeFragment();
                timePicker.show(getSupportFragmentManager(), "Promo Time");
            }
        });
        TextView timeTo = findViewById(R.id.a_register_to_time);
        timeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = 2;
                Bundle promo = new Bundle();
                promo.putInt("picker_id", 1);
                DialogFragment timePicker = new PromoTimeFragment();
                timePicker.show(getSupportFragmentManager(), "Promo Time");
            }
        });

        registerBtn = findViewById(R.id.registerBtn);
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
        if(bannerUri == null || pdfUri == null || idUri == null)
        {
            return;
        }
        pb = findViewById(R.id.a_register_pb);
        pb.setVisibility(View.VISIBLE);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setEnabled(false);

        //check if the karinderya is 24 hours
        editName = findViewById(R.id.editStoreName);
        editLocation = findViewById(R.id.a_register_street_address);
        editCity = findViewById(R.id.a_register_city);
        editContactNumber = findViewById(R.id.a_register_contact_number);
        editCapacity = findViewById(R.id.a_register_capacity);
        if(time.equals("24 hours"))
        {
            request = new Request(editName.getText().toString(), uid, getCurrentDate(), editLocation.getText().toString(), editCity.getText().toString()
                    , editContactNumber.getText().toString(), editCapacity.getText().toString(), mDateStarted, "pending", time);
        }
        else
        {
            request = new Request(editName.getText().toString(), uid, getCurrentDate(), editLocation.getText().toString(), editCity.getText().toString()
                    , editContactNumber.getText().toString(), editCapacity.getText().toString(), mDateStarted, "pending", time, mTimeFrom, mTimeTo);
        }

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Stores");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                while(!flag)
                {
                    requestId = random();
                    if(!dataSnapshot.hasChild(requestId))
                    {
                        requestDB = FirebaseDatabase.getInstance().getReference().child("Stores").child(requestId);
                        //save request

                        requestDB.setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Saving banner
                                final StorageReference bannerStore = fileRef.child(requestId).child("banner");
                                bannerStore.putFile(bannerUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //saving banner url
                                        bannerStore.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                requestDB.child("banner").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //Saving permit
                                                        final StorageReference permitStore = fileRef.child(requestId).child("barangay_permit");
                                                        permitStore.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                //saving permit url
                                                                permitStore.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        requestDB.child("barangay_permit").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                final StorageReference idStore = fileRef.child(requestId).child("valid_id");
                                                                                idStore.putFile(idUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                        //saving valid url
                                                                                        idStore.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                            @Override
                                                                                            public void onSuccess(Uri uri) {
                                                                                                requestDB.child("valid_id").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        //add only after
                                                                                                        //Toast.makeText(getApplicationContext(), "Done! Now wait until your request gets accepted!", Toast.LENGTH_LONG).show();
                                                                                                        finish();
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });

                        //perhaps here
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
            return;
        }

        selectPdf();
    }

    public void chooseID(View view)
    {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1532);
            return;
        }

        selectID();
    }

    public void chooseBanner(View view)
    {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1533);
            return;
        }

        selectBanner();
    }

    private void selectPdf()
    {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0531);
    }

    private void selectID()
    {
        Intent intent = new Intent();
        intent.setType("pdf/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0532);
    }

    private void selectBanner()
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


    //called after every request permission is answered
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1531)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                selectPdf();
            }
        }
        else if(requestCode == 1532)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                selectID();
            }
        }
        else if(requestCode == 1533)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                selectBanner();
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

            }
        }
        else if(requestCode == 0532)
        {
            if(resultCode == RESULT_OK && data!=null)
            {
                idUri = data.getData();
                TextView idName = findViewById(R.id.a_register_id_text);
                idName.setText(idUri.getLastPathSegment());
            }
        }
        else if(requestCode == 0533)
        {
            if(resultCode == RESULT_OK && data!=null)
            {
                bannerUri = data.getData();
                ImageView banner = findViewById(R.id.a_register_banner);
                Picasso.get()
                        .load(bannerUri)
                        .into(banner);
            }
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDate = DateFormat.getDateInstance().format(c.getTime());
        dateStarted.setText(currentDate);
        mDateStarted = currentDate;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        period = "am";
        if (hourOfDay >= 13) {
            hourOfDay -= 12;
            period = "pm";
        }

        switch (timeType) {
            case 1: //time from
                promoFrom = hourOfDay + ":" + String.format("%02d", minute);
                TextView timeFrom = findViewById(R.id.a_register_from_time);
                timeFrom.setText(promoFrom + " " + period);
                mTimeFrom = promoFrom + " " + period;
                break;
            case 2: //time to
                promoTo = hourOfDay + ":" + String.format("%02d", minute);
                TextView timeTo = findViewById(R.id.a_register_to_time);
                timeTo.setText(promoTo + " " + period);
                mTimeTo = promoTo + " " + period;
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        time = parent.getItemAtPosition(position).toString();
        if (position == 1) { //set visible
            CardView cardView = findViewById(R.id.a_register_container_time);
            cardView.setVisibility(View.VISIBLE);
        } else if (position == 0) {
            CardView cardView = findViewById(R.id.a_register_container_time);
            cardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
