package ac.at.tuwien.mse.taxpot.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.service.SearchService;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
                                                            GoogleApiClient.OnConnectionFailedListener,
                                                            OnMyLocationButtonClickListener,
                                                            ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleApiClient googleApiClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean hasPermissions = false;

    private GoogleMap mMap;
    private Marker searchMarker;
    private FloatingSearchView searchBar;

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

/**
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
 **/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
        Log.d("TaxPot", "Connection with google services failed!");
    }

}
