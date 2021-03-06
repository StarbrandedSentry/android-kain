package com.oddlid.karinderya;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oddlid.karinderya.fragments.StoreDetailsFragment;
import com.oddlid.karinderya.fragments.StoreMenuFragment;
import com.oddlid.karinderya.fragments.StorePromosFragment;
import com.oddlid.karinderya.utils.SectionsPageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ActiveStoreActivity extends AppCompatActivity implements AvailMenuAdapter.OnMenuListener, AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener, PromoAdapter.OnMenuListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private String type, time;
    String promoFrom, promoTo;
    String period;
    FusedLocationProviderClient fusedLocationProviderClient;

    //control init
    Dialog imageZoom, promoMenu;
    ImageView banner;
    TextView name;
    ProgressBar uploadProg;
    ImageView zoomed;
    ImageButton closeDialog;
    Boolean flag;
    int promoTime, promoType;
    Button storeLocation;
    private SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;

    private RecyclerView recyclerView, promoRecycler;
    private RecyclerView.Adapter recyclerAdapter, promoAdapter;
    private RecyclerView.LayoutManager layoutManager, promoLayout;

    private Uri imageUri;

    //firebase init
    StorageReference storageRef;
    DatabaseReference itemDB;
    Intent old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_store);
        storageRef = FirebaseStorage.getInstance().getReference("Stores");
        itemDB = FirebaseDatabase.getInstance().getReference("Stores");
        old = getIntent();
        Intent intent = getIntent();

        sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.a_active_store_container);
        setUpViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.a_active_store_tab);
        tabLayout.setupWithViewPager(viewPager);

        //IF ACTIVITY WAS OPENED BY OWNER
        if (intent.getExtras().getBoolean("byOwner")) {
            //add to show
            /*Button addAvailable = findViewById(R.id.a_activeStore_addAvailable_btn);
            addAvailable.setVisibility(View.VISIBLE);*/
            Button changeBanner = findViewById(R.id.a_activeStore_changeBanner_btn);
            changeBanner.setVisibility(View.VISIBLE);
            Button cancelChange = findViewById(R.id.a_activeStore_cancelChangeBanner_btn);
            cancelChange.setVisibility(View.VISIBLE);
            Button chooseImage = findViewById(R.id.a_activeStore_choosePhoto_btn);
            chooseImage.setVisibility(View.VISIBLE);
            /*Button createPromo = findViewById(R.id.a_activeStore_createPromo_btn);
            createPromo.setVisibility(View.VISIBLE);
            storeLocation = findViewById(R.id.a_activeStore_map_location);
            storeLocation.setVisibility(View.VISIBLE);*/
        }
        banner = findViewById(R.id.a_activeStore_banner);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        initBanner();
        //initInfo();
        //initAvailMenu();
        //initPromo();
    }

    private void initBanner()
    {
        DatabaseReference bannerDb = FirebaseDatabase.getInstance().getReference("Stores").child(old.getStringExtra("id"));
        bannerDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.child("banner").getValue().toString())
                        .fit().centerCrop()
                        .into(banner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initPromo() {
        /*final String id = getIntent().getStringExtra("id");
        DatabaseReference promoDB = FirebaseDatabase.getInstance().getReference("Promos");
        promoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> store_ids = new ArrayList<>();
                ArrayList<String> stores = new ArrayList<>();
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> descriptions = new ArrayList<>();
                ArrayList<String> types = new ArrayList<>();
                ArrayList<String> times = new ArrayList<>();
                ArrayList<String> time_frames = new ArrayList<>();
                ArrayList<String> promo_ids = new ArrayList<>();
                boolean key = getIntent().getExtras().getBoolean("byOwner");
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
                promoRecycler = findViewById(R.id.a_activeStore_promo_view);
                promoRecycler.setHasFixedSize(true);
                promoLayout = new LinearLayoutManager(getApplicationContext());
                promoAdapter = new PromoAdapter(promo_ids, store_ids, stores, names, descriptions, types, times, time_frames,
                        ActiveStoreActivity.this, id, key);

                promoRecycler.setLayoutManager(promoLayout);
                promoRecycler.setAdapter(promoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    private void initInfo() {
        /*String id = getIntent().getStringExtra("id");
        DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(id);
        storeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String bannerUrl = dataSnapshot.child("banner").getValue().toString();
                location = findViewById(R.id.a_activeStore_location_text);
                name = findViewById(R.id.a_activeStore_name_text);
                name.setText(dataSnapshot.child("name").getValue().toString());
                location.setText(dataSnapshot.child("location").getValue().toString());
                if (dataSnapshot.child("geo_location").child("latitude").exists() && old.getExtras().getBoolean("byOwner")) {
                    String lat = dataSnapshot.child("geo_location").child("latitude").getValue().toString();
                    String longi = dataSnapshot.child("geo_location").child("longitude").getValue().toString();
                    storeLocation.setText(lat + ", " + longi);
                }
                if (dataSnapshot.child("banner").exists()) {
                    banner = findViewById(R.id.a_activeStore_banner);
                    Picasso.get()
                            .load(dataSnapshot.child("banner").getValue().toString())
                            .into(banner);
                    banner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageZoom = new Dialog(ActiveStoreActivity.this);
                            imageZoom.setContentView(R.layout.dialog_image_view);

                            closeDialog = imageZoom.findViewById(R.id.d_image_close_btn);
                            closeDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    imageZoom.dismiss();
                                }
                            });
                            zoomed = imageZoom.findViewById(R.id.d_addPromo_image);
                            Picasso.get()
                                    .load(bannerUrl)
                                    .into(zoomed);

                            imageZoom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            imageZoom.show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    private void initAvailMenu() {
        /*String id = getIntent().getStringExtra("id");
        DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(id)
                .child("menu");
        storeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("availability").getValue().equals("available")) {
                        names.add(data.child("name").getValue(String.class));
                        images.add(data.child("image_url").getValue(String.class));

                        boolean key = getIntent().getExtras().getBoolean("byOwner");

                        recyclerView = findViewById(R.id.a_actStore_availMenu);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new GridLayoutManager(ActiveStoreActivity.this, 2);
                        recyclerAdapter = new AvailMenuAdapter(images, names, ActiveStoreActivity.this, key, data.getKey(), data.child("image_url").getValue(String.class));

                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(recyclerAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    public void uploadImage(View view) {
        if (imageUri == null) {
            Snackbar.make(findViewById(android.R.id.content), "No picture picked!", Snackbar.LENGTH_LONG).show();
            return;
        }
        uploadProg = findViewById(R.id.a_activeStore_banner_pb);
        uploadProg.setVisibility(View.VISIBLE);

        final String id = getIntent().getStringExtra("id");
        final StorageReference bannerRef = storageRef.child(id).child("banner");
        bannerRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                bannerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference bannerDB = FirebaseDatabase.getInstance().getReference("Stores").child(id).child("banner");
                        bannerDB.setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                uploadProg.setVisibility(View.GONE);

                                Toast.makeText(ActiveStoreActivity.this, "Banner changed!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }
        });
    }

    public void createPromo(View view) {
        final String id = getIntent().getStringExtra("id");
        promoMenu = new Dialog(this);
        promoMenu.setContentView(R.layout.dialog_add_promo);

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
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.promo_type, R.layout.flat_spinner);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        promoType.setAdapter(typeAdapter);
        promoType.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.promo_time, R.layout.flat_spinner);
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
                timePicker.show(getSupportFragmentManager(), "Promo Time");
            }
        });
        TextView timeTo = promoMenu.findViewById(R.id.d_addPromo_to);
        timeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoTime = 2;
                Bundle promo = new Bundle();
                promo.putInt("picker_id", 1);
                DialogFragment timePicker = new PromoTimeFragment();
                timePicker.show(getSupportFragmentManager(), "Promo Time");
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

                                Toast.makeText(ActiveStoreActivity.this, "Promo created!", Toast.LENGTH_SHORT).show();
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

    public void addAvailableItem(View view) {
        String id = getIntent().getStringExtra("id");
        Intent intent = new Intent(ActiveStoreActivity.this, AddItemActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get()
                    .load(imageUri)
                    .into(banner);
        }
    }

    @Override
    public void onMenuClick(int position, String url) {
        imageZoom = new Dialog(this);
        imageZoom.setContentView(R.layout.dialog_image_view);

        closeDialog = imageZoom.findViewById(R.id.d_image_close_btn);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageZoom.dismiss();
            }
        });
        zoomed = imageZoom.findViewById(R.id.d_addPromo_image);
        Picasso.get()
                .load(url)
                .into(zoomed);

        imageZoom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        imageZoom.show();
    }

    @Override
    public void onRemove(int position, ArrayList<String> promo_ids) {
        DatabaseReference promoDB = FirebaseDatabase.getInstance().getReference().child("Promos").child(promo_ids.get(position));
        promoDB.removeValue();
    }

    @Override
    public void onChangeName(int position, ArrayList<String> promo_ids) {

    }


    //Override
    public void onUnavailableClick(int position, String id) {
        String itemID = getIntent().getStringExtra("id");
        itemDB = itemDB.child(itemID).child("menu").child(id);

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("availability", "unavailable");
        itemDB.updateChildren(statusMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                initAvailMenu();
            }
        });
    }

    //@Override
    public void onDeleteClick(int position, String id) {
        String itemID = getIntent().getStringExtra("id");
        itemDB = itemDB.child(itemID).child("menu").child(id);

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("availability", "removed");
        itemDB.updateChildren(statusMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                initAvailMenu();
            }
        });
    }


    //@Override
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

    //@Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //@Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        period = "am";
        if (hourOfDay >= 13) {
            hourOfDay -= 12;
            period = "pm";
        }

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

    //creating random stuffs
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

    public void setStoreMap(View view) {
        final String id = getIntent().getStringExtra("id");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                //Toast.makeText(getApplicationContext(), geoPoint.toString(), Toast.LENGTH_LONG).show();
                DatabaseReference storeDb = FirebaseDatabase.getInstance().getReference("Stores").child(id).child("geo_location");
                storeDb.setValue(geoPoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        storeLocation.setText(geoPoint.getLatitude() + ", " + geoPoint.getLongitude());
                    }
                });
            }
        });
    }

    public void cancelUploadImage(View view)
    {
        /*DatabaseReference storeDB = FirebaseDatabase.getInstance().getReference("Stores").child(id);
        storeDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        initBanner();
        imageUri = null;
    }

    //setting up tabbing system
    private void setUpViewPager(ViewPager viewPager)
    {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new StoreDetailsFragment(), "Details", old.getExtras().getBoolean("byOwner"), old.getStringExtra("id"));
        adapter.addFragment(new StoreMenuFragment(), "Menu", old.getExtras().getBoolean("byOwner"), old.getStringExtra("id"));
        adapter.addFragment(new StorePromosFragment(), "Promos", old.getExtras().getBoolean("byOwner"), old.getStringExtra("id"));
        viewPager.setAdapter(adapter);
    }
}
