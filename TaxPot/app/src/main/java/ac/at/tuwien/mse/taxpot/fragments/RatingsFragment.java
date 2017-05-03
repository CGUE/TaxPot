package ac.at.tuwien.mse.taxpot.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.models.TaxPot;

/**
 * Created by markj on 4/29/2017.
 */

public class RatingsFragment extends Fragment {

    private TaxPot taxPot;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;

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

        TextView parkingSpaceValue = (TextView) view.findViewById(R.id.parkingSpaceValue);
        parkingSpaceValue.setText(taxPot.getParkingSpace());

        TextView serviceTimeValue = (TextView) view.findViewById(R.id.serviceTimeValue);
        serviceTimeValue.setText(taxPot.getServiceTime());

        final RatingBar driversRating = (RatingBar) view.findViewById(R.id.driversRating);
        driversRating.setMax(5);
        driversRating.setClickable(false);

        RatingBar safeRating = (RatingBar) view.findViewById(R.id.safeRating);
        safeRating.setMax(5);
        //safeRating.setNumStars(3);
        safeRating.setClickable(false);

        final RatingBar taxiCountRating = (RatingBar) view.findViewById(R.id.taxiCountRating);
        taxiCountRating.setMax(5);
        //taxiCountRating.setRating(2.5f);
        taxiCountRating.setClickable(false);


        Button submitRatingBtn = (Button)view.findViewById(R.id.postCommentBtn);
        submitRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("TaxPot", String.valueOf(driversRating.getRating()));
                /*
                layoutInflater = LayoutInflater.from(getActivity());
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.layout_submit_rating,null);

                popupWindow = new PopupWindow(container,400,400, true);
                popupWindow.showAtLocation(container.get);
                */

                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_submit_rating,null);
                popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.showAsDropDown(popupView,0,0);

                Button submitRatingBtn = (Button)popupView.findViewById(R.id.postCommentBtn);
                submitRatingBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        popupWindow.dismiss();
                        //TODO:
                    }
                });

                Button cancelBtn = (Button)popupView.findViewById(R.id.cancel_btn);
                cancelBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        popupWindow.dismiss();
                    }
                });
            }
        });


        return view;
    }

}
