package railsync.model;

/**
 * Represents a railway station node in the RailSync network.
 */
public class Station {
    private final String stationId;
    private final String stationName;
    private final String city;
    private final int platformCount;
    private final String stationType;

    public Station(String stationId, String stationName, String city, int platformCount, String stationType) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.city = city;
        this.platformCount = platformCount;
        this.stationType = stationType;
    }

    public String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getCity() {
        return city;
    }

    public int getPlatformCount() {
        return platformCount;
    }

    public String getStationType() {
        return stationType;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-28s | City: %-12s | Platforms: %2d | Type: %s",
                stationId, stationName, city, platformCount, stationType);
    }
}
