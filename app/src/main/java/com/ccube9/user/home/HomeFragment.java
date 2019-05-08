package com.ccube9.user.home;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ccube9.user.Modules.DirectionFinder;
import com.ccube9.user.Modules.DirectionFinderListener;
import com.ccube9.user.Modules.Route;
import com.ccube9.user.R;
import com.ccube9.user.network.BaseUrl;
import com.ccube9.user.util.CustomUtil;
import com.ccube9.user.util.PrefManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.Places;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements
        OnMapReadyCallback, DirectionFinderListener {
    private  TextView et_picloc, et_droploc,tv_estimateprice;
    private Button btn_connect;
    private GoogleApiClient googleApiClient;
    private LatLng piclatlng, droplatlng;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private Boolean picStatus = false, dropStatus = false;
    private FusedLocationProviderClient fusedLocationClient;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private Place placedrop, placepick;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Button btn_confirmbooking;
    private LinearLayout frm_lay_estimate,fra_lay_confirm_booking;


    private GoogleMap mMap;
    View v;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());

        googleApiClient = new GoogleApiClient.Builder(getActivity())

                .addApi(LocationServices.API).build();

        googleApiClient.connect();

        GPSenableDialog();


        initView(v);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize Places.
        Places.initialize(getActivity(), getResources().getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(getActivity());

        // getChildFragmentManager().beginTransaction().replace(R.id.frm_lay_estimate,new CalEstimateFragment()).commit();

        // Add an import statement for the client library.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (checkPermission() == false) {
            requestPermission();
        } else {


            getLastKnownLocation();
            getUpdatedLocation();




            if (fusedLocationClient != null) {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                        locationCallback,
                        null /* Looper */);
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }

        }

        onClick();


        return v;
    }//onCreateClose


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    //init view start

    public void initView(View view) {
        et_picloc = view.findViewById(R.id.pic_loc);
        et_droploc = view.findViewById(R.id.drop_loc);
        btn_connect = view.findViewById(R.id.btn_connect);
        frm_lay_estimate= view.findViewById(R.id.frm_lay_estimate);
        fra_lay_confirm_booking= view.findViewById(R.id.fra_lay_confirm_booking);
        tv_estimateprice= view.findViewById(R.id.tv_estimateprice);
        btn_confirmbooking= view.findViewById(R.id.btn_confirmbooking);

    }//initViewClose


    //onClick start

    public void onClick() {




        et_picloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Places.isInitialized()) {
                    Places.initialize(getActivity(),  getResources().getString(R.string.google_maps_key));
                    PlacesClient placesClient = Places.createClient(getActivity());
                }
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.REGIONS)
                        .build(getActivity());
                startActivityForResult(intent, 1);
                Log.d("sda", String.valueOf(fields));


            }
        });


        et_droploc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Places.isInitialized()) {
                    Places.initialize(getActivity(),  getResources().getString(R.string.google_maps_key));
                    PlacesClient placesClient = Places.createClient(getActivity());
                }
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.REGIONS)
                        .build(getActivity());
                startActivityForResult(intent, 2);
                Log.d("sda", String.valueOf(fields));
            }
        });


        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (picStatus == false && dropStatus == false) {
                    Toast.makeText(getActivity(), "Please select Pickup and drop Location ", Toast.LENGTH_SHORT).show();
                } else if (picStatus == false) {
                    Toast.makeText(getActivity(), "Please select Pickup Location ", Toast.LENGTH_SHORT).show();
                } else if (dropStatus == false) {
                    Toast.makeText(getActivity(), "Please select Drop Location ", Toast.LENGTH_SHORT).show();
                } else {
                    CustomUtil.ShowDialog(getActivity());
                    stringRequest = new StringRequest(Request.Method.POST, BaseUrl.BASE_URL.concat("user/estimate_price"), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            CustomUtil.DismissDialog(getActivity());
                            Log.d("fghfghgf", response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("status").equals("1")) {
                                    frm_lay_estimate.setVisibility(View.GONE);
                                    fra_lay_confirm_booking.setVisibility(View.VISIBLE);
                                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
                                    tv_estimateprice.setText(jsonObject1.getString("price"));


                                } else if (jsonObject.getString("status").equals("0")) {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            CustomUtil.DismissDialog(getActivity());
                            requestQueue.cancelAll(stringRequest);
                            String message = null;
                            if (volleyError instanceof NetworkError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (volleyError instanceof ServerError) {
                                message = "The server could not be found. Please try again after some time!!";
                            } else if (volleyError instanceof AuthFailureError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (volleyError instanceof ParseError) {
                                message = "Parsing error! Please try again after some time!!";
                            } else if (volleyError instanceof NoConnectionError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (volleyError instanceof TimeoutError) {
                                message = "Connection TimeOut! Please check your internet connection.";
                            }
                            if (message != null) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getActivity(), "An error occured", Toast.LENGTH_SHORT).show();


                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> param = new HashMap<>();

                            param.put("language", "en");
                            param.put("user_type", "1");
                            param.put("start_lat", String.valueOf(piclatlng.latitude));
                            param.put("start_lon", String.valueOf(piclatlng.longitude));
                            param.put("end_lat", String.valueOf(droplatlng.latitude));
                            param.put("end_lon", String.valueOf(droplatlng.longitude));
                            param.put("user_id", PrefManager.getUserId(getActivity()));
                            param.put("api_token", PrefManager.getApiToken(getActivity()));
                            return param;
                        }
                    };

                    requestQueue.add(stringRequest);
                }
            }
        });




        btn_confirmbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if (picStatus == false && dropStatus == false) {
                    Toast.makeText(getActivity(), "Please select Pickup and drop Location ", Toast.LENGTH_SHORT).show();
                } else if (picStatus == false) {
                    Toast.makeText(getActivity(), "Please select Pickup Location ", Toast.LENGTH_SHORT).show();
                } else if (dropStatus == false) {
                    Toast.makeText(getActivity(), "Please select Drop Location ", Toast.LENGTH_SHORT).show();
                } else {
                    CustomUtil.ShowDialog(getActivity());
                    stringRequest = new StringRequest(Request.Method.POST, BaseUrl.BASE_URL.concat("user/booking_order"), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            CustomUtil.DismissDialog(getActivity());
                            Log.d("fghfghgf", response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("status").equals("1")) {

                                    Toast.makeText(getActivity(), "Booking Confirmed", Toast.LENGTH_SHORT).show();
                                    frm_lay_estimate.setVisibility(View.GONE);
                                    fra_lay_confirm_booking.setVisibility(View.GONE);

                                } else if (jsonObject.getString("status").equals("0")) {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            CustomUtil.DismissDialog(getActivity());
                            requestQueue.cancelAll(stringRequest);

                            Log.d("fghfghgf", volleyError.toString());
                            String message = null;
                            if (volleyError instanceof NetworkError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (volleyError instanceof ServerError) {
                                message = "The server could not be found. Please try again after some time!!";
                            } else if (volleyError instanceof AuthFailureError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (volleyError instanceof ParseError) {
                                message = "Parsing error! Please try again after some time!!";
                            } else if (volleyError instanceof NoConnectionError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (volleyError instanceof TimeoutError) {
                                message = "Connection TimeOut! Please check your internet connection.";
                            }
                            if (message != null) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getActivity(), "An error occured", Toast.LENGTH_SHORT).show();


                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> param = new HashMap<>();

                            param.put("language", "en");
                            param.put("user_type", "1");
                            param.put("user_id", PrefManager.getUserId(getActivity()));
                            param.put("api_token", PrefManager.getApiToken(getActivity()));
                            param.put("pick_up_lat", String.valueOf(piclatlng.latitude));
                            param.put("pick_up_lon", String.valueOf(piclatlng.longitude));
                            param.put("drop_lat", String.valueOf(droplatlng.latitude));
                            param.put("drop_lon", String.valueOf(droplatlng.longitude));
                            param.put("pick_up_location", et_picloc.getText().toString());
                            param.put("drop_location", et_droploc.getText().toString());


                            return param;
                        }
                    };

                    requestQueue.add(stringRequest);


                }
            }

        });


    }

    //on click end


