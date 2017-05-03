package ac.at.tuwien.mse.taxpot.service;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public MarkerDetailService(MapsActivity mainActivity, GoogleApiClient googleApiClient){
        this.mainActivity = mainActivity;
        this.googleApiClient = googleApiClient;
    }

    @Override
    public boolean onClusterItemClick(TaxPot taxPot) {

        // get FragmentManager and start FragmentTransaction
        FragmentManager fragmentManager = mainActivity.getFragmentManager();

        // remove old fragment
        if(fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("adress", taxPot.getAddress());
        bundle.putDouble("longitude", taxPot.getLatLng().longitude);
        bundle.putDouble("latitude", taxPot.getLatLng().latitude);
        bundle.putString("serviceTime", taxPot.getServiceTime());
        bundle.putString("parkingSpace", taxPot.getParkingSpace());

        final DetailsFragment detailsFrag = new DetailsFragment();
        getDuration(mainActivity.getCurrentLocation(), taxPot.getLatLng(), detailsFrag);
        detailsFrag.setArguments(bundle);

        transaction.setCustomAnimations(R.anim.slide_up,
                R.anim.slide_down, 0, 0);
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
        JsonObjectRequest request  = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TaxPot", "Response received!");

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
