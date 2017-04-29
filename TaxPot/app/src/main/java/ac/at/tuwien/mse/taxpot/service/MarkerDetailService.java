package ac.at.tuwien.mse.taxpot.service;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.fragments.DetailsFragment;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

/**
 * Created by markj on 4/29/2017.
 */

public class MarkerDetailService implements GoogleMap.OnMarkerClickListener {

    private MapsActivity mainActivity;

    public MarkerDetailService(MapsActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        // get FragmentManager and start FragmentTransaction
        FragmentManager fragmentManager = mainActivity.getFragmentManager();

        // remove old detail view if exist
        Fragment previousDetails = fragmentManager.findFragmentByTag("DetailsFragment");
        if(previousDetails != null){
            fragmentManager.beginTransaction().remove(previousDetails).commit();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        DetailsFragment detailsFrag = new DetailsFragment();

        transaction.setCustomAnimations(R.animator.slide_up, 0, 0,
                R.animator.slide_down);
        transaction.add(R.id.detailsFragment_container, detailsFrag, "DetailsFragment");
        transaction.addToBackStack(null);
        transaction.commit();

        return false;
    }
}
