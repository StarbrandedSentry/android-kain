package com.oddlid.karinderya;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.oddlid.karinderya.models.ClusterMarker;
import com.oddlid.karinderya.utils.ClusterManagerRenderer;

import java.util.ArrayList;
import java.util.List;

import static com.oddlid.karinderya.utils.Constants.MAPVIEW_BUNDLE_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    MapView mapView;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LatLngBounds mapBoundary;
    private GoogleMap map;
    private GeoPoint userPosition, myLocation;
    private ClusterManager clusterManager;
    private ClusterManagerRenderer clusterManagerRenderer;
    private ArrayList<ClusterMarker> clusterMarkers;
    private ArrayList<GeoPoint> storeLocation;
    private ArrayList<StoreLocation> gmLocation;
    private ArrayList<String> storeId;
    private ArrayList<String> storeImage;
    private ArrayList<String> storeName;
    private String thisId;
    private GeoApiContext geoApiContext;
    DatabaseReference storeLocationDb;
    ValueEventListener storeLocationListener;

    FusedLocationProviderClient fClient;
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
        storeId = new ArrayList<>();
        storeLocation = new ArrayList<>();
        storeImage = new ArrayList<>();
        storeName = new ArrayList<>();
        clusterMarkers = new ArrayList<>();
        gmLocation = new ArrayList<>();
        mapView = view.findViewById(R.id.f_map_view);
        fClient = LocationServices.getFusedLocationProviderClient(getActivity());
        initGoogleMap(savedInstanceState);

        // Inflate the layout for this fragment
        return view;
    }

    private void setCameraView() {
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

                double bottomBoundary = geoPoint.getLatitude() - .02;
                double leftBoundary = geoPoint.getLongitude() - .02;
                double topBoundary = geoPoint.getLatitude() + .02;
                double rightBoundary = geoPoint.getLongitude() + .02;

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

    private void addMapMarkers() {
        if (map != null) {
            if (clusterManager == null) {
                clusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), map);
            }

            if (clusterManagerRenderer == null) {
                clusterManagerRenderer = new ClusterManagerRenderer(getActivity(), map, clusterManager);
                clusterManager.setRenderer(clusterManagerRenderer);
            }

            /*for(int i = 0; i < storeId.size(); i++)
            {
                try
                {
                    String snippet = "Determine route to: " + storeName.get(i);
                    String avatar = storeImage.get(i);

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(storeLocation.get(i).getLatitude(), storeLocation.get(i).getLongitude()),
                            storeImage.get(i),
                            snippet,
                            storeName.get(i)
                    );

                    clusterManager.addItem(newClusterMarker);
                    clusterMarkers.add(newClusterMarker);
                }
                catch(NullPointerException e)
                {
                    Toast.makeText(getContext(), "Whoops!: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }*/
            for (StoreLocation store : gmLocation) {
                //Toast.makeText(getContext(), "gfasges", Toast.LENGTH_SHORT).show();
                try {

                    String snippet = store.getName();
                    String avatar = store.getImage();

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(store.getGeo_location().getLatitude(), store.getGeo_location().getLongitude()),
                            avatar,
                            snippet,
                            store.getName(),
                            store.getId()
                    );
                    //Toast.makeText(getContext(), "NOT NULL - " + store.getGeo_location().getLatitude(), Toast.LENGTH_SHORT).show();
                    clusterManager.addItem(newClusterMarker);
                    clusterMarkers.add(newClusterMarker);
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), "Whoops!: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            clusterManager.cluster();
            setCameraView();

            map.setOnInfoWindowClickListener(this);
        }
    }

    private void getStoreLocations() {
        storeLocationDb = FirebaseDatabase.getInstance().getReference("Stores");
        storeLocationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //int i = 0;
                    if (data.child("geo_location").exists()) {
                        latitude.add(data.child("geo_location").child("latitude").getValue().toString());
                        longitude.add(data.child("geo_location").child("longitude").getValue().toString());
                        GeoPoint thisLocation = new GeoPoint(Double.parseDouble(latitude.get(latitude.size() - 1)), Double.parseDouble(longitude.get(longitude.size() - 1)));
                        storeLocation.add(thisLocation);
                        storeId.add(data.getKey());
                        storeName.add(data.child("name").getValue().toString());
                        storeImage.add(data.child("banner").getValue().toString());
                        StoreLocation thisSL = new StoreLocation(
                                data.getKey(),
                                thisLocation,
                                data.child("name").getValue().toString(),
                                data.child("banner").getValue().toString()
                        );
                        gmLocation.add(thisSL);

                        //Toast.makeText(getContext(), gmLocation.size() + " - " + gmLocation.size(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getContext(), storeLocation.size() + " - " + storeLocation.get(latitude.size() - 1).getLatitude(), Toast.LENGTH_SHORT).show();
                    }
                    //i = i + 1;
                }
                addMapMarkers();
                //Toast.makeText(getContext(), longitude.get(0), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        storeLocationDb.addValueEventListener(storeLocationListener);
    }


    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_map_api_key))
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);
        map = googleMap;
        getStoreLocations();
        //setCameraView();
        //addMapMarkers();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        getStoreLocations();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        getStoreLocations();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        storeLocationDb.removeEventListener(storeLocationListener);
    }


    @Override
    public void onInfoWindowClick(final Marker marker) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Select option! ")
                .setPositiveButton("Locate this karinderya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        calculateDirections(marker);
                    }
                }).setNegativeButton("Look at karinderya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                for (StoreLocation store : gmLocation) {
                    if (store.getName().equals(marker.getTitle())) {
                        thisId = store.getId();
                    }
                }

                Intent intent = new Intent(getActivity(), ActiveStoreActivity.class);
                intent.putExtra("id", thisId);
                intent.putExtra("byOwner", false);
                startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void calculateDirections(final Marker marker) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                myLocation = new GeoPoint(location.getLatitude(), location.getLongitude());


                com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                        marker.getPosition().latitude,
                        marker.getPosition().longitude
                );

                DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
                directions.alternatives(true);
                directions.origin(
                        new com.google.maps.model.LatLng(
                                myLocation.getLatitude(),
                                myLocation.getLongitude()
                        )
                );

                directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        //add polylines
                        addPolyLinesToMap(result);
                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                });
            }
        });

        //calculations
        /*com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        myLocation.getLatitude(),
                        myLocation.getLongitude()
                )
        );

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {

            }

            @Override
            public void onFailure(Throwable e) {

            }
        });*/
    }

    private void addPolyLinesToMap(final DirectionsResult result)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                for(DirectionsRoute route: result.routes)
                {
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();

                    for(com.google.maps.model.LatLng latLng : decodedPath)
                    {
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    polyline.setClickable(true);
                }
            }
        });
    }
}
