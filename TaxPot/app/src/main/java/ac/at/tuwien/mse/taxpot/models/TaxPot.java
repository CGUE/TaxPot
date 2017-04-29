package ac.at.tuwien.mse.taxpot.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by markj on 4/29/2017.
 */

public class TaxPot {

    private String address;
    private LatLng latLng;
    private String parkingSpace;
    private String serviceTime;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getParkingSpace() {
        return parkingSpace;
    }

    public void setParkingSpace(String parkingSpace) {
        this.parkingSpace = parkingSpace;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }
}
