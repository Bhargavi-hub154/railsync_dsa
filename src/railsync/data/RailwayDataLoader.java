package railsync.data;

import railsync.model.Schedule;
import railsync.model.ServiceAlert;
import railsync.model.Station;
import railsync.model.Train;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles robust loading of railway CSV and TXT dataset files.
 * Designed to seamlessly find the 'data' directory in Eclipse IDE or command-line runs.
 */
public class RailwayDataLoader {

    public static class RailwayDataset {
        private final File dataDirectory;
        private final List<Station> stations;
        private final List<Train> trains;
        private final List<Schedule> schedules;
        private final List<ServiceAlert> alerts;
        private final String railwayDocument;

        public RailwayDataset(File dataDirectory, List<Station> stations, List<Train> trains,
                              List<Schedule> schedules, List<ServiceAlert> alerts, String railwayDocument) {
            this.dataDirectory = dataDirectory;
            this.stations = stations;
            this.trains = trains;
            this.schedules = schedules;
            this.alerts = alerts;
            this.railwayDocument = railwayDocument;
        }

        public File getDataDirectory() {
            return dataDirectory;
        }

        public List<Station> getStations() {
            return stations;
        }

        public List<Train> getTrains() {
            return trains;
        }

        public List<Schedule> getSchedules() {
            return schedules;
        }

        public List<ServiceAlert> getAlerts() {
            return alerts;
        }

        public String getRailwayDocument() {
            return railwayDocument;
        }
    }

    /**
     * Resolves the active data directory by checking common Eclipse and terminal execution paths.
     *
     * @param customPath user-specified directory path, or null/empty to auto-detect.
     * @return a valid File pointing to the data folder.
     * @throws IOException if no valid data directory containing trains.csv could be found.
     */
    public static File resolveDataDirectory(String customPath) throws IOException {
        List<File> candidates = new ArrayList<>();

        if (customPath != null && !customPath.trim().isEmpty()) {
            candidates.add(new File(customPath.trim()));
        }

        // Standard locations for Eclipse, command line, and test environments
        candidates.add(new File("data"));
        candidates.add(new File("RailSync/data"));
        candidates.add(new File("../data"));
        candidates.add(new File("../../data"));

        String sysProp = System.getProperty("railsync.data.dir");
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            candidates.add(new File(sysProp.trim()));
        }

        for (File candidate : candidates) {
            if (candidate.exists() && candidate.isDirectory()) {
                File testFile = new File(candidate, "trains.csv");
                if (testFile.exists() && testFile.isFile()) {
                    return candidate.getCanonicalFile();
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Could not locate railway data directory. Checked candidate locations:\n");
        for (File c : candidates) {
            sb.append("  - ").append(c.getAbsolutePath()).append("\n");
        }
        sb.append("Current Working Directory is: ").append(new File(".").getAbsolutePath()).append("\n");
        sb.append("Ensure the 'data' folder containing trains.csv, stations.csv, etc., is present.");
        throw new IOException(sb.toString());
    }

    /**
     * Loads the entire railway dataset from the resolved data directory.
     */
    public static RailwayDataset loadAll(String customPath) throws IOException {
        File dataDir = resolveDataDirectory(customPath);

        List<Station> stations = loadStations(new File(dataDir, "stations.csv"));
        List<Train> trains = loadTrains(new File(dataDir, "trains.csv"));
        List<Schedule> schedules = loadSchedules(new File(dataDir, "schedules.csv"));
        List<ServiceAlert> alerts = loadAlerts(new File(dataDir, "alerts.csv"));
        String document = loadDocument(new File(dataDir, "railway_documents.txt"));

        return new RailwayDataset(dataDir, stations, trains, schedules, alerts, document);
    }

    public static List<Station> loadStations(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("Missing required file: " + file.getAbsolutePath());
        }
        List<Station> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || lineNum == 1 && line.toLowerCase().startsWith("station_id")) {
                    continue; // Skip empty or header
                }
                String[] parts = parseCsvLine(line);
                if (parts.length < 5) {
                    System.err.println("[WARN] Skipping malformed line " + lineNum + " in " + file.getName());
                    continue;
                }
                try {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String city = parts[2].trim();
                    int platformCount = Integer.parseInt(parts[3].trim());
                    String type = parts[4].trim();
                    list.add(new Station(id, name, city, platformCount, type));
                } catch (NumberFormatException e) {
                    System.err.println("[WARN] Number parse error at line " + lineNum + " in " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        return list;
    }

    public static List<Train> loadTrains(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("Missing required file: " + file.getAbsolutePath());
        }
        List<Train> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || lineNum == 1 && line.toLowerCase().startsWith("train_id")) {
                    continue;
                }
                String[] parts = parseCsvLine(line);
                if (parts.length < 9) {
                    System.err.println("[WARN] Skipping malformed line " + lineNum + " in " + file.getName());
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String src = parts[2].trim();
                    String dst = parts[3].trim();
                    String dep = parts[4].trim();
                    String arr = parts[5].trim();
                    int delay = Integer.parseInt(parts[6].trim());
                    int pax = Integer.parseInt(parts[7].trim());
                    String status = parts[8].trim();
                    list.add(new Train(id, name, src, dst, dep, arr, delay, pax, status));
                } catch (NumberFormatException e) {
                    System.err.println("[WARN] Number parse error at line " + lineNum + " in " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        return list;
    }

    public static List<Schedule> loadSchedules(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("Missing required file: " + file.getAbsolutePath());
        }
        List<Schedule> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || lineNum == 1 && line.toLowerCase().startsWith("schedule_id")) {
                    continue;
                }
                String[] parts = parseCsvLine(line);
                if (parts.length < 6) {
                    System.err.println("[WARN] Skipping malformed line " + lineNum + " in " + file.getName());
                    continue;
                }
                try {
                    String schId = parts[0].trim();
                    int trainId = Integer.parseInt(parts[1].trim());
                    String stnId = parts[2].trim();
                    String arr = parts[3].trim();
                    String dep = parts[4].trim();
                    int platform = Integer.parseInt(parts[5].trim());
                    list.add(new Schedule(schId, trainId, stnId, arr, dep, platform));
                } catch (NumberFormatException e) {
                    System.err.println("[WARN] Number parse error at line " + lineNum + " in " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        return list;
    }

    public static List<ServiceAlert> loadAlerts(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("Missing required file: " + file.getAbsolutePath());
        }
        List<ServiceAlert> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || lineNum == 1 && line.toLowerCase().startsWith("alert_id")) {
                    continue;
                }
                String[] parts = parseCsvLine(line);
                if (parts.length < 6) {
                    System.err.println("[WARN] Skipping malformed line " + lineNum + " in " + file.getName());
                    continue;
                }
                try {
                    String alertId = parts[0].trim();
                    int trainId = Integer.parseInt(parts[1].trim());
                    String stnName = parts[2].trim();
                    String type = parts[3].trim();
                    String desc = parts[4].trim();
                    String severity = parts[5].trim();
                    list.add(new ServiceAlert(alertId, trainId, stnName, type, desc, severity));
                } catch (NumberFormatException e) {
                    System.err.println("[WARN] Number parse error at line " + lineNum + " in " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        return list;
    }

    public static String loadDocument(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("Missing required file: " + file.getAbsolutePath());
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Splits a CSV line by comma, respecting quoted fields if present.
     */
    public static String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());

        String[] result = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            String val = tokens.get(i).trim();
            if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
                val = val.substring(1, val.length() - 1);
            }
            result[i] = val;
        }
        return result;
    }
}
