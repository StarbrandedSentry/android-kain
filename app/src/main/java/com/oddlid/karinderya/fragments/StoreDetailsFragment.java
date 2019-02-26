package com.oddlid.karinderya.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oddlid.karinderya.R;
import com.oddlid.karinderya.Request;

public class StoreDetailsFragment extends Fragment {
    Bundle oldBundle;
    TextView name, dateStarted, location, contactNumber, operatingHours, rateThis;
    Button submitRating;
    RatingBar storeRating, storeRatingView;

    FirebaseAuth fbAuth;
    DatabaseReference storeDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_store_details, container, false);
        fbAuth = FirebaseAuth.getInstance();
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

        //setting on clicks
        submitRating = view.findViewById(R.id.f_store_detail_submit_rating);
        submitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(storeRating.getRating() == 0)
                        {
                            return;
                        }

                        if(dataSnapshot.child("rated_by").child(fbAuth.getUid()).exists())
                        {
                            final float count = dataSnapshot.child("rated").getValue(float.class);
                            float rate = dataSnapshot.child("rating").getValue(float.class);
                            final float newRate = ((rate - dataSnapshot.child("rated_by").child(fbAuth.getUid()).getValue(float.class)) + storeRating.getRating()) / count;
                            final float mRating = storeRating.getRating();

                            storeDb.child("rating").setValue(newRate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storeDb.child("rated").setValue(count).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            storeDb.child("rated_by").child(fbAuth.getUid()).setValue(mRating).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Rating Changed!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        else if(dataSnapshot.child("rating").exists() && !dataSnapshot.child("rated_by").child(fbAuth.getUid()).exists())
                        {
                            float count = dataSnapshot.child("rated").getValue(float.class);
                            float rate = dataSnapshot.child("rating").getValue(float.class);
                            final float newCount = count + 1;
                            final float newRate = (rate + storeRating.getRating()) / newCount;
                            final float mRating = storeRating.getRating();

                            storeDb.child("rating").setValue(newRate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storeDb.child("rated").setValue(newCount).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            storeDb.child("rated_by").child(fbAuth.getUid()).setValue(mRating).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                        else if(!dataSnapshot.child("rating").exists() && !dataSnapshot.child("rated_by").child(fbAuth.getUid()).exists())
                        {
                            final float number = 1;
                            final float mRating = storeRating.getRating();
                            storeDb.child("rating").setValue(mRating).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storeDb.child("rated").setValue(number).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            storeDb .child("rated_by").child(fbAuth.getUid()).setValue(mRating).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        //work
        setEnabled(view);
        initDetails(view);

        return view;
    }

    private void initDetails(View view)
    {
        storeDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Request request = dataSnapshot.getValue(Request.class);

                name.setText(request.getName());
                dateStarted.setText(request.getDate_started());
                location.setText(request.getStreet_address() + ", " + request.getCity());
                contactNumber.setText(request.getContact_number());

                if(dataSnapshot.child("time_to").exists()) //if not 24 hours
                {
                    operatingHours.setText(request.getTime_from() + " - " + request.getTime_to());
                }
                else //if all day
                {
                    operatingHours.setText(request.getOperating_hours());
                }

                if(dataSnapshot.child("rating").exists())
                {
                    storeRatingView.setRating(dataSnapshot.child("rating").getValue(float.class));
                }

                if(dataSnapshot.child("rated_by").child(fbAuth.getUid()).exists())
                {
                    storeRating.setRating(dataSnapshot.child("rated_by").child(fbAuth.getUid()).getValue(float.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setEnabled(View view)
    {
        storeDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("uid").getValue(String.class).equals(fbAuth.getUid()))
                {
                    storeRating.setVisibility(View.GONE);
                    submitRating.setVisibility(View.GONE);
                    rateThis.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
