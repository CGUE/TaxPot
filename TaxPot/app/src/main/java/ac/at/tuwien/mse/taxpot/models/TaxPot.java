package ac.at.tuwien.mse.taxpot.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by markj on 4/29/2017.
 */

public class TaxPot implements ClusterItem{

    private String address;
    private LatLng latLng;
    private String parkingSpace;
    private String serviceTime;
    private double rating =3.5;
    private int ratingCount = 0;
    private double allRatings;
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

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

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public double getRating(){
        return this.rating;
    }

    public void setRating(double rating){
        ratingCount++;

        this.rating = rating;

        //TODO: calculate avarage rating here;
        /*
        allRatings += rating;
        this.rating = allRatings/ratingCount;
        */
    }
}
