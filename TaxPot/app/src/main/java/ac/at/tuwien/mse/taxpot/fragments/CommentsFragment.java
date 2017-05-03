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
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.models.TaxPot;

/**
 * Created by Aileen on 5/3/2017.
 */

public class CommentsFragment extends Fragment {
    private TaxPot taxPot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_comments, container, false);

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

        final TextView ownComment = (TextView) view.findViewById(R.id.own_comment);

        final ScrollView commentSection = (ScrollView) view.findViewById(R.id.comment_section);

        Button postCommentBtn = (Button)view.findViewById(R.id.postCommentBtn);
        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ownComment == null) {
                    Log.d("TaxPot", "comment null");
                }

                if(commentSection == null){
                    Log.d("TaxPot", "section null");
                }

                //commentSection.addView(ownComment);

            }
        });

        return view;
    }
}
