package ac.at.tuwien.mse.taxpot.models;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private String duration;

    // rating variables
    private List<Double> friendliness;
    private List<Double> safety;
    private List<Double> occupancy;

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

    public List<Double> getFriendliness() {
        return friendliness;
    }

    public void setFriendliness(List<Double> friendliness) {
        this.friendliness = friendliness;
    }

    public List<Double> getSafety() {
        return safety;
    }

    public void setSafety(List<Double> safety) {
        this.safety = safety;
    }

    public List<Double> getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(List<Double> occupancy) {
        this.occupancy = occupancy;
    }

    public float calculateFriendliness(){
        if(friendliness == null || friendliness.size() == 0){
            return 0.f;
        }

        float sum = 0;
        for(Double val : friendliness){
            sum += val;
        }
        return sum/friendliness.size();
    }

    public float calculateSafety(){
        if(safety == null || safety.size() == 0){
            return 0.f;
        }

        float sum = 0;
        for(Double val : safety){
            sum += val;
        }
        return sum/safety.size();
    }

    public float calculateOccupancy(){
        if(occupancy == null || occupancy.size() == 0){
            return 0.f;
        }

        float sum = 0;
        for(Double val : occupancy){
            sum += val;
        }
        return sum/occupancy.size();
    }

    public float calculateAvgRating(){
        float sumFriendliness = 0;
        float sumSafety = 0;
        float sumOccupancy = 0;

        if(friendliness == null){
            friendliness = new ArrayList<Double>();
        }
        for(Double val : friendliness){
            sumFriendliness += val;
        }

        if(safety == null){
            safety = new ArrayList<Double>();
        }
        for(Double val : safety){
            sumSafety += val;
        }

        if(occupancy == null){
            occupancy = new ArrayList<Double>();
        }
        for(Double val : occupancy){
            sumOccupancy += val;
        }

        sumFriendliness = friendliness.size() > 0 ? sumFriendliness / friendliness.size() : 0;
        sumSafety = safety.size() > 0 ? sumSafety / safety.size() : 0;
        sumOccupancy = occupancy.size() > 0 ? sumOccupancy / occupancy.size() : 0;

        return (sumFriendliness + sumSafety + sumOccupancy) / 3;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
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
