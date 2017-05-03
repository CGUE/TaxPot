package ac.at.tuwien.mse.taxpot.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.text.Text;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.models.TaxPot;

/**
 * Created by markj on 4/29/2017.
 */

public class DetailsFragment extends Fragment {

    private TaxPot taxPot;
    private TextView duration_tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_markerdetails, container, false);

        taxPot = new TaxPot();
        taxPot.setAddress(getArguments().getString("adress"));
        LatLng latLng = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));
        taxPot.setLatLng(latLng);
        taxPot.setServiceTime(getArguments().getString("serviceTime"));
        taxPot.setParkingSpace(getArguments().getString("parkingSpace"));

        // set streetname
        TextView header = (TextView) view.findViewById(R.id.streetname_header);
        header.setText(taxPot.getAddress());

        // set duration
        duration_tv = (TextView) view.findViewById(R.id.distance_in_min);

        //TODO: rating stars here!

        TextView ratingValue = (TextView) view.findViewById(R.id.rating);
        ratingValue.setText(String.valueOf(taxPot.getRating()));

        Button ratingButton = (Button)view.findViewById(R.id.rating_button);
        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get FragmentManager and start FragmentTransaction
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putString("adress", taxPot.getAddress());
                bundle.putDouble("longitude", taxPot.getLatLng().longitude);
                bundle.putDouble("latitude", taxPot.getLatLng().latitude);
                bundle.putString("serviceTime", taxPot.getServiceTime());
                bundle.putString("parkingSpace", taxPot.getParkingSpace());

                RatingsFragment ratingsFrag = new RatingsFragment();
                ratingsFrag.setArguments(bundle);

                // replace
                transaction.setCustomAnimations(R.animator.slide_up,
                        R.animator.slide_down, 0, 0);
                transaction.replace(R.id.detailsFragment_container, ratingsFrag, "RatingsFragment");
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        Button commentButton = (Button)view.findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get FragmentManager and start FragmentTransaction
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putString("adress", taxPot.getAddress());
                bundle.putDouble("longitude", taxPot.getLatLng().longitude);
                bundle.putDouble("latitude", taxPot.getLatLng().latitude);
                bundle.putString("serviceTime", taxPot.getServiceTime());
                bundle.putString("parkingSpace", taxPot.getParkingSpace());

                CommentsFragment commentsFrag = new CommentsFragment();
                commentsFrag.setArguments(bundle);

                // replace
                transaction.setCustomAnimations(R.animator.slide_up,
                        R.animator.slide_down, 0, 0);
                transaction.replace(R.id.detailsFragment_container, commentsFrag, "CommentFragment");
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        return view;
    }

    public void OnDurationCalculatedCallback(String duration){
        duration_tv.setText(duration);
    }

}