//onMapReady Callback

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (checkPermission() == false) {
            requestPermission();
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }


    }

    //on map ready callback end


    //onActivityresult

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {


                placepick = Autocomplete.getPlaceFromIntent(data);
                piclatlng = placepick.getLatLng();
                et_picloc.setText(placepick.getAddress());
                picStatus = true;


                frm_lay_estimate.setVisibility(View.VISIBLE);
                fra_lay_confirm_booking.setVisibility(View.GONE);

                if(!et_picloc.getText().equals("") && !et_droploc.getText().equals("")) {
                    try {
                        new DirectionFinder(this, et_picloc.getText().toString(), et_droploc.getText().toString()).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

                Log.d("fdgfdgfd", String.valueOf(status));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                Place placedrop = Autocomplete.getPlaceFromIntent(data);

                et_droploc.setText(placedrop.getAddress());
                droplatlng = placedrop.getLatLng();
                dropStatus = true;


                frm_lay_estimate.setVisibility(View.VISIBLE);
                fra_lay_confirm_booking.setVisibility(View.GONE);

                if(!et_picloc.getText().equals("") && !et_droploc.getText().equals("")) {
                    try {
                        new DirectionFinder(this, et_picloc.getText().toString(), et_droploc.getText().toString()).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d("fdgfdgfd", String.valueOf(status));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


//end of onActivityresult


    //get address from location code start

    public String getAddressFromLatLng(Location loc) {
        List<Address> addresses = null;
        String address = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(
                    loc.getLatitude(),
                    loc.getLongitude(),
                    // In this sample, get just a single address.
                    1);
            if (addresses == null || addresses.size() == 0) {

            } else {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    //gt address from location code end


    //getLast known location code start

    private void getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            location.getLatitude();
                            location.getLatitude();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraPosition cp = new CameraPosition.Builder()
                                    .target(latLng) // your initial co-ordinates here. like, LatLng initialLatLng
                                    .zoom(10)
                                    .build();
                            et_picloc.setText(getAddressFromLatLng(location));
                            picStatus = true;
                            piclatlng=latLng;
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                            Log.d("dsds", String.valueOf(location));
                        }
                    }
                });

    }
    //get last known location end


    //getupdated location code start

    private void getUpdatedLocation() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        location.getLatitude();
                        location.getLatitude();


                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraPosition cp = new CameraPosition.Builder()
                                .target(latLng) // your initial co-ordinates here. like, LatLng initialLatLng
                                .zoom(10)
                                .build();
                        et_picloc.setText(getAddressFromLatLng(location));
                        piclatlng=latLng;
                        picStatus=true;
                       // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                        Log.d("dsds", String.valueOf(location));
                    }
                }
            }
        };

    }

    //getupdated location code end

    //Gps enable dialog
    public void GPSenableDialog() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), 10);

                        } catch (IntentSender.SendIntentException e) {
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });

    }

    //gps enable dialog end







    //direction finder


    @Override
    public void onDirectionFinderStart() {

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    //direction finder end


    //onDirection finder sucess listner
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {



        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();


        for (Route route : routes) {


            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_green))
                    .title(route.startAddress)
                    .position(route.startLocation)));


            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLACK).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : route.points) {
                builder.include(point);
            }

            LatLngBounds bounds = builder.build();
            int padding =400; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                    padding);
            mMap.moveCamera(cu);
            mMap.animateCamera(cu, 2000, null);
        }
    }

    //on direction finder sucess end



    //permission methods

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE)) {
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

    private boolean checkPermission() {
        int result = (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION));
        int result2 = (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION));
        int result3 = (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE));

        if (result == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    //end of permission method
}
