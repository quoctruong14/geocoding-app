package com.example.googlemapsproject;

import android.app.DownloadManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public final static String URL_PREFIX = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    public final static String API_KEY = "&key=AIzaSyAMxkKIOejjWvHRinEFlntXQFr567IESB0";

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Searches for the address entered by the user
     */
    public void searchLocation(View view) throws IOException {
        // Hides the keyboard
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        EditText enteredLocation = (EditText) findViewById(R.id.enter_location);
        String location = enteredLocation.getText().toString();
        location = location.replace(' ', '+');
        String geocoderURL = URL_PREFIX + location + API_KEY;
        Log.d("coords", geocoderURL);

        // The Volley request code is from the following tutorial:
        // https://developer.android.com/training/volley/simple
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, geocoderURL,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse (String response){
                        try {
                            JSONObject reader = new JSONObject(response);
                            if(reader.get("status").toString().equals("OK")) {
                                JSONObject location = reader.getJSONArray("results")
                                        .getJSONObject(0).getJSONObject("geometry")
                                        .getJSONObject("location");
                                double latitude = (double) location.get("lat");
                                double longitude = (double) location.get("lng");

                                // Add a marker in the given location and move the camera
                                LatLng myLocation = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(myLocation).title("My Marker"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            }
                        }
                        catch (Exception e){
                            Log.d("coords", e.toString());
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.d("error", "FAIL");
            }
        });

        queue.add(stringRequest);

    }
}
