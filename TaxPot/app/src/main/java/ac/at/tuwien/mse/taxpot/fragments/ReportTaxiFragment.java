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

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.models.TaxPot;

/**
 * Created by Aileen on 5/3/2017.
 */

public class ReportTaxiFragment extends Fragment {
    private TaxPot taxPot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout_reporttaxi, container, false);

        taxPot = new TaxPot();

        Button reportButton = (Button)view.findViewById(R.id.report_button);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TaxPot","report button clicked");
                //TODO:handle the input

                EditText address = (EditText) view.findViewById(R.id.input_box_address);
                EditText timeframe = (EditText) view.findViewById(R.id.input_box_timeframe);
                EditText taxiPlaceCount = (EditText) view.findViewById(R.id.input_box_taxiPlaceCount);

                taxPot.setAddress(address.toString());
                taxPot.setServiceTime(timeframe.toString());
                taxPot.setParkingSpace(taxiPlaceCount.toString());
            }
        });

        return view;

    }
}
