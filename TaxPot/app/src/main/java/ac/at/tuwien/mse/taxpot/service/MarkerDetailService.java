package ac.at.tuwien.mse.taxpot.service;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.fragments.DetailsFragment;
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

/**
 * Created by markj on 4/29/2017.
 */

public class MarkerDetailService implements ClusterManager.OnClusterItemClickListener<TaxPot> {

    private MapsActivity mainActivity;

    public MarkerDetailService(MapsActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onClusterItemClick(TaxPot taxPot) {

        // get FragmentManager and start FragmentTransaction
        FragmentManager fragmentManager = mainActivity.getFragmentManager();

        // remove old detail view if exist
        Fragment previousDetails = fragmentManager.findFragmentByTag("DetailsFragment");
        if(previousDetails != null){
            fragmentManager.beginTransaction().remove(previousDetails).commit();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("adress", taxPot.getAddress());
        bundle.putDouble("longitude", taxPot.getLatLng().longitude);
        bundle.putDouble("latitude", taxPot.getLatLng().latitude);
        bundle.putString("serviceTime", taxPot.getServiceTime());
        bundle.putString("parkingSpace", taxPot.getParkingSpace());

        DetailsFragment detailsFrag = new DetailsFragment();
        detailsFrag.setArguments(bundle);

        transaction.setCustomAnimations(R.animator.slide_up,
                R.animator.slide_down, 0, 0);
        transaction.add(R.id.detailsFragment_container, detailsFrag, "DetailsFragment");

        transaction.addToBackStack(null);
        transaction.commit();

        return false;
    }
}
