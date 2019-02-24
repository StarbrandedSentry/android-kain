package com.oddlid.karinderya;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.clustering.ClusterManager;
import com.oddlid.karinderya.models.ClusterMarker;
import com.oddlid.karinderya.utils.ClusterManagerRenderer;

import java.util.ArrayList;

import static com.oddlid.karinderya.utils.Constants.MAPVIEW_BUNDLE_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LatLngBounds mapBoundary;
    private GoogleMap map;
    private GeoPoint userPosition;
    private ClusterManager clusterManager;
    private ClusterManagerRenderer clusterManagerRenderer;
    private ArrayList<ClusterMarker> clusterMarkers = new ArrayList<>();

    ArrayList<GeoPoint> userP;
    ArrayList<String> latitude;
    ArrayList<String> longitude;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        userP = new ArrayList<>();
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        mapView = view.findViewById(R.id.f_map_view);
        initGoogleMap(savedInstanceState);
        getStoreLocations();

        // Inflate the layout for this fragment
        return view;
    }

    private void setCameraView()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //fusedLocationProviderClient.getLastLocation().toString();
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                double bottomBoundary = geoPoint.getLatitude() - .01;
                double leftBoundary = geoPoint.getLongitude() - .01;
                double topBoundary = geoPoint.getLatitude() + .01;
                double rightBoundary = geoPoint.getLongitude() + .01;

                mapBoundary = new LatLngBounds(
                        new LatLng(bottomBoundary, leftBoundary),
                        new LatLng(topBoundary, rightBoundary)
                );

                map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundary, 0));
            }
        });
        /*//map view .2 * .2 = .4
        double bottomBoundary = userP.get(0).getLatitude() - .1;
        double leftBoundary = userP.get(0).getLongitude() - .1;
        double topBoundary = userP.get(0).getLatitude() + .1;
        double rightBoundary = userP.get(0).getLongitude() + .1;

        mapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundary, 0));*/
    }

    private void addMapMarkers()
    {
        if(map != null)
        {
            if(clusterManager == null)
            {
                clusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), map);
            }

            if(clusterManagerRenderer == null)
            {
                clusterManagerRenderer = new ClusterManagerRenderer(getActivity(), map, clusterManager);
            }

            DatabaseReference storeDb = FirebaseDatabase.getInstance().getReference("Stores");
            storeDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren())
                    {
                        String snippet = "";
                        LatLng lng = null;
                        if(data.child("geo_location").exists())
                        {
                            snippet = data.child("location").getValue(String.class);
                            lng =  new LatLng(data.child("geo_location").child("latitude").getValue(double.class), data.child("geo_location").child("longitude").getValue(double.class));
                        }
                        int avatar = R.drawable.empty_image;
                        String sName = data.child("name").getValue(String.class);

                        ClusterMarker clusterMarker = new ClusterMarker(lng, avatar, snippet, sName);
                    }

                    clusterManager.cluster();
                    setCameraView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void getStoreLocations()
    {
        DatabaseReference storeDb = FirebaseDatabase.getInstance().getReference("Stores");
        storeDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    if(data.child("geo_location").exists())
                    {
                        latitude.add(data.child("geo_location").child("latitude").getValue().toString());
                        longitude.add(data.child("geo_location").child("longitude").getValue().toString());
                    }

                }
                //Toast.makeText(getContext(), longitude.get(0), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        map = googleMap;
        //setCameraView();
        addMapMarkers();
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }


}
