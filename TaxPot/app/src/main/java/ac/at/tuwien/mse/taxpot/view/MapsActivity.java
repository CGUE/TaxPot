package ac.at.tuwien.mse.taxpot.view;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.fragments.ReportTaxiFragment;
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.service.MarkerDetailService;
import ac.at.tuwien.mse.taxpot.service.SearchService;
import ac.at.tuwien.mse.taxpot.utli.GoogleClient;
import ac.at.tuwien.mse.taxpot.utli.PermissionUtils;
import ac.at.tuwien.mse.taxpot.view.CustomCluster.CustomClusterRenderer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private final String TAG = "TaxPot";
    private GoogleApiClient googleApiClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private FloatingSearchView searchBar;

    private FirebaseDatabase database;

    private ClusterManager<TaxPot> taxPotClusterManager;
    private HashMap<String,TaxPot> allTaxPots;
    private Location currentLocation;
    private FloatingActionButton myLocationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // request permission if not given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);

        } else {
            initializeMap();
        }
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void initializeMap() {

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

        searchBar = (FloatingSearchView) findViewById(R.id.floating_search_view);

        searchBar.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                onMenuListened(item);
            }
        });

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // activate my location button
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        }
        myLocationButton = (FloatingActionButton) findViewById(R.id.my_location_button);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap == null)
                    return;

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                }
                if (currentLocation == null) {
                    Log.d(TAG, "currentLocation not found!");
                    return;
                }

                LatLng moveTo = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(moveTo));
            }
        });

        // set camera perpective
        mMap.setMinZoomPreference(10.f);
        mMap.setMaxZoomPreference(20.f);

        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.f));

        SearchService service = new SearchService(googleApiClient, mMap, this);
        searchBar.setOnQueryChangeListener(service);
        searchBar.setOnSearchListener(service);

        // initialize clustermanager
        taxPotClusterManager = new ClusterManager<TaxPot>(this, mMap);
        allTaxPots = new HashMap<>();
        taxPotClusterManager.setAlgorithm(new GridBasedAlgorithm<TaxPot>());



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

                View v = findViewById(R.id.root_view);
                final Snackbar snackbar = Snackbar.make(v, getString(R.string.internet_connection_lost), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.close_txt, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                }).show();
            }
        });

        GoogleClient.getInstance(getApplicationContext()).addToRequestQueue(request);

        taxPotClusterManager.setOnClusterItemClickListener(new MarkerDetailService(this, googleApiClient));
        taxPotClusterManager.setRenderer(new CustomClusterRenderer(getApplicationContext(), mMap, taxPotClusterManager));

        mMap.setOnMarkerClickListener(taxPotClusterManager);
        mMap.setOnCameraIdleListener(taxPotClusterManager);
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public FloatingActionButton getMyLocationButton() {
        return myLocationButton;
    }

    private void parseDatagvResponse(JSONObject json) throws JSONException {
        JSONArray features = json.getJSONArray("features");
        TaxPot spot;
        for (int i = 0; i < features.length(); i++) {
            JSONObject feature = (JSONObject) features.get(i);
            JSONObject properties = feature.getJSONObject("properties");

            JSONObject geometryInfo = feature.getJSONObject("geometry");
            JSONArray coordinates = geometryInfo.getJSONArray("coordinates");
            JSONArray latLngInfo = coordinates.getJSONArray(0);

            spot = new TaxPot();
            spot.setId(feature.getString("id").replace(".", ""));

            if (properties.get("ADRESSE") != null)
                spot.setAddress(properties.getString("ADRESSE"));

            if (latLngInfo != null)
                spot.setLatitude(latLngInfo.getDouble(1));
            spot.setLongitude(latLngInfo.getDouble(0));

            if (properties.get("ZEITRAUM") != null)
                spot.setServiceTime(properties.getString("ZEITRAUM"));

            if (properties.get("STELLPLATZANZAHL") != null)
                spot.setParkingSpace(properties.getString("STELLPLATZANZAHL"));

            taxPotClusterManager.addItem(spot);
            allTaxPots.put(spot.getId(),spot);
        }

        initSpotInfos();

        mMap.moveCamera(CameraUpdateFactory.zoomIn());
    }


    public FloatingSearchView getSearchBar() {
        return this.searchBar;
    }

    public Location getCurrentLocation() {
        return this.currentLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeMap();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_permission), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection with google services failed!");
        Toast.makeText(getApplicationContext(), R.string.on_request_fail_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        if(currentLocation != null) {
            LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "supended entered"); View v = findViewById(R.id.root_view);
        final Snackbar snackbar = Snackbar.make(v, getString(R.string.internet_connection_lost), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.close_txt, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    public void onMenuListened(MenuItem item) {
        //Log.d(TAG, "MenuItem Clicked");
        if (item.getItemId() == R.id.menu_item1){
            Log.d(TAG, "Filtern clicked");

            final View popupView = LayoutInflater.from(this).inflate(R.layout.layout_filter,null);
            final PopupWindow popupWindow = new PopupWindow(popupView,
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);

            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.RED));
            popupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);


            SeekBar driver = (SeekBar)popupView.findViewById(R.id.sliderDriver);
            SeekBar safe = (SeekBar)popupView.findViewById(R.id.sliderSafe);
            SeekBar taxiCount = (SeekBar)popupView.findViewById(R.id.slider_taxi);

            driver.setMax(10);
            safe.setMax(10);
            taxiCount.setMax(10);

            final TextView driverFilterNr = (TextView)popupView.findViewById(R.id.driver_filter_double);
            final TextView safeFilterNr = (TextView)popupView.findViewById(R.id.safe_filter_double);
            final TextView taxiCountFilterNr = (TextView)popupView.findViewById(R.id.taxi_filter_double);

            final CheckBox driverCb = (CheckBox)popupView.findViewById(R.id.driver_check_box);
            final CheckBox safeCb = (CheckBox)popupView.findViewById(R.id.safe_check_box);
            final CheckBox taxiCountCb = (CheckBox)popupView.findViewById(R.id.taxi_check_box);

            driver.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double output = (double)progress/2;
                    driverFilterNr.setText(String.valueOf(output));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            safe.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double output = (double)progress/2;
                    Log.d(TAG, progress+" "+ output);
                    safeFilterNr.setText(String.valueOf(output));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            taxiCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double output = (double)progress/2;
                    taxiCountFilterNr.setText(String.valueOf(output));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            Button toFilterBtn = (Button)popupView.findViewById(R.id.toFilter_Btn);
            toFilterBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    double driver = 0;
                    double safe = 0;
                    double taxiCount = 0;

                    if(driverCb.isChecked()){
                        //Log.d(TAG,"Driver Checkbox is checked!");
                        driver = Double.parseDouble(driverFilterNr.getText().toString());
                    }
                    if(safeCb.isChecked()){
                        //Log.d(TAG,"Safety Checkbox is checked!");
                        safe = Double.parseDouble(safeFilterNr.getText().toString());
                    }
                    if(taxiCountCb.isChecked()){
                        //Log.d(TAG,"Occupancy Checkbox is checked!");
                        taxiCount = Double.parseDouble(taxiCountFilterNr.getText().toString());
                    }

                    filterSpots(driver, safe, taxiCount);

                    popupWindow.dismiss();
                }
            });

            Button cancelBtn = (Button)popupView.findViewById(R.id.cancel_btn);
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    popupWindow.dismiss();
                }
            });

        }else if(item.getItemId() == R.id.menu_item2){
            Log.d(TAG, "Melden clicked");

            // get FragmentManager and start FragmentTransaction
            FragmentManager fragmentManager = this.getFragmentManager();

            // remove old fragment
            if(fragmentManager.getBackStackEntryCount() > 0) {
                Log.d(TAG,"popped backstack");
                fragmentManager.popBackStack();
            }

            FragmentTransaction transaction = fragmentManager.beginTransaction();

            final ReportTaxiFragment reportFrag = new ReportTaxiFragment();
            //reportFrag.setArguments(bundle);

            transaction.setCustomAnimations(R.animator.slide_up,
                    R.animator.slide_down, 0, 0);

            transaction.add(R.id.reportTaxiFragment_container, reportFrag, "ReportTaxiFragment");

            transaction.addToBackStack("reportTaxiFragment_container");

            transaction.commit();

        }
    }

    public void filterSpots(double driver, double safe, double occupancy){
        int markerCount = 0;

        Collection<TaxPot> taxPotCollection = taxPotClusterManager.getAlgorithm().getItems();
        taxPotClusterManager.clearItems();

        Iterator it = allTaxPots.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            TaxPot p = (TaxPot)entry.getValue();

            if(p.getAvgFriendliness()>= driver && p.getAvgSafety() >= safe &&
                    p.getAvgOccupancy()>= occupancy) {
                Log.d(TAG, p.getId());
                taxPotClusterManager.addItem(p);
            }
        }
        for(TaxPot p : taxPotCollection){

            markerCount++;
        }

        //Log.d(TAG, "gvdata size: " + allTaxPots.size());
        //Log.d(TAG, "Clustermanager marker size:  "+taxPotClusterManager.getMarkerCollection().getMarkers().size());
        Log.d(TAG, "MarkerCount: " + markerCount);
        taxPotClusterManager.setRenderer(new CustomClusterRenderer(this, mMap, taxPotClusterManager));
        taxPotClusterManager.cluster();
    }

    private void initSpotInfos(){
        final DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if (!data.getKey().equals("Reports")) {
                        TaxPot currentSpot = allTaxPots.get(data.getKey());
                        currentSpot.calculateAvgRating();
                        if(data.hasChild("friendliness")){
                            DataSnapshot friendlinessData = data.child("friendliness");

                            for(DataSnapshot ratings : friendlinessData.getChildren()){
                                Long rating = ratings.getValue(Long.class);
                                Double dRating = rating.doubleValue();

                                currentSpot.getFriendliness().add(dRating);

                            }
                        }

                        if(data.hasChild("safety")){
                            DataSnapshot safetyData = data.child("safety");

                            for(DataSnapshot ratings : safetyData.getChildren()){
                                Long rating = ratings.getValue(Long.class);
                                Double dRating = rating.doubleValue();

                                currentSpot.getSafety().add(dRating);

                            }
                        }

                        if(data.hasChild("occupancy")){
                            DataSnapshot occupancyData = data.child("occupancy");

                            for(DataSnapshot ratings : occupancyData.getChildren()){
                                Long rating = ratings.getValue(Long.class);
                                Double dRating = rating.doubleValue();

                                currentSpot.getOccupancy().add(dRating);
                            }
                        }

                        currentSpot.calculateFriendliness();
                        currentSpot.calculateSafety();
                        currentSpot.calculateOccupancy();
                        currentSpot.calculateAvgRating();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
