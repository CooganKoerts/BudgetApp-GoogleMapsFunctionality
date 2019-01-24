package com.example.coogan.googlemapsfunctionality;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        Location location = googleMap.getMyLocation();
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        // Add a marker in Sydney and move the camera
        /** LatLng ksuLibrary = new LatLng(33.939167526582196, -84.520017204843882);
        mMap.addMarker(new MarkerOptions().position(ksuLibrary).title("Marker at the Library on the Marietta KSU campus"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ksuLibrary)); */
    }

    public void onClick(View view){
        switch(view.getId()) {
            case R.id.returnBT:
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", mMap.getMyLocation().getLatitude());
                bundle.putDouble("longitude", mMap.getMyLocation().getLongitude());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
