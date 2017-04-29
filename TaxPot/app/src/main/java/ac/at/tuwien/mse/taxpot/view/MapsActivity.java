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
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
                                                            GoogleApiClient.OnConnectionFailedListener,
                                                            OnMyLocationButtonClickListener,
                                                            ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = "TaxPot";
    private GoogleApiClient googleApiClient;

    private boolean hasPermissions = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private FloatingSearchView searchBar;

    private ClusterManager<TaxPot> taxPotClusterManager;
    private Map<Marker, TaxPot> markerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // establish connection with google services
        googleApiClient = new GoogleApiClient.Builder(this)
                                .addApi(Places.GEO_DATA_API)
                                .addApi(Places.PLACE_DETECTION_API)
                                .enableAutoManage(this, this)
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

        markerData = new HashMap<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // start initializing markers
        new StartupTask().execute("");

        // Add a marker in current position and move the camera
        LatLng current = new LatLng(48.210033, 16.363449);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.setMinZoomPreference(10.f);
        mMap.setMaxZoomPreference(20.f);

        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.f));

        // activate my location button
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        SearchService service = new SearchService(googleApiClient, mMap, searchBar);
        searchBar.setOnQueryChangeListener(service);
        searchBar.setOnSearchListener(service);

        // initialize clustermanager
        taxPotClusterManager = new ClusterManager<TaxPot>(this, mMap);
        taxPotClusterManager.setOnClusterItemClickListener(new MarkerDetailService(this));

        mMap.setOnMarkerClickListener(taxPotClusterManager);
        mMap.setOnCameraIdleListener(taxPotClusterManager);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

        } else if (mMap != null) {
            hasPermissions = true;
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("TaxPot", "Permission for location granted.");
            hasPermissions = true;
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public boolean onMyLocationButtonClick() {
        if (mMap == null)
            return false;

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (!hasPermissions) {
            enableMyLocation();
            return false;
        }

        Location location = manager.getLastKnownLocation(manager.getBestProvider(criteria, false));
        if(location != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection with google services failed!");
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    // adds all taxpots to map
    private boolean taxPotsInitialized(List<TaxPot> results){
        if(mMap == null){
            return false;
        }

        for(TaxPot result : results){
//            Marker marker = mMap.addMarker(new MarkerOptions()
//                            .title(result.getAddress())
//                            .position(result.getLatLng())
//                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_taxpot)));
//
//            markerData.put(marker, result);
            taxPotClusterManager.addItem(result);
        }
        return true;
    }

    private class StartupTask extends AsyncTask<String, String, Void> {

        private List<TaxPot> results;
        private String response;

        @Override
        protected Void doInBackground(String... params) {
            // TODO: save url in resources
            final String datagvURL = "http://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:TAXIOGD&srsName=EPSG:4326&outputFormat=json";

            results = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(datagvURL);
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                response = buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray features = jsonObject.getJSONArray("features");

                for(int i = 0; i < features.length(); i++){
                    JSONObject feature = (JSONObject) features.get(i);
                    JSONObject properties = feature.getJSONObject("properties");

                    JSONObject geometryInfo = feature.getJSONObject("geometry");
                    JSONArray coordinates = geometryInfo.getJSONArray("coordinates");
                    JSONArray latLngInfo = coordinates.getJSONArray(0);

                    TaxPot spot = new TaxPot();
                    if(properties.get("ADRESSE") != null)
                        spot.setAddress(properties.getString("ADRESSE"));

                    if(latLngInfo != null)
                        spot.setLatLng(new LatLng(latLngInfo.getDouble(1), latLngInfo.getDouble(0)));

                    if(properties.get("ZEITRAUM") != null)
                        spot.setServiceTime(properties.getString("ZEITRAUM"));

                    if(properties.get("STELLPLATZANZAHL") != null)
                        spot.setParkingSpace(properties.getString("STELLPLATZANZAHL"));

                    results.add(spot);
                }
                executeUpdate();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void executeUpdate(){
            if(!taxPotsInitialized(results))
                executeUpdate();
        }
    }
}
