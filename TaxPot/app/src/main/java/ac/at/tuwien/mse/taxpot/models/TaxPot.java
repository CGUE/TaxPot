package ac.at.tuwien.mse.taxpot.models;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

import ac.at.tuwien.mse.taxpot.BR;

/**
 * Created by markj on 4/29/2017.
 */

public class TaxPot implements ClusterItem, Serializable, Observable{

    private transient PropertyChangeRegistry registry = new PropertyChangeRegistry();

    private String id;
    private String address;
    private Double latitude;
    private Double longitude;
    private String parkingSpace;
    private String serviceTime;
    private double rating =3.5;
    private int ratingCount = 0;
    private double allRatings;
    private String duration;

    @Bindable
    public String getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDuration(String duration) {
        this.duration = duration;
        registry.notifyChange(this, BR.duration);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
        return new LatLng(latitude, longitude);
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

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        registry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        registry.remove(onPropertyChangedCallback);
    }
}
