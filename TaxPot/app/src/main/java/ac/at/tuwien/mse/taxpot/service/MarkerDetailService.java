package ac.at.tuwien.mse.taxpot.service;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.fragments.DetailsFragment;
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.utli.GoogleClient;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

/**
 * Created by markj on 4/29/2017.
 */

public class MarkerDetailService implements ClusterManager.OnClusterItemClickListener<TaxPot> {

    private MapsActivity mainActivity;
    private GoogleApiClient googleApiClient;

    private Map<String, Boolean> ratingStates = new HashMap<>();

    public MarkerDetailService(MapsActivity mainActivity, GoogleApiClient googleApiClient){
        this.mainActivity = mainActivity;
        this.googleApiClient = googleApiClient;

        // observe initialization states
        ratingStates.put("friendliness", false);
        ratingStates.put("safety", false);
        ratingStates.put("occupancy", false);
    }

    @Override
    public boolean onClusterItemClick(final TaxPot taxPot) {

        // get FragmentManager and start FragmentTransaction
        FragmentManager fragmentManager = mainActivity.getFragmentManager();

        // remove old fragment
        if(fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }

        // Firebase database ref
        final DatabaseReference friendlinessRef = mainActivity.getDatabase().getReference().child(taxPot.getId()).child("friendliness");
        final DatabaseReference safetyRef = mainActivity.getDatabase().getReference().child(taxPot.getId()).child("safety");
        final DatabaseReference occupancyRef = mainActivity.getDatabase().getReference().child(taxPot.getId()).child("occupancy");

        friendlinessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Double>> type = new GenericTypeIndicator<List<Double>>() {};
                if(!dataSnapshot.exists()){
                    taxPot.setFriendliness(new ArrayList<Double>());
                } else {
                    List<Double> ratings = dataSnapshot.getValue(type);
                    taxPot.setFriendliness(ratings);
                }
                taxPot.calculateFriendliness();
                taxPot.calculateAvgRating();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        safetyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Double>> type = new GenericTypeIndicator<List<Double>>() {};
                if(!dataSnapshot.exists()){
                    taxPot.setSafety(new ArrayList<Double>());
                } else {
                    List<Double> ratings = dataSnapshot.getValue(type);
                    taxPot.setSafety(ratings);
                }
                taxPot.calculateSafety();
                taxPot.calculateAvgRating();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        occupancyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Double>> type = new GenericTypeIndicator<List<Double>>() {};
                if(!dataSnapshot.exists()){
                    taxPot.setOccupancy(new ArrayList<Double>());
                } else {
                    List<Double> ratings = dataSnapshot.getValue(type);
                    taxPot.setOccupancy(ratings);
                }
                taxPot.calculateOccupancy();
                taxPot.calculateAvgRating();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("taxpot", taxPot);

        final DetailsFragment detailsFrag = new DetailsFragment();
        getDuration(mainActivity.getCurrentLocation(), new LatLng(taxPot.getLatitude(), taxPot.getLongitude()), detailsFrag);
        detailsFrag.setArguments(bundle);

        transaction.setCustomAnimations(R.animator.slide_up,
                R.animator.slide_down, 0, 0);
        transaction.add(R.id.detailsFragment_container, detailsFrag, "DetailsFragment");

        transaction.addToBackStack(null);
        transaction.commit();

        return false;
    }

    private void getDuration(Location currentLocation, LatLng latLng, final DetailsFragment fragment) {
        if(currentLocation == null){
            return;
        }

        String url = mainActivity.getResources().getString(R.string.google_directions_url);
        url += "origin="+ currentLocation.getLatitude()+","+currentLocation.getLongitude();
        url += "&destination="+latLng.latitude+","+latLng.longitude;
        url += "&mode=walking";
        url += "&key="+mainActivity.getResources().getString(R.string.google_maps_key);

        Log.d("TaxPot", "URL: "+url);

        JsonObjectRequest request  = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TaxPot", "Response received!: "+response.toString());

                        try {
                            JSONArray routes = response.getJSONArray("routes");
                            JSONObject sub = (JSONObject)routes.get(0);
                            JSONObject legs = (JSONObject) sub.getJSONArray("legs").get(0);
                            JSONObject duration = (JSONObject)legs.get("duration");

                           fragment.OnDurationCalculatedCallback(duration.getString("text"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
        GoogleClient.getInstance(mainActivity.getApplicationContext()).addToRequestQueue(request);
    }
}
