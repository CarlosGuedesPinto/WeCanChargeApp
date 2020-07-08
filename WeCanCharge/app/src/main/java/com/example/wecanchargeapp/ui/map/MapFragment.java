package com.example.wecanchargeapp.ui.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.adaptersRecyclers.BusinessesMapAdapterRecycler;
import com.example.wecanchargeapp.adaptersRecyclers.ChargerAdapterRecycler;
import com.example.wecanchargeapp.classes.BusinessCharger;
import com.example.wecanchargeapp.classes.Businesses;
import com.example.wecanchargeapp.classes.Charger;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.ui.charging.chargerDetails.ChargerDetailsActivity;
import com.example.wecanchargeapp.ui.profile.settings.SettingsActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private static final int RESULT_OK = -1;
    MapView mMapView;
    private GoogleMap googleMap;
    private Marker marker;
    private ArrayList<Marker> markers = new ArrayList<Marker>();

    private ArrayList<BusinessCharger> chargers = new ArrayList<BusinessCharger>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "ASD";

    RecyclerView rvBusinesses;
    private ArrayList<Businesses> businesses;
    private BusinessesMapAdapterRecycler businessAdapter;

    private Button scanQRCodeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getAllChargers();
        listAllBusinesses();

        scanQRCodeButton = getView().findViewById(R.id.buttonScanQRCode);

        scanQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                    startActivityForResult(intent, 0);

                } catch (Exception e) {

                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);

                }
            }
        });
    }


    private void listAllBusinesses() {

        rvBusinesses = getView().findViewById(R.id.businessRV);
        rvBusinesses.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager((getActivity()), LinearLayoutManager.HORIZONTAL, false);
        rvBusinesses.setLayoutManager(llm);
        businesses = new ArrayList<Businesses>();

        db.collection("businesses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                businesses.add(document.toObject(Businesses.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        if (businesses.size() > 0) {
                            businessAdapter = new BusinessesMapAdapterRecycler(businesses, getContext());
                            rvBusinesses.setAdapter(businessAdapter);
                        }
                    }
                });
    }

    private void getAllChargers() {

            db.collection("businessChargers")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    chargers.add(document.toObject(BusinessCharger.class));
                                }
                                if (chargers.size() > 0) {

                                    for (final BusinessCharger charger : chargers) {

                                        mMapView.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap mMap) {

                                                googleMap = mMap;

                                                // For showing a move to my location button
                                                googleMap.setMyLocationEnabled(true);

                                                // For dropping a marker at a point on the Map
                                                LatLng newMarker = new LatLng(charger.getLat(), charger.getLng());
                                                getOwnerNameFromUid(charger.getOwnerUid(), newMarker, charger.getName());

                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(newMarker).zoom(7).build();
                                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                                            }
                                        });
                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


    }

    private void setUpMap() {
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker markerClicked) {
                Log.d(TAG, googleMap.getCameraPosition().toString());
                breakLoop:
                for (int i = 0; i < markers.size(); i++) {
                    if (markers.get(i).getTitle().equals(markerClicked.getTitle())) {
                        getBusinessFromBusinessName(markerClicked.getSnippet());
                        break breakLoop;
                    }
                }
            }
        });


    }

    private void getOwnerNameFromUid(String uid, final LatLng newMarkerPosition, final String chargerName) {
        setUpMap();
        db.collection("businesses")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {

                                String businessName = document.get("name").toString();
                                marker = googleMap.addMarker(new MarkerOptions().position(newMarkerPosition).title(chargerName).snippet(businessName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                markers.add(marker);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    public void getBusinessFromBusinessName(final String businessName) {

        db.collection("businesses")
                .whereEqualTo("name", businessName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                GlobalUtils.currentBusiness = document.toObject(Businesses.class);
                                Intent intent = new Intent(getActivity(), BusinessPage.class);
                                startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                searchBusinessById(contents);
            }
            if(resultCode == Activity.RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    public void searchBusinessById(String uid) {

        db.collection("businesses")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                GlobalUtils.currentBusiness = document.toObject(Businesses.class);
                                Intent intent = new Intent(getActivity(), BusinessPage.class);
                                startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
