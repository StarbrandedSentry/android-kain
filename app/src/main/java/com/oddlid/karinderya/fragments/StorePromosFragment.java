package com.oddlid.karinderya.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.oddlid.karinderya.Promo;
import com.oddlid.karinderya.PromoAdapter;
import com.oddlid.karinderya.PromoTimeFragment;
import com.oddlid.karinderya.R;

import java.util.ArrayList;
import java.util.Random;

public class StorePromosFragment extends Fragment implements PromoAdapter.OnMenuListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {
    Bundle oldBundle;

    TextView addPromo;
    ImageButton closeDialog;
    Dialog imageZoom, promoMenu;
    private String type, time;
    String promoFrom, promoTo;
    String period;
    int promoTime, promoType;
    DatabaseReference promoDB;
    Dialog singleInput;

    private RecyclerView promoRecycler;
    private RecyclerView.Adapter promoAdapter;
    private RecyclerView.LayoutManager promoLayout;
    ValueEventListener promoListener;

    FirebaseAuth fbAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_store_promos, container, false);
        oldBundle = getArguments();
        fbAuth = FirebaseAuth.getInstance();
        promoMenu = new Dialog(getContext());
        promoMenu.setContentView(R.layout.dialog_add_promo);
        //set up

        addPromo = view.findViewById(R.id.f_store_promo_add_promo);
        addPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPromoOnClick(v);
            }
        });

        promoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String id = oldBundle.getString("id");
                ArrayList<String> store_ids = new ArrayList<>();
                ArrayList<String> stores = new ArrayList<>();
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                ArrayList<String> types = new ArrayList<>();
                ArrayList<String> times = new ArrayList<>();
                ArrayList<String> time_frames = new ArrayList<>();
                ArrayList<String> promo_ids = new ArrayList<>();
                boolean key = oldBundle.getBoolean("byOwner");
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("store_id").getValue().equals(id)) {
                        promo_ids.add(data.getKey());
                        store_ids.add(data.child("store_id").getValue(String.class));
                        stores.add(data.child("store_name").getValue(String.class));
                        names.add(data.child("name").getValue(String.class));
                        descriptions.add(data.child("description").getValue(String.class));
                        types.add(data.child("type").getValue(String.class));
                        times.add(data.child("time_type").getValue(String.class));
                        if (!data.child("time_from").equals("") || !data.child("time_to").equals("")) {
                            time_frames.add("");
                            continue;
                        }
                        time_frames.add(data.child("time_from").getValue(String.class) + " - " + data.child("time_to").getValue(String.class));
                    }
                }

                //store names
                promoRecycler = view.findViewById(R.id.f_store_promo_view);
                promoRecycler.setHasFixedSize(true);
                promoLayout = new LinearLayoutManager(getContext());
                promoAdapter = new PromoAdapter(promo_ids, store_ids, stores, names, descriptions, types, times, time_frames,
                        StorePromosFragment.this, id, key);

                promoRecycler.setLayoutManager(promoLayout);
                promoRecycler.setAdapter(promoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        initPromo(view);
        setDetails(view);

        return view;
    }

    private void initPromo(final View view) {
        promoDB = FirebaseDatabase.getInstance().getReference("Promos");
        promoDB.addValueEventListener(promoListener);
    }

    @Override
    public void onMenuClick(int position, String id) {

    }

    @Override
    public void onRemove(int position, ArrayList<String> promo_ids) {
        promoDB = FirebaseDatabase.getInstance().getReference("Promos").child(promo_ids.get(position));
        promoDB.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Promo removed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onChangeName(int position, ArrayList<String> promo_ids) {

    }

    private void setDetails(View view)
    {
        if(oldBundle.getBoolean("byOwner"))
        {
            addPromo.setVisibility(View.VISIBLE);
        }
    }

    private void addPromoOnClick(View view)
    {
        final String id = oldBundle.getString("id");

        closeDialog = promoMenu.findViewById(R.id.d_addPromo_close_btn);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoMenu.dismiss();
            }
        });
        Spinner promoType = promoMenu.findViewById(R.id.d_addPromo_typeList);
        Spinner mPromoTime = promoMenu.findViewById(R.id.d_addPromo_timeList);
        //promo type spinner
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.promo_type, R.layout.flat_spinner);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        promoType.setAdapter(typeAdapter);
        promoType.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.promo_time, R.layout.flat_spinner);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPromoTime.setAdapter(timeAdapter);
        mPromoTime.setOnItemSelectedListener(this);

        final TextView timeFrom = promoMenu.findViewById(R.id.d_addPromo_from);
        timeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoTime = 1;
                Bundle promo = new Bundle();
                promo.putInt("picker_id", 1);
                DialogFragment timePicker = new PromoTimeFragment();
                timePicker.show(getActivity().getSupportFragmentManager(), "Promo Time");
            }
        });
        TextView timeTo = promoMenu.findViewById(R.id.d_addPromo_to);
        timeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoTime = 2;
                Bundle promo = new Bundle();
                promo.putInt("picker_id", 2);
                DialogFragment timePicker = new PromoTimeFragment();
                timePicker.show(getActivity().getSupportFragmentManager(), "Promo Time");
            }
        });

        //promo creation
        final Button create = promoMenu.findViewById(R.id.d_addPromo_btn);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.setEnabled(false);
                final DatabaseReference promoDB = FirebaseDatabase.getInstance().getReference("Promos");
                //promo saving
                final TextView pName = promoMenu.findViewById(R.id.d_addPromo_promo_name);
                final TextView pDesc = promoMenu.findViewById(R.id.d_addPromo_promo_description);
                final String mName = pName.getText().toString();
                final String mDesc = pDesc.getText().toString();
                final String itemID = random();
                DatabaseReference storeDB = FirebaseDatabase.getInstance().getReference("Stores").child(id);
                storeDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String storeName = dataSnapshot.child("name").getValue(String.class);
                        Promo promo;
                        if (time.equals("Certain Time")) {
                            promo = new Promo(id, storeName, mName, mDesc, type, time, promoFrom, promoTo);
                        } else {
                            promo = new Promo(id, storeName, mName, mDesc, type, time, "", "");
                        }

                        //Toast.makeText(ActiveStoreActivity.this, storeName, Toast.LENGTH_SHORT).show();
                        promoDB.child(itemID).setValue(promo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pName.setText("");
                                pDesc.setText("");

                                Toast.makeText(getContext(), "Promo created!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        promoMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        promoMenu.show();
    }

    protected String random() {
        String SALTCHARS = "1234567890qwerty";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getCount()) {
            case 2: //for promo time
                time = parent.getItemAtPosition(position).toString();
                if (position == 1) {
                    TextView toTitle = promoMenu.findViewById(R.id.d_addPromo_to_title);
                    TextView fromTitle = promoMenu.findViewById(R.id.d_addPromo_from_title);
                    TextView to = promoMenu.findViewById(R.id.d_addPromo_to);
                    TextView from = promoMenu.findViewById(R.id.d_addPromo_from);

                    toTitle.setVisibility(View.VISIBLE);
                    fromTitle.setVisibility(View.VISIBLE);
                    to.setVisibility(View.VISIBLE);
                    from.setVisibility(View.VISIBLE);

                    /*from = promoMenu.findViewById(R.id.d_addPromo_from);
                    from.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            promoTime = 1;
                            Bundle promo = new Bundle();
                            promo.putInt("picker_id", 1);
                            DialogFragment timePicker = new PromoTimeFragment();
                            timePicker.show(getFragmentManager(), "Promo Time");
                        }
                    });
                    to = promoMenu.findViewById(R.id.d_addPromo_to);
                    to.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            promoTime = 2;
                            Bundle promo = new Bundle();
                            promo.putInt("picker_id", 2);
                            DialogFragment timePicker = new PromoTimeFragment();
                            timePicker.show(getFragmentManager(), "Promo Time");
                        }
                    });*/
                } else if (position == 0) {
                    TextView toTitle = promoMenu.findViewById(R.id.d_addPromo_to_title);
                    TextView fromTitle = promoMenu.findViewById(R.id.d_addPromo_from_title);
                    TextView to = promoMenu.findViewById(R.id.d_addPromo_to);
                    TextView from = promoMenu.findViewById(R.id.d_addPromo_from);

                    toTitle.setVisibility(View.GONE);
                    fromTitle.setVisibility(View.GONE);
                    to.setVisibility(View.GONE);
                    from.setVisibility(View.GONE);
                }
                break;
            case 3: //for promo type
                type = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        promoDB.removeEventListener(promoListener);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        period = "am";
        if (hourOfDay >= 13) {
            hourOfDay -= 12;
            period = "pm";
        }

        Toast.makeText(getContext(), "IT GOT HERE", Toast.LENGTH_SHORT).show();

        switch (promoTime) {
            case 1: //time from
                promoFrom = hourOfDay + ":" + String.format("%02d", minute);
                TextView timeFrom = promoMenu.findViewById(R.id.d_addPromo_from);
                timeFrom.setText(promoFrom + " " + period);
                break;
            case 2: //time to
                promoTo = hourOfDay + ":" + String.format("%02d", minute);
                TextView timeTo = promoMenu.findViewById(R.id.d_addPromo_to);
                timeTo.setText(promoTo + " " + period);
                break;
        }
    }
}
