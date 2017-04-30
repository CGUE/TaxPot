package ac.at.tuwien.mse.taxpot.view.CustomCluster;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.models.TaxPot;

/**
 * Created by markj on 4/30/2017.
 */

public class CustomClusterRenderer extends DefaultClusterRenderer<TaxPot> {

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<TaxPot> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(TaxPot item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_taxpot));
    }
}
