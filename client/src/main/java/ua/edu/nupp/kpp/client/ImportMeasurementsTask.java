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

public class ImportMeasurementsTask extends Task<Integer> {

    private static final Logger LOG = Logger.getLogger(ImportMeasurementsTask.class.getName());

    public static final Path CSV_PATH = Path.of("sample-data", "measurements.csv");

    private static final String DELIMITER = ";";

    private static final int EXPECTED_COLUMNS = 6;

    private static final String EXPECTED_HEADER =
            "equipment_id;name;model;serial_number;status;location";

    private final HttpClient client;
    private final Path csvPath;

    public ImportMeasurementsTask(HttpClient client) {
        this(client, CSV_PATH);
    }

    public ImportMeasurementsTask(HttpClient client, Path csvPath) {
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
        String header = lines.get(0).trim();
        if (!header.equals(EXPECTED_HEADER)) {
            throw new IOException("Неправильний заголовок CSV. Очікувано: " + EXPECTED_HEADER
                    + ", отримано: " + header);
        }

        List<String> dataLines = lines.subList(1, lines.size());
        int total = dataLines.size();

        int processed = 0;
        int imported = 0;
        int skippedFormat = 0;
        int failedHttp = 0;

        updateMessage("Імпорт: 0 з " + total + " рядків");

        for (String line : dataLines) {
            if (isCancelled()) {
                updateMessage("Імпорт скасовано. Імпортовано: " + imported);
                return imported;
            }

            processed++;

            if (line == null || line.isBlank()) {
                skippedFormat++;
                logSkip(processed, "порожній рядок");
                updateProgress(processed, total);
                updateMessage("Імпорт: " + processed + " з " + total
                        + " (імпортовано " + imported + ", пропущено " + skippedFormat + ")");
                continue;
            }

            Optional<Equipment> parsed = parseRow(line);
            if (parsed.isEmpty()) {
                skippedFormat++;
                updateProgress(processed, total);
                updateMessage("Імпорт: " + processed + " з " + total
                        + " (імпортовано " + imported + ", пропущено " + skippedFormat + ")");
                continue;
            }

            try {
                client.addEquipment(parsed.get());
                imported++;
            } catch (Exception e) {
                failedHttp++;
                LOG.warning("CSV: рядок " + processed + " відхилено сервером: " + e.getMessage());
            }

            updateProgress(processed, total);
            updateMessage("Імпорт: " + processed + " з " + total
                    + " (імпортовано " + imported + ", пропущено " + skippedFormat
                    + ", помилок " + failedHttp + ")");
        }

        updateMessage("Імпорт завершено: імпортовано " + imported
                + " з " + total + " (пропущено " + skippedFormat + ", помилок " + failedHttp + ")");
        LOG.info("CSV-імпорт завершено: imported=" + imported + ", skippedFormat=" + skippedFormat
                + ", failedHttp=" + failedHttp + ", total=" + total);
        return imported;
    }

    public Optional<Equipment> parseRow(String line) {
        String[] parts = line.split(DELIMITER, -1);

        if (parts.length != EXPECTED_COLUMNS) {
            logSkip(null, "очікувано " + EXPECTED_COLUMNS + " стовпців, отримано " + parts.length
                    + ": " + line);
            return Optional.empty();
        }

        try {
            Long id = parts[0].isBlank() ? null : Long.parseLong(parts[0].trim());
            return Optional.of(new Equipment(
                    id,
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim()
            ));
        } catch (NumberFormatException e) {
            logSkip(null, "некоректний equipment_id: " + line);
            return Optional.empty();
        }
    }

    private void logSkip(Integer rowNumber, String reason) {
        String prefix = rowNumber == null ? "CSV" : "CSV: рядок " + rowNumber;
        LOG.warning(prefix + " пропущено — " + reason);
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient();
        ImportMeasurementsTask task = new ImportMeasurementsTask(client);

        task.messageProperty().addListener((obs, old, msg) -> System.out.println("  [msg] " + msg));
        task.setOnSucceeded(e -> System.out.println("SUCCESS: imported=" + task.getValue()));
        task.setOnFailed(e -> System.out.println("FAILED: " + task.getException().getMessage()));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
        t.join();
    }
}
