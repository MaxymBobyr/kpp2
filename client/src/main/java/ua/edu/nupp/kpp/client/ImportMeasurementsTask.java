package ua.edu.nupp.kpp.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportMeasurementsTask {

    private final HttpClient client;

    public ImportMeasurementsTask(HttpClient client) {
        this.client = client;
    }

    public List<String> importFromCsv(String csvPath) throws Exception {
        List<String> results = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 6) {
                    continue;
                }
                String json = "{" +
                        "\"name\":\"" + escape(parts[1]) + "\"," +
                        "\"model\":\"" + escape(parts[2]) + "\"," +
                        "\"serialNumber\":\"" + escape(parts[3]) + "\"," +
                        "\"status\":\"" + escape(parts[4]) + "\"," +
                        "\"location\":\"" + escape(parts[5]) + "\"" +
                        "}";
                String response = client.post("/equipment", json);
                results.add(response);
            }
        }
        return results;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient("http://localhost:8080");
        ImportMeasurementsTask task = new ImportMeasurementsTask(client);
        List<String> results = task.importFromCsv("sample-data/measurements.csv");
        for (String r : results) {
            System.out.println(r);
        }
    }
}
