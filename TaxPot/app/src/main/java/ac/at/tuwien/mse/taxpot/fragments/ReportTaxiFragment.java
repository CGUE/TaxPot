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
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

/**
 * Created by Aileen on 5/3/2017.
 */

public class ReportTaxiFragment extends Fragment {
    private TaxPot taxPot;
    private static int id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout_reporttaxi, container, false);

        taxPot = new TaxPot();

        final MapsActivity mainActivity = (MapsActivity) getActivity();
        if(mainActivity.getmMap() != null){
            mainActivity.getmMap().getUiSettings().setAllGesturesEnabled(false);
        }
        if(mainActivity.getMyLocationButton() != null) {
            mainActivity.getMyLocationButton().hide();
        }


        Button reportButton = (Button)view.findViewById(R.id.report_button);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TaxPot","report button clicked");
                //TODO:handle the input

                final EditText address = (EditText) view.findViewById(R.id.input_box_address);
                EditText timeframe = (EditText) view.findViewById(R.id.input_box_timeframe);
                EditText taxiPlaceCount = (EditText) view.findViewById(R.id.input_box_taxiPlaceCount);

                taxPot.setId("Report"+id);
                taxPot.setAddress(address.getText().toString());
                taxPot.setServiceTime(timeframe.getText().toString());
                taxPot.setParkingSpace(taxiPlaceCount.getText().toString());

                // report new Taxi to database
                final DatabaseReference addressRef = mainActivity.getDatabase().getReference().child("Reports").child(taxPot.getId()).child("address");
                final DatabaseReference timeframeRef = mainActivity.getDatabase().getReference().child("Reports").child(taxPot.getId()).child("timeframe");
                final DatabaseReference taxiPlaceCountRef = mainActivity.getDatabase().getReference().child("Reports").child(taxPot.getId()).child("taxiplaceCount");
                addressRef.setValue(address.getText().toString());
                timeframeRef.setValue(timeframe.getText().toString());
                taxiPlaceCountRef.setValue(taxiPlaceCount.getText().toString());


                //reportRef.setValue(taxPot.getAddress());
                Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.taxi_reported), Toast.LENGTH_LONG).show();
                getFragmentManager().popBackStack();

                id++;
            }
        });

        return view;

    }
}
