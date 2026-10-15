package railsync.model;

/**
 * Represents a scheduled timetable stop of a train at a specific station.
 */
public class Schedule {
    private final String scheduleId;
    private final int trainId;
    private final String stationId;
    private final String arrivalTime;
    private final String departureTime;
    private final int platformNumber;

    public Schedule(String scheduleId, int trainId, String stationId,
                    String arrivalTime, String departureTime, int platformNumber) {
        this.scheduleId = scheduleId;
        this.trainId = trainId;
        this.stationId = stationId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.platformNumber = platformNumber;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public int getTrainId() {
        return trainId;
    }

    public String getStationId() {
        return stationId;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public int getPlatformNumber() {
        return platformNumber;
    }

    @Override
    public String toString() {
        return String.format("[%s] Train: %-6d | Station: %-8s | Arr: %-8s | Dep: %-8s | Platform: %2d",
                scheduleId, trainId, stationId, arrivalTime, departureTime, platformNumber);
    }
}
