package railsync.model;

/**
 * Represents a live or recorded railway operational alert.
 */
public class ServiceAlert {
    private final String alertId;
    private final int trainId;
    private final String stationName;
    private final String alertType;
    private final String description;
    private final String severity;

    public ServiceAlert(String alertId, int trainId, String stationName,
                        String alertType, String description, String severity) {
        this.alertId = alertId;
        this.trainId = trainId;
        this.stationName = stationName;
        this.alertType = alertType;
        this.description = description;
        this.severity = severity;
    }

    public String getAlertId() {
        return alertId;
    }

    public int getTrainId() {
        return trainId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return String.format("[%s] Severity: %-8s | Train: %-6d | Station: %-22s | Type: %-18s\n       Details: %s",
                alertId, severity, trainId, stationName, alertType, description);
    }
}
