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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        final MapsActivity mainActivity = (MapsActivity) getActivity();
        if(mainActivity.getmMap() != null){
            mainActivity.getmMap().getUiSettings().setAllGesturesEnabled(false);
        }
        if(mainActivity.getMyLocationButton() != null) {
            mainActivity.getMyLocationButton().hide();
        }

        binding.driversRating.setMax(5);
        binding.driversRating.setClickable(false);
        Log.d("Rating", "friendliness: "+binding.driversRating.getRating());

        binding.safeRating.setMax(5);
        binding.safeRating.setClickable(false);
        Log.d("Rating", "safety: "+binding.safeRating.getRating());

        binding.taxiCountRating.setMax(5);
        binding.taxiCountRating.setClickable(false);
        Log.d("Rating", "occupancy: "+binding.taxiCountRating.getRating());

        binding.postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_submit_rating,null);
                popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.showAsDropDown(popupView,0,0);

                Button submitRatingBtn = (Button)popupView.findViewById(R.id.postCommentBtn);
                submitRatingBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Log.d("TaxPot", "Rating for "+taxPot.getId()+" submitted!");
                        // submit rating to database
                        final DatabaseReference friendlinessRef = mainActivity.getDatabase().getReference().child(taxPot.getId()).child("friendliness");
                        final DatabaseReference safetyRef = mainActivity.getDatabase().getReference().child(taxPot.getId()).child("safety");
                        final DatabaseReference occupancyRef = mainActivity.getDatabase().getReference().child(taxPot.getId()).child("occupancy");

                        friendlinessRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<List<Double>> type = new GenericTypeIndicator<List<Double>>() {};
                                List<Double> values;
                                if(dataSnapshot.exists()){
                                    values = dataSnapshot.getValue(type);
                                } else {
                                    values = new ArrayList<Double>();
                                }
                                taxPot.setFriendliness(values);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        safetyRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<List<Double>> type = new GenericTypeIndicator<List<Double>>() {};
                                List<Double> values;
                                if(dataSnapshot.exists()){
                                    values = dataSnapshot.getValue(type);
                                } else {
                                    values = new ArrayList<Double>();
                                }
                                taxPot.setSafety(values);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        occupancyRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<List<Double>> type = new GenericTypeIndicator<List<Double>>() {};
                                List<Double> values;
                                if(dataSnapshot.exists()){
                                    values = dataSnapshot.getValue(type);
                                } else {
                                    values = new ArrayList<Double>();
                                }
                                taxPot.setOccupancy(values);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        double friendlinessRating = ((RatingBar)popupView.findViewById(R.id.driversRating)).getRating();
                        double safetyRating = ((RatingBar)popupView.findViewById(R.id.safeRating)).getRating();
                        double occupancyRating = ((RatingBar)popupView.findViewById(R.id.taxiCountRating)).getRating();

                        Log.d("TaxPot", "ratings: "+friendlinessRating+"; "+safetyRating+"; "+occupancyRating);

                        if(friendlinessRating > 0){
                            taxPot.getFriendliness().add(friendlinessRating);
                            friendlinessRef.setValue(taxPot.getFriendliness());
                        }
                        if(safetyRating > 0){
                            taxPot.getSafety().add(safetyRating);
                            safetyRef.setValue(taxPot.getSafety());
                        }
                        if(occupancyRating > 0){
                            taxPot.getOccupancy().add(occupancyRating);
                            occupancyRef.setValue(taxPot.getOccupancy());
                        }
                        popupWindow.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.rating_submitted), Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStack();
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
