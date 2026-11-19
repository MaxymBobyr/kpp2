package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ImportEquipmentTask extends Task<Integer> {

    private static final Logger LOG = Logger.getLogger(ImportEquipmentTask.class.getName());

    public static final Path CSV_PATH = Path.of("sample-data", "equipment.csv");

    private static final String DELIMITER = ";";

    private static final int EXPECTED_COLUMNS = 5;

    private static final String EXPECTED_HEADER =
            "name;model;serialNumber;status;location";

    private final HttpClient client;
    private final Path csvPath;

    public ImportEquipmentTask(HttpClient client) {
        this(client, CSV_PATH);
    }

    public ImportEquipmentTask(HttpClient client, Path csvPath) {
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

        LOG.info("Початок CSV-імпорту обладнання: " + csvPath);
        updateMessage("Імпорт обладнання: 0 з " + total + " рядків");

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
                    updateMessage("Імпорт обладнання: " + processed + " з " + total
                            + " (імпортовано " + imported + ", пропущено " + skippedFormat + ")");
                    continue;
                }

                processed++;

                Optional<Equipment> parsed = parseRow(line);
                if (parsed.isEmpty()) {
                    skippedFormat++;
                    updateProgress(processed, total);
                    updateMessage("Імпорт обладнання: " + processed + " з " + total
                            + " (імпортовано " + imported + ", пропущено " + skippedFormat + ")");
                    continue;
                }

                client.addEquipment(parsed.get());
                imported++;
            } catch (Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : "";
                if (msg.contains("Дублікат") || msg.contains("duplicate")) {
                    skippedDuplicate++;
                    LOG.warning("CSV: рядок " + processed + " пропущено (дублікат): " + e.getMessage());
                } else {
                    failedHttp++;
                    LOG.warning("CSV: рядок " + processed + " відхилено сервером: " + e.getMessage());
                }
            }

            updateProgress(processed, total);
            updateMessage("Імпорт обладнання: " + processed + " з " + total
                    + " (імпортовано " + imported + ", пропущено " + skippedFormat
                    + ", дублікатів " + skippedDuplicate + ", помилок " + failedHttp + ")");
        }

        updateMessage("Імпорт обладнання завершено: імпортовано " + imported
                + " з " + total + " (пропущено " + skippedFormat + ", дублікатів " + skippedDuplicate
                + ", помилок " + failedHttp + ")");
        LOG.info("CSV-імпорт обладнання завершено: imported=" + imported + ", skippedFormat=" + skippedFormat
                + ", skippedDuplicate=" + skippedDuplicate + ", failedHttp=" + failedHttp + ", total=" + total);
        return imported;
    }

    public Optional<Equipment> parseRow(String line) {
        line = line.replace("\r", "").replace("\uFEFF", "");
        String[] parts = line.split(DELIMITER, -1);

        if (parts.length != EXPECTED_COLUMNS) {
            logSkip(null, "очікувано " + EXPECTED_COLUMNS + " стовпців, отримано " + parts.length
                    + ": " + line);
            return Optional.empty();
        }

        String name = parts[0].trim();
        if (name.isEmpty()) {
            logSkip(null, "порожня назва: " + line);
            return Optional.empty();
        }

        String model = parts[1].trim();

        String serialNumber = parts[2].trim();
        if (serialNumber.isEmpty()) {
            logSkip(null, "порядковий номер порожній: " + line);
            return Optional.empty();
        }

        String status = parts[3].trim();
        if (status.isEmpty()) {
            logSkip(null, "статус порожній: " + line);
            return Optional.empty();
        }

        String location = parts[4].trim();
        if (location.isEmpty()) {
            logSkip(null, "локація порожня: " + line);
            return Optional.empty();
        }

        return Optional.of(new Equipment(
                null,
                name,
                model,
                serialNumber,
                status,
                location
        ));
    }

    private void logSkip(Integer rowNumber, String reason) {
        String prefix = rowNumber == null ? "CSV" : "CSV: рядок " + rowNumber;
        LOG.warning(prefix + " пропущено — " + reason);
    }
}
