package com.oddlid.karinderya.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.oddlid.karinderya.R;
import com.oddlid.karinderya.Request;

public class StoreDetailsFragment extends Fragment {
    float count, mRating, newRate;
    float rate;

    Bundle oldBundle;
    TextView name, dateStarted, location, contactNumber, operatingHours, rateThis;
    Button submitRating;
    RatingBar storeRating, storeRatingView;
    Button setGeoPoint;
    TextView geoPointTitle, geoPoint;
    String latitude, longitude;
    GeoPoint geoPointVal;

    private FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseUser fbAuth;
    DatabaseReference storeDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_store_details, container, false);
        fbAuth = FirebaseAuth.getInstance().getCurrentUser();
        oldBundle = getArguments();
        storeDb = FirebaseDatabase.getInstance().getReference("Stores").child(oldBundle.getString("id"));

        //initialize them
        name = view.findViewById(R.id.f_store_detail_name);
        dateStarted = view.findViewById(R.id.f_store_detail_date_started);
        location = view.findViewById(R.id.f_store_detail_location);
        contactNumber = view.findViewById(R.id.f_store_detail_contact_number);
        operatingHours = view.findViewById(R.id.f_store_detail_operating_hours);
        storeRating = view.findViewById(R.id.f_store_detail_rating);
        storeRatingView = view.findViewById(R.id.f_store_detail_rating_view);
        rateThis = view.findViewById(R.id.f_store_detail_rating_this_title);
        submitRating = view.findViewById(R.id.f_store_detail_submit_rating);
        geoPoint = view.findViewById(R.id.f_store_detail_geopoint);
        geoPointTitle = view.findViewById(R.id.f_store_detail_geopoint_title);
        setGeoPoint = view.findViewById(R.id.f_store_detail_set_geopoint);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //work
        setEnabled(view);
        initDetails(view);

        //setting on clicks
        submitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (storeRating.getRating() == 0) {
                            return;
                        }

                        if (dataSnapshot.child("rated").exists()) {
                            count = dataSnapshot.child("rated").getValue(float.class) + 1;
                        } else {
                            count = 1;
                        }

                        if (dataSnapshot.child("rating").exists()) {
                            rate = dataSnapshot.child("rating").getValue(float.class);
                        } else {
                            rate = 0;
                        }


                        mRating = storeRating.getRating();

                        if (dataSnapshot.child("rated_by").child(fbAuth.getUid()).exists()) {
                            count--;
                            rate = (rate * count) - dataSnapshot.child("rated_by").child(fbAuth.getUid()).getValue(float.class);
                        }

                        newRate = (rate + mRating) / count;

                        storeDb.child("rating").setValue(newRate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                storeDb.child("rated").setValue(count).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        storeDb.child("rated_by").child(fbAuth.getUid()).setValue(newRate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Rated!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        setGeoPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastKnownLocation();

            }
        });

        return view;
    }

    private void initDetails(final View view) {
        storeDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Request request = dataSnapshot.getValue(Request.class);

                name.setText(request.getName());
                dateStarted.setText(request.getDate_started());
                location.setText(request.getStreet_address() + ", " + request.getCity());
                contactNumber.setText(request.getContact_number());

                if (dataSnapshot.child("time_to").exists()) //if not 24 hours
                {
                    operatingHours.setText(request.getTime_from() + " - " + request.getTime_to());
                } else //if all day
                {
                    operatingHours.setText(request.getOperating_hours());
                }

                if (dataSnapshot.child("rating").exists()) {
                    storeRatingView.setRating(dataSnapshot.child("rating").getValue(float.class));
                }

                if (dataSnapshot.child("rated_by").child(fbAuth.getUid()).exists()) {
                    storeRating.setRating(dataSnapshot.child("rated_by").child(fbAuth.getUid()).getValue(float.class));
                }

                if(dataSnapshot.child("uid").getValue(String.class).equals(fbAuth.getUid())
                    && dataSnapshot.child("geo_location").exists())
                {
                    geoPoint.setText(dataSnapshot.child("geo_location").child("latitude").getValue().toString() + ", " + dataSnapshot.child("geo_location").child("longitude").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setEnabled(View view) {
        storeDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("uid").getValue(String.class).equals(fbAuth.getUid())) {
                    storeRating.setVisibility(View.GONE);
                    submitRating.setVisibility(View.GONE);
                    rateThis.setVisibility(View.GONE);

                    setGeoPoint.setVisibility(View.VISIBLE);
                    geoPoint.setVisibility(View.VISIBLE);
                    geoPointTitle.setVisibility(View.VISIBLE);
                }

                //TRY

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    geoPointVal = new GeoPoint(location.getLatitude(), location.getLongitude());
                    latitude = location.getLatitude() + "";
                    longitude = location.getLongitude() + "";

                    geoPoint.setText(latitude + ", " + longitude);

                    storeDb.child("geo_location").setValue(geoPointVal).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Map Location Updated!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}
