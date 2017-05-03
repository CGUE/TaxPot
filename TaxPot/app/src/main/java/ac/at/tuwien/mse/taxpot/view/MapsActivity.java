package ac.at.tuwien.mse.taxpot.view;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.fragments.DetailsFragment;
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.service.MarkerDetailService;
import ac.at.tuwien.mse.taxpot.service.SearchService;
import ac.at.tuwien.mse.taxpot.utli.GoogleClient;
import ac.at.tuwien.mse.taxpot.utli.PermissionUtils;
import ac.at.tuwien.mse.taxpot.view.CustomCluster.CustomClusterRenderer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
                                                            GoogleApiClient.OnConnectionFailedListener,
                                                            GoogleApiClient.ConnectionCallbacks,
                                                            OnMyLocationButtonClickListener,
                                                            ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = "TaxPot";
    private GoogleApiClient googleApiClient;

    private boolean hasPermissions = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private FloatingSearchView searchBar;

    private ClusterManager<TaxPot> taxPotClusterManager;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // establish connection with google services
        googleApiClient = new GoogleApiClient.Builder(this)
                                .addApi(Places.GEO_DATA_API)
                                .addApi(Places.PLACE_DETECTION_API)
                                .addApi(LocationServices.API)
                                .enableAutoManage(this, this)
                                .addConnectionCallbacks(this)
                                .build();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View locationButton = ((View) mapFragment.getView().findViewById(1).getParent()).findViewById(2);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

        searchBar = (FloatingSearchView)findViewById(R.id.floating_search_view);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // activate my location button
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        // set camera perpective
        mMap.setMinZoomPreference(10.f);
        mMap.setMaxZoomPreference(20.f);

        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.f));

        SearchService service = new SearchService(googleApiClient, mMap, this);
        searchBar.setOnQueryChangeListener(service);
        searchBar.setOnSearchListener(service);

        // initialize clustermanager
        taxPotClusterManager = new ClusterManager<TaxPot>(this, mMap);

        // fill map with TaxPots
        String datagv_url = getResources().getString(R.string.datagv_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, datagv_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            parseDatagvResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.on_request_fail_message+": "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        GoogleClient.getInstance(getApplicationContext()).addToRequestQueue(request);

        taxPotClusterManager.setOnClusterItemClickListener(new MarkerDetailService(this, googleApiClient));
        taxPotClusterManager.setRenderer(new CustomClusterRenderer(getApplicationContext(), mMap, taxPotClusterManager));

        mMap.setOnMarkerClickListener(taxPotClusterManager);
        mMap.setOnCameraIdleListener(taxPotClusterManager);


    }

    private void parseDatagvResponse(JSONObject json) throws JSONException {
        JSONArray features = json.getJSONArray("features");

        for(int i = 0; i < features.length(); i++) {
            JSONObject feature = (JSONObject) features.get(i);
            JSONObject properties = feature.getJSONObject("properties");

            JSONObject geometryInfo = feature.getJSONObject("geometry");
            JSONArray coordinates = geometryInfo.getJSONArray("coordinates");
            JSONArray latLngInfo = coordinates.getJSONArray(0);

            TaxPot spot = new TaxPot();
            if (properties.get("ADRESSE") != null)
                spot.setAddress(properties.getString("ADRESSE"));

            if (latLngInfo != null)
                spot.setLatLng(new LatLng(latLngInfo.getDouble(1), latLngInfo.getDouble(0)));

            if (properties.get("ZEITRAUM") != null)
                spot.setServiceTime(properties.getString("ZEITRAUM"));

            if (properties.get("STELLPLATZANZAHL") != null)
                spot.setParkingSpace(properties.getString("STELLPLATZANZAHL"));

            taxPotClusterManager.addItem(spot);
        }

        mMap.moveCamera(CameraUpdateFactory.zoomIn());
    }



    public FloatingSearchView getSearchBar(){
        return this.searchBar;
    }

    public Location getCurrentLocation(){
        return this.currentLocation;
    }

    private void enableMyLocation() {
        Log.d(TAG, "enable location");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);

        } else if(mMap != null){
            hasPermissions = true;
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasPermissions = true;
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (!hasPermissions) {
            // Permission was not granted, display error dialog
            enableMyLocation();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (mMap == null)
            return false;

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(currentLocation == null) {
            return false;
        }

        LatLng moveTo = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(moveTo));

        return false;
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection with google services failed!");
        Toast.makeText(getApplicationContext(), R.string.on_request_fail_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "CONNECTION ESTABLISHED!");
        if(hasPermissions) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        } else {
            LatLng current = new LatLng(48.210033, 16.363449);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
            enableMyLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
