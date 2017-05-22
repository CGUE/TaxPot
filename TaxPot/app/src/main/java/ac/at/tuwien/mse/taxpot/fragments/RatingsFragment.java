package ac.at.tuwien.mse.taxpot.fragments;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
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
import ac.at.tuwien.mse.taxpot.databinding.LayoutRatingsBinding;
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

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
        final LayoutRatingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_ratings, container, false);

        taxPot = (TaxPot) getArguments().get("taxpot");
        binding.setTaxpot(taxPot);

        MapsActivity mainActivity = (MapsActivity) getActivity();
        if(mainActivity.getMyLocationButton() != null) {
            mainActivity.getMyLocationButton().hide();
        }

        binding.driversRating.setMax(5);
        binding.driversRating.setClickable(false);

        binding.safeRating.setMax(5);
        //safeRating.setNumStars(3);
        binding.safeRating.setClickable(false);

        binding.taxiCountRating.setMax(5);
        //taxiCountRating.setRating(2.5f);
        binding.taxiCountRating.setClickable(false);

        binding.postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("TaxPot", String.valueOf(binding.driversRating.getRating()));
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
        return binding.getRoot();
    }

}
