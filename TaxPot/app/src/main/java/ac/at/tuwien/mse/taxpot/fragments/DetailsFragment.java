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

import ac.at.tuwien.mse.taxpot.R;

/**
 * Created by markj on 4/29/2017.
 */

public class DetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_markerdetails, container, false);

        Button ratingButton = (Button)view.findViewById(R.id.rating_button);
        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get FragmentManager and start FragmentTransaction
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                RatingsFragment ratingsFrag = new RatingsFragment();

                // replace
                transaction.setCustomAnimations(R.anim.slide_up,
                        R.anim.slide_down, 0, 0);
                transaction.replace(R.id.detailsFragment_container, ratingsFrag, "RatingsFragment");
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        return view;
    }

}
