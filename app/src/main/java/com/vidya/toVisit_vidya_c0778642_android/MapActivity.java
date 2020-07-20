package com.vidya.toVisit_vidya_c0778642_android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.vidya.toVisit_vidya_c0778642_android.networking.volley.GetByVolley;
import com.vidya.toVisit_vidya_c0778642_android.networking.volley.VolleySingleton;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

public
class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = "MapActivity";
    private static final int RADIUS = 1500;
    private final float DEFAULT_ZOOM = 15;
    String placeType;
    Button btnfindPlaces;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFind;
    private Marker userMarker;
    private Spinner mSpinner;
    private Spinner mPlacesSpinner;


    private
    void showNearbyPlaces(String url) {
        /*By Volley Library*/
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                                                    url,
                                                                    null,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public
                                                                        void onResponse(JSONObject response) {
                                                                            GetByVolley.getNearbyPlaces(response, mMap);
                                                                        }
                                                                    }, new Response.ErrorListener() {
            @Override
            public
            void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private
    String getPlaceUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append(("&radius=" + RADIUS));
        googlePlaceUrl.append("&type=" + placeType);
        googlePlaceUrl.append("&key=" + getString(R.string.google_maps_api));
        Log.d(TAG, "getDirectionUrl: " + googlePlaceUrl);
        return googlePlaceUrl.toString();
    }

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        materialSearchBar = findViewById(R.id.searchBar);
        btnFind           = findViewById(R.id.btn_find);
        btnfindPlaces = findViewById(R.id.btn_findPlaces);

        mSpinner          = (Spinner) findViewById(R.id.layers_spinner);
        mPlacesSpinner    = (Spinner) findViewById(R.id.places_spinner);
        setupSpinners();
        findPlaces();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        Places.initialize(MapActivity.this, getString(R.string.google_maps_api));
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public
            void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public
            void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public
            void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //opening or closing a navigation drawer
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.disableSearch();
                }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public
            void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public
            void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public
                    void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                for (int i = 0; i < predictionList.size(); i++) {
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionsList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                if (!materialSearchBar.isSuggestionsVisible()) {
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Log.i("mytag", "prediction fetching task unsuccessful");
                        }
                    }
                });
            }

            @Override
            public
            void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public
            void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String                 suggestion         = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public
                    void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                final String      placeId     = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public
                    void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i("mytag", "Place found: " + place.getName());
                        LatLng latLngOfPlace = place.getLatLng();
                        if (latLngOfPlace != null) {

                            markOnMap(latLngOfPlace);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, DEFAULT_ZOOM));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public
                    void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Log.i("mytag", "place not found: " + e.getMessage());
                            Log.i("mytag", "status code: " + statusCode);
                        }
                    }
                });
            }

            @Override
            public
            void OnItemDeleteListener(int position, View v) {

            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {

//                LatLng currentMarkerLocation = mMap.getCameraPosition().target;
//                rippleBg.startRippleAnimation();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        rippleBg.stopRippleAnimation();
//                        startActivity(new Intent(MapActivity.this, MapActivity.class));
//                        finish();
//                    }
//                }, 3000);
            }
        });
    }

    private
    void setupSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this, R.array.places_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPlacesSpinner.setAdapter(adapter1);
        mPlacesSpinner.setOnItemSelectedListener(this);
    }

    private
    void updateMapType() {
        if (mMap == null) {
            return;
        }

        String layerName = ((String) mSpinner.getSelectedItem());
        if (layerName.equals(getString(R.string.normal))) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.hybrid))) {
            mMap.setMapType(MAP_TYPE_HYBRID);


        } else if (layerName.equals(getString(R.string.satellite))) {
            mMap.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.terrain))) {
            mMap.setMapType(MAP_TYPE_TERRAIN);
        } else if (layerName.equals(getString(R.string.none_map))) {
            mMap.setMapType(MAP_TYPE_NONE);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }
private void findPlaces(){
    btnfindPlaces.setOnClickListener(new View.OnClickListener(){
        @Override
        public
        void onClick(View v) {
            String layerName = ((String) mPlacesSpinner.getSelectedItem());
            if (layerName.equals(getString(R.string.atm))) {
                placeType = "atm";
            } else if (layerName.equals(getString(R.string.bank))) {
                placeType = "bank";
            } else if (layerName.equals(getString(R.string.movie))) {
                placeType = "movie_theater";
            } else if (layerName.equals(getString(R.string.hospital))) {
                placeType = "hospital";
            } else if (layerName.equals(getString(R.string.restaurant))) {
                placeType = "restaurant";
            }
            String url = getPlaceUrl(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), placeType);
            showNearbyPlaces(url);
        }
    });
}
    private
    void markOnMap(LatLng latLng) {
        if (userMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            String        address       = getAddress(latLng);
            userMarker = mMap.addMarker(
                    markerOptions
                            .title(address));
            userMarker.setTag("UserMarker");
            userMarker.setDraggable(true);
            userMarker.showInfoWindow();
//            showFavoriteOption();
        }
    }

    private
    String getAddress(LatLng latLng) {
        Geocoder      geocoder;
        String        address = null;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address   = addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (address == null || address.equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            address = sdf.format(new Date());
        }
        return address;

    }

    @SuppressLint("MissingPermission")
    @Override
    public
    void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View                        locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams   = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient                 settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task           = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public
            void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
            @Override
            public
            void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public
            boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchEnabled())
                    materialSearchBar.disableSearch();
                return false;
            }
        });
    }

    @Override
    protected
    void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private
    void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public
                    void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public
                                    void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(MapActivity.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public
    void onMapLongClick(LatLng latLng) {
        markOnMap(latLng);
    }

    @Override
    public
    boolean onMarkerClick(Marker marker) {
        Log.i(TAG, "onMarkerClick: markerPosition " + marker.getPosition());

        if (marker.equals(userMarker)) {
            userMarker = null;
            mMap.clear();
            return true;
        }
        userMarker = marker;
        return false;
    }

    @Override
    public
    void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        updateMapType();
    }

    @Override
    public
    void onNothingSelected(AdapterView<?> adapterView) {
    }

}
