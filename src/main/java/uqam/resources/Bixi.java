package uqam.resources;

import org.springframework.stereotype.*;

@Component
public class Bixi {

    private int id;
    private String stationName;
    private int stationId;
    private int stationState;
    private boolean stationIsBlocked;
    private boolean stationUnderMaintenance;
    private boolean stationOutOforder;
    private long millisLastUpdate;
    private long millisLastServerCommunication;
    private double lat;
    private double lng;
    private int availableTerminals;
    private int unavailableTerminals;
    private int availableBikes;
    private int unavailableBikes;

    // Default constructor
    public Bixi() {
    }

    // Constructor with parameters
    public Bixi(int id,
            String stationName,
            int stationId,
            int stationState,
            boolean stationIsBlocked,
            boolean stationUnderMaintenance,
            boolean stationOutOforder,
            long millisLastUpdate,
            long millisLastServerCommunication,
            double lat,
            double lng,
            int availableTerminals,
            int unavailableTerminals,
            int availableBikes,
            int unavailableBikes) {
        this.id = id;
        this.stationName = stationName;
        this.stationId = stationId;
        this.stationState = stationState;
        this.stationIsBlocked = stationIsBlocked;
        this.stationUnderMaintenance = stationUnderMaintenance;
        this.stationOutOforder = stationOutOforder;
        this.millisLastUpdate = millisLastUpdate;
        this.millisLastServerCommunication = millisLastServerCommunication;
        this.lat = lat;
        this.lng = lng;
        this.availableTerminals = availableTerminals;
        this.unavailableTerminals = unavailableTerminals;
        this.availableBikes = availableBikes;
        this.unavailableBikes = unavailableBikes;
    }

    // Getters methods
    public int getId() {
        return id;
    }

    public String getStationName() {
        return stationName;
    }

    public int getStationId() {
        return stationId;
    }

    public int getStationState() {
        return stationState;
    }

    public boolean getStationIsBlocked() {
        return stationIsBlocked;
    }

    public boolean getStationUnderMaintenance() {
        return stationUnderMaintenance;
    }

    public boolean getStationOutOfOrder() {
        return stationOutOforder;
    }

    public long getMillisLastUpdate() {
        return millisLastUpdate;
    }

    public long getMillisLastServerCommunication() {
        return millisLastServerCommunication;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getAvailableTerminals() {
        return availableTerminals;
    }

    public int getUnavailableTerminals() {
        return unavailableTerminals;
    }

    public int getAvailableBikes() {
        return availableBikes;
    }

    public int getUnavailableBikes() {
        return unavailableBikes;
    }

    @Override
    public String toString() {
        return String.format("«%s» --%s", id, stationName, stationId, stationState, stationIsBlocked, stationUnderMaintenance,
                stationOutOforder, millisLastUpdate, millisLastServerCommunication, lat, lng, availableTerminals,
                unavailableTerminals, availableBikes, unavailableBikes);
    }
}
