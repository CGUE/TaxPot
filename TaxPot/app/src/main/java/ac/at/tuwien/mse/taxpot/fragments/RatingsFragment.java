package ac.at.tuwien.mse.taxpot.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ac.at.tuwien.mse.taxpot.R;

/**
 * Created by markj on 4/29/2017.
 */

public class RatingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("TaxPot", "Ratingsfragment created");
        View view = inflater.inflate(R.layout.layout_ratings, container, false);
        return view;
    }

}
