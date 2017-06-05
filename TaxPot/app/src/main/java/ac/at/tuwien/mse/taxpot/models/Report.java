package ac.at.tuwien.mse.taxpot.models;

/**
 * Created by Aileen on 6/5/2017.
 */

public class Report {
    private String streetname;
    private int occupancy;
    private String timeframe;

    public Report(){}

    public Report(String streetname, int occupancy, String timeframe){
        this.streetname = streetname;
        this.occupancy = occupancy;
        this.timeframe = timeframe;
    }

    public String getStreetname() {
        return streetname;
    }

    public void setStreetname(String streetname) {
        this.streetname = streetname;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }
}
