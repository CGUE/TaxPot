package ac.at.tuwien.mse.taxpot.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.models.TaxPot;

/**
 * Created by markj on 4/29/2017.
 */

public class RatingsFragment extends Fragment {

    private TaxPot taxPot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("TaxPot", "Ratingsfragment created");
        View view = inflater.inflate(R.layout.layout_ratings, container, false);

        taxPot = new TaxPot();
        taxPot.setAddress(getArguments().getString("adress"));
        LatLng latLng = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));
        taxPot.setLatLng(latLng);
        taxPot.setServiceTime(getArguments().getString("serviceTime"));
        taxPot.setParkingSpace(getArguments().getString("parkingSpace"));

        TextView header = (TextView) view.findViewById(R.id.streetName);
        header.setText(taxPot.getAddress());

        return view;
    }

}
