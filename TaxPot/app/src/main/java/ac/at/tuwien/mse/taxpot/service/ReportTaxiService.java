package ac.at.tuwien.mse.taxpot.service;

import android.util.Log;
import android.view.MenuItem;

import com.arlib.floatingsearchview.FloatingSearchView;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

/**
 * Created by Aileen on 5/3/2017.
 */

public class ReportTaxiService implements FloatingSearchView.OnMenuItemClickListener {
    @Override
    public void onActionMenuItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item2){
            Log.d("TaxPot", "Melden clicked");


        }
    }
}
