package railsync.model;

/**
 * Represents a train service operating across the railway network.
 */
public class Train {
    private final int trainId;
    private final String trainName;
    private final String sourceStation;
    private final String destinationStation;
    private final String departureTime;
    private final String arrivalTime;
    private final int delayMinutes;
    private final int passengerCount;
    private final String status;

    public Train(int trainId, String trainName, String sourceStation, String destinationStation,
                 String departureTime, String arrivalTime, int delayMinutes, int passengerCount, String status) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.delayMinutes = delayMinutes;
        this.passengerCount = passengerCount;
        this.status = status;
    }

    public int getTrainId() {
        return trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("%-6d | %-24s | %-24s -> %-24s | Dep: %-5s | Arr: %-5s | Delay: %3d min | Pax: %4d | %s",
                trainId, trainName, sourceStation, destinationStation,
                departureTime, arrivalTime, delayMinutes, passengerCount, status);
    }
}
