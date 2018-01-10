package com.pointofsalesandroid.androidbasedpos_inventory;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.RestaurantLocationMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.RestaurantLocationModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsProfileUpdateActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = MapsProfileUpdateActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 14;
    private boolean mLocationPermissionGranted = false;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private UiSettings mUiSettings;
    private LatLng currnetLoc;
    TextView lblLocation;
    private Marker marker;
    private double restoLocationLatitude;
    private double restoLocationLongitude;
    private ImageView confirmLocation;
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;
    private ArrayList<RestaurantLocationModel> restaurantLocationModels = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_profile_update);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mapFragment.getMapAsync(this);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        lblLocation = (TextView) findViewById(R.id.lblLocation);
        confirmLocation = (ImageView) findViewById(R.id.confirmLocation);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

        mDataBase.child(Utils.restaurantLocation).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RestaurantLocationModel restaurantLocationModel = new RestaurantLocationModel();
                RestaurantLocationMapModel restaurantLocationMapModel = dataSnapshot.getValue(RestaurantLocationMapModel.class);
                if (restaurantLocationMapModel!=null){
                    restaurantLocationModel.setKey(restaurantLocationMapModel.key);
                    restaurantLocationModel.setRestaurantAddress(restaurantLocationMapModel.restauarantAddress);
                    restaurantLocationModel.setLocationLat(restaurantLocationMapModel.locationLatitude);
                    restaurantLocationModel.setLocationLongitude(restaurantLocationMapModel.locationLongitude);
                    restaurantLocationModels.add(restaurantLocationModel);
                    restoLocationLatitude = restaurantLocationMapModel.locationLatitude;
                    restoLocationLongitude = restaurantLocationMapModel.locationLongitude;
                    setLocation();
                }else {
                    getDeviceLocation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addressLocation = lblLocation.getText().toString();
                double latitude = restoLocationLatitude;
                double longitude = restoLocationLongitude;
                RestaurantLocationMapModel restaurantLocationMapModel = new RestaurantLocationMapModel(mAuth.getUid(),addressLocation,latitude,longitude);
                Map<String,Object> restLocVal = restaurantLocationMapModel.toMap();
                Map<String,Object> childUpdate = new HashMap<>();
                childUpdate.put(mAuth.getUid(),restLocVal);
                mDataBase.child(Utils.restaurantLocation).updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
      /*  LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        updateLocationUI();

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub


            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                currnetLoc = new LatLng(arg0.getPosition().latitude,arg0.getPosition().longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));

                try {
                    Geocoder geo = new Geocoder(MapsProfileUpdateActivity.this, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(arg0.getPosition().latitude, arg0.getPosition().longitude, 1);
                    if (addresses.isEmpty()) {
                        Log.i("System out", "onMarkerDragEnd... waiting for location name"+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                    }
                    else {
                        if (addresses.size() > 0) {
                           for (int i = 0;i<addresses.size();i++){
                             /*  Log.i("Location",addresses.get(i).getAddressLine(0)+", "+addresses.get(i).getFeatureName() + ", " + addresses.get(i).getLocality() +", " + addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getFeatureName() + ", " + addresses.get(i).getCountryName());*/
                               Log.i("Location",addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                lblLocation.setText(addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                restoLocationLatitude = arg0.getPosition().latitude;
                                restoLocationLongitude = arg0.getPosition().longitude;
                           }
                        }
                    }
                }catch (IOException e){
                    Log.i("System out error", e.toString());
                    Utils.toster(MapsProfileUpdateActivity.this,"Please Check Internet Connection");
                }
            }
            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub


            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location"));
                marker.setDraggable(true);
                try {

                    Geocoder geo = new Geocoder(MapsProfileUpdateActivity.this, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latLng.latitude,latLng.longitude, 1);
                    if (addresses.isEmpty()) {
                        Log.i("System out", "onMarkerDragEnd... waiting for location name"+latLng.latitude+"..."+latLng.longitude);
                    }
                    else {
                        if (addresses.size() > 0) {
                            for (int i = 0;i<addresses.size();i++){
                                Log.i("Location",addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                lblLocation.setText(addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                restoLocationLatitude = latLng.latitude;
                                restoLocationLatitude = latLng.longitude;

                            }
                        }
                    }
                }catch (IOException e){
                    Log.i("System out error", e.toString());
                    Utils.toster(MapsProfileUpdateActivity.this,"Please Check Internet Connection");

                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()) {
                                    // Set the map's camera position to the current location of the device.
                                    mLastKnownLocation = task.getResult();
                                    LatLng latLng = null;
                                    if (mLastKnownLocation!=null){
                                        latLng =  new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude());
                                    }

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(mLastKnownLocation.getLatitude(),
                                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                   marker= mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location"));
                                   marker.setDraggable(true);

                                    currnetLoc = latLng;
                                    try {

                                        Geocoder geo = new Geocoder(MapsProfileUpdateActivity.this, Locale.getDefault());
                                        List<Address> addresses = geo.getFromLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1);
                                        if (addresses.isEmpty()) {
                                         //   Log.i("System out", "onMarkerDragEnd... waiting for location name"+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                                        }
                                        else {
                                            if (addresses.size() > 0) {
                                                for (int i = 0;i<addresses.size();i++){
                             /*  Log.i("Location",addresses.get(i).getAddressLine(0)+", "+addresses.get(i).getFeatureName() + ", " + addresses.get(i).getLocality() +", " + addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getFeatureName() + ", " + addresses.get(i).getCountryName());*/
                                                    Log.i("Location",addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                                    lblLocation.setText(addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());

                                                }
                                            }
                                        }
                                    }catch (IOException e){
                                        Log.i("System out error", e.toString());
                                        Utils.toster(MapsProfileUpdateActivity.this,"Please Check Internet Connection");

                                    }


                                } else {
                                    Log.d(TAG, "Current location is null. Using defaults.");
                                    Log.e(TAG, "Exception: %s", task.getException());

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                }
                               mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                   @Override
                                   public void onMapClick(LatLng latLng) {

                                   }
                               });
                            }
                        });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setLocation(){
        try {
            if (mLocationPermissionGranted) {
                LatLng latLng = null;
                    latLng =  new LatLng(restoLocationLatitude,restoLocationLongitude);


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latLng.latitude,latLng.longitude), DEFAULT_ZOOM));

                marker= mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location"));
                marker.setDraggable(true);

                currnetLoc = latLng;
                lblLocation.setText(restaurantLocationModels.get(0).getRestaurantAddress());
                try {

                    Geocoder geo = new Geocoder(MapsProfileUpdateActivity.this, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latLng.latitude,latLng.longitude, 1);
                    if (addresses.isEmpty()) {
                        //   Log.i("System out", "onMarkerDragEnd... waiting for location name"+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                    }
                    else {
                        if (addresses.size() > 0) {
                            for (int i = 0;i<addresses.size();i++){
                             /*  Log.i("Location",addresses.get(i).getAddressLine(0)+", "+addresses.get(i).getFeatureName() + ", " + addresses.get(i).getLocality() +", " + addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getFeatureName() + ", " + addresses.get(i).getCountryName());*/
                                Log.i("Location",addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                lblLocation.setText(addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());

                            }
                        }
                    }
                }catch (IOException e){
                    Utils.toster(MapsProfileUpdateActivity.this,"Please Check Internet Connection");

                    Log.i("System out error", e.toString());
                }
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


}
