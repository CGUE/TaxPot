package ac.at.tuwien.mse.taxpot.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.databinding.LayoutMarkerdetailsBinding;
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

/**
 * Created by markj on 4/29/2017.
 */

public class DetailsFragment extends Fragment {

    private TaxPot taxPot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        LayoutMarkerdetailsBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_markerdetails, container, false);
        taxPot = (TaxPot) getArguments().getSerializable("taxpot");
        binding.setTaxpot(taxPot);

        MapsActivity mainActivity = (MapsActivity) getActivity();
        if(mainActivity.getmMap() != null){
            mainActivity.getmMap().getUiSettings().setAllGesturesEnabled(false);
        }
        if(mainActivity.getMyLocationButton() != null) {
            mainActivity.getMyLocationButton().hide();
        }

        //TODO: rating stars here!

        binding.navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = "q="+taxPot.getLatitude()+","+taxPot.getLongitude();
                q += "&mode=w";
                Log.d("TaxPot", getString(R.string.google_navigation_url)+q);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_navigation_url)+q));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        binding.ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get FragmentManager and start FragmentTransaction
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                RatingsFragment ratingsFrag = new RatingsFragment();
                ratingsFrag.setArguments(getArguments());

                // replace
                transaction.setCustomAnimations(R.animator.slide_up,
                        R.animator.slide_down, 0, 0);
                transaction.replace(R.id.detailsFragment_container, ratingsFrag, "RatingsFragment");
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        // Button commentButton = (Button)view.findViewById(R.id.report_button);
        binding.reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get FragmentManager and start FragmentTransaction
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                CommentsFragment commentsFrag = new CommentsFragment();
                commentsFrag.setArguments(getArguments());

                // replace
                transaction.setCustomAnimations(R.animator.slide_up,
                        R.animator.slide_down, 0, 0);
                transaction.replace(R.id.detailsFragment_container, commentsFrag, "CommentFragment");
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapsActivity mainActivity = (MapsActivity) getActivity();
        if(mainActivity.getmMap() != null){
            mainActivity.getmMap().getUiSettings().setAllGesturesEnabled(true);
        }
        if(mainActivity.getMyLocationButton() != null){
            mainActivity.getMyLocationButton().show();
        }
    }

    public void OnDurationCalculatedCallback(String duration){
        if(taxPot != null){
            taxPot.setDuration(duration);
        }
    }

}
