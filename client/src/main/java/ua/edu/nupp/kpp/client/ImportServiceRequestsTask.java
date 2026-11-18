package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ImportServiceRequestsTask extends Task<Integer> {

    private static final Logger LOG = Logger.getLogger(ImportServiceRequestsTask.class.getName());

    public static final Path CSV_PATH = Path.of("sample-data", "serviceRequests.csv");

    private static final String DELIMITER = ";";

    private static final int EXPECTED_COLUMNS = 7;

    private static final String EXPECTED_HEADER =
            "equipment_id;description;f1;f2;f3;state;created_date";

    private final HttpClient client;
    private final Path csvPath;

    public ImportServiceRequestsTask(HttpClient client) {
        this(client, CSV_PATH);
    }

    public ImportServiceRequestsTask(HttpClient client, Path csvPath) {
        this.client = client;
        this.csvPath = csvPath;
    }

    @Override
    protected Integer call() throws Exception {
        if (!Files.exists(csvPath)) {
            throw new IOException("Файл CSV не знайдено: " + csvPath.toAbsolutePath());
        }

        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);

        if (lines.isEmpty()) {
            throw new IOException("CSV-файл порожній: " + csvPath);
        }
        String header = lines.get(0).trim().replace("\uFEFF", "");
        if (!header.equals(EXPECTED_HEADER)) {
            throw new IOException("Неправильний заголовок CSV. Очікувано: " + EXPECTED_HEADER
                    + ", отримано: " + header);
        }

        List<String> dataLines = lines.subList(1, lines.size());
        int total = 0;
        for (String line : dataLines) {
            if (line != null && !line.isBlank()) {
                total++;
            }
        }

        int processed = 0;
        int imported = 0;
        int skippedFormat = 0;
        int failedHttp = 0;
        int skippedDuplicate = 0;

        LOG.info("Початок CSV-імпорту: " + csvPath);
        updateMessage("Імпорт: 0 з " + total + " рядків");

        for (String line : dataLines) {
            if (isCancelled()) {
                updateMessage("Імпорт скасовано. Імпортовано: " + imported);
                return imported;
            }

            try {
                if (line == null || line.isBlank()) {
                    skippedFormat++;
                    logSkip(null, "порожній рядок");
                    updateProgress(processed, total);
                    updateMessage("Імпорт: " + processed + " з " + total
                            + " (імпортовано " + imported + ", пропущено " + skippedFormat + ")");
                    continue;
                }

                processed++;

                Optional<ServiceRequest> parsed = parseRow(line);
                if (parsed.isEmpty()) {
                    skippedFormat++;
                    updateProgress(processed, total);
                    updateMessage("Імпорт: " + processed + " з " + total
                            + " (імпортовано " + imported + ", пропущено " + skippedFormat + ")");
                    continue;
                }

                client.addServiceRequest(parsed.get());
                imported++;
            } catch (Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : "";
                if (msg.contains("Дублікат")) {
                    skippedDuplicate++;
                    LOG.warning("CSV: рядок " + processed + " пропущено (дублікат): " + e.getMessage());
                } else {
                    failedHttp++;
                    LOG.warning("CSV: рядок " + processed + " відхилено сервером: " + e.getMessage());
                }
            }

            updateProgress(processed, total);
            updateMessage("Імпорт: " + processed + " з " + total
                    + " (імпортовано " + imported + ", пропущено " + skippedFormat
                    + ", дублікатів " + skippedDuplicate + ", помилок " + failedHttp + ")");
        }

        updateMessage("Імпорт завершено: імпортовано " + imported
                + " з " + total + " (пропущено " + skippedFormat + ", дублікатів " + skippedDuplicate
                + ", помилок " + failedHttp + ")");
        LOG.info("CSV-імпорт завершено: imported=" + imported + ", skippedFormat=" + skippedFormat
                + ", skippedDuplicate=" + skippedDuplicate + ", failedHttp=" + failedHttp + ", total=" + total);
        return imported;
    }

    public Optional<ServiceRequest> parseRow(String line) {
        line = line.replace("\r", "").replace("\uFEFF", "");
        String[] parts = line.split(DELIMITER, -1);

        if (parts.length != EXPECTED_COLUMNS) {
            logSkip(null, "очікувано " + EXPECTED_COLUMNS + " стовпців, отримано " + parts.length
                    + ": " + line);
            return Optional.empty();
        }

        long equipmentId;
        try {
            equipmentId = Long.parseLong(parts[0].trim());
        } catch (NumberFormatException e) {
            logSkip(null, "некоректний equipment_id: '" + parts[0] + "'");
            return Optional.empty();
        }

        String description = parts[1].trim();
        if (description.isEmpty()) {
            logSkip(null, "порожній опис: " + line);
            return Optional.empty();
        }

        double f1;
        try {
            f1 = Double.parseDouble(parts[2].trim());
        } catch (NumberFormatException e) {
            logSkip(null, "некоректне F1: '" + parts[2] + "'");
            return Optional.empty();
        }

        double f2;
        try {
            f2 = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            logSkip(null, "некоректне F2: '" + parts[3] + "'");
            return Optional.empty();
        }

        double f3;
        try {
            f3 = Double.parseDouble(parts[4].trim());
        } catch (NumberFormatException e) {
            logSkip(null, "некоректне F3: '" + parts[4] + "'");
            return Optional.empty();
        }

        ServiceRequestState state;
        try {
            state = ServiceRequestState.valueOf(parts[5].trim());
        } catch (IllegalArgumentException e) {
            logSkip(null, "некоректний стан: '" + parts[5] + "'");
            return Optional.empty();
        }

        java.time.LocalDate createdDate;
        try {
            createdDate = java.time.LocalDate.parse(parts[6].trim());
        } catch (Exception e) {
            logSkip(null, "некоректна дата: '" + parts[6] + "'");
            return Optional.empty();
        }

        return Optional.of(new ServiceRequest(
                null,
                equipmentId,
                description,
                f1,
                f2,
                f3,
                0,
                state,
                createdDate
        ));
    }

    private void logSkip(Integer rowNumber, String reason) {
        String prefix = rowNumber == null ? "CSV" : "CSV: рядок " + rowNumber;
        LOG.warning(prefix + " пропущено — " + reason);
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient();
        ImportServiceRequestsTask task = new ImportServiceRequestsTask(client);

        task.messageProperty().addListener((obs, old, msg) -> System.out.println("  [msg] " + msg));
        task.setOnSucceeded(e -> System.out.println("SUCCESS: imported=" + task.getValue()));
        task.setOnFailed(e -> System.out.println("FAILED: " + task.getException().getMessage()));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
        t.join();
    }
}
