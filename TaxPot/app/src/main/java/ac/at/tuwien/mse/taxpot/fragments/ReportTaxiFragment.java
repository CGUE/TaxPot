package ac.at.tuwien.mse.taxpot.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.models.Report;
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

/**
 * Created by Aileen on 5/3/2017.
 */

public class ReportTaxiFragment extends Fragment {
    private TaxPot taxPot;
    private Report report;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout_reporttaxi, container, false);

        taxPot = new TaxPot();
        report = new Report();

        final MapsActivity mainActivity = (MapsActivity) getActivity();
        if(mainActivity.getmMap() != null){
            mainActivity.getmMap().getUiSettings().setAllGesturesEnabled(false);
        }
        if(mainActivity.getMyLocationButton() != null) {
            mainActivity.getMyLocationButton().hide();
        }

        view.findViewById(R.id.rl_report).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("TaxPot", "outside of report layout touched!");
                if(mainActivity.getmMap() != null){
                    mainActivity.getmMap().getUiSettings().setAllGesturesEnabled(true);
                }
                mainActivity.getFragmentManager().popBackStack();
            }
        });


        final Button reportButton = (Button)view.findViewById(R.id.report_button);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TaxPot","report button clicked");

                String address = ((EditText) view.findViewById(R.id.input_box_address)).getText().toString();
                String timeframe = ((EditText) view.findViewById(R.id.input_box_timeframe)).getText().toString();
                String timeframe2 = ((EditText) view.findViewById(R.id.input_box_timeframe2)).getText().toString();
                String taxiPlaceCount = ((EditText) view.findViewById(R.id.input_box_taxiPlaceCount)).getText().toString();

                final DatabaseReference reportIdRef = mainActivity.getDatabase().getReference().child("Reports");

                if(isEmpty(address)){
                    return;
                }
                report.setStreetname(address);
                if(!isEmpty(taxiPlaceCount)) {
                    report.setOccupancy(Integer.parseInt(taxiPlaceCount));
                }
                String finalTimeframe = isEmpty(timeframe) ? "" : timeframe;
                finalTimeframe += isEmpty(finalTimeframe) ? (isEmpty(timeframe2) ? "" : timeframe2) : (isEmpty(timeframe2) ? "" : " - " + timeframe2);

                if(!isEmpty(finalTimeframe)) {
                    report.setTimeframe(finalTimeframe);
                }

                reportIdRef.push().setValue(report);

                Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.taxi_reported), Toast.LENGTH_LONG).show();
                getFragmentManager().popBackStack();

                if(mainActivity.getmMap() != null){
                    mainActivity.getmMap().getUiSettings().setAllGesturesEnabled(true);
                }
            }
        });

        return view;

    }

    private boolean isEmpty(String val){
        return val == null || val.isEmpty();
    }
}
