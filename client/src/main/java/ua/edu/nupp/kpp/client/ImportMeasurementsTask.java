package ua.edu.nupp.kpp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Початковий CSV-імпорт реєстру обладнання (Етап III, пункт 10).
 *
 * Послідовність одного рядка:
 *   CSV -> парсинг -> клієнтська модель -> HttpClient -> POST -> Servlet
 *        -> Service -> DAO -> MySQL
 *
 * На цьому етапі реалізовано лише послідовне (синхронне) читання:
 * без Task, без ProgressBar, без окремого потоку.
 */
public class ImportMeasurementsTask {

    /** Відносний шлях до CSV-файлу (пункт 10.3). */
    public static final Path CSV_PATH = Path.of("sample-data", "measurements.csv");

    /** Роздільник полів у CSV-файлі (пункт 10.5). */
    private static final String DELIMITER = ";";

    /** Очікувана кількість стовпців: equipment_id;name;model;serial_number;status;location */
    private static final int EXPECTED_COLUMNS = 6;

    private final HttpClient client;

    public ImportMeasurementsTask(HttpClient client) {
        this.client = client;
    }

    // =====================================================================
    // 10.4. Читання файлу: заголовок пропускається, береться один рядок
    // =====================================================================

    /**
     * Читає CSV-файл і повертає перший коректний рядок як клієнтську модель.
     * Заголовок ігнорується. Рядки з неправильною кількістю стовпців
     * та з помилками перетворення типів пропускаються.
     */
    public Optional<Equipment> readFirstRow(Path csvPath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            reader.readLine(); // пропускаємо заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                Optional<Equipment> parsed = parseRow(line);
                if (parsed.isPresent()) {
                    return parsed;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Читає один тестовий рядок і передає його серверу через HttpClient.
     * Повертає запис, створений на сервері (з присвоєним MySQL id).
     */
    public Equipment importFirstRow(Path csvPath) throws Exception {
        Equipment equipment = readFirstRow(csvPath)
                .orElseThrow(() -> new IllegalStateException("CSV не містить коректних рядків: " + csvPath));
        return client.addEquipment(equipment);
    }

    // =====================================================================
    // 10.5–10.7. Розділення рядка, перетворення типів, створення моделі
    // =====================================================================

    /**
     * Розділяє рядок на поля та створює клієнтську модель Equipment.
     * Повертає empty(), якщо кількість стовпців не відповідає формату
     * або значення не вдалося перетворити до потрібного типу.
     */
    public Optional<Equipment> parseRow(String line) {
        if (line == null || line.isBlank()) {
            return Optional.empty();
        }

        String[] parts = line.split(DELIMITER, -1);

        // 10.5. Перевірка кількості стовпців
        if (parts.length != EXPECTED_COLUMNS) {
            System.err.println("CSV: пропущено рядок — очікувалося " + EXPECTED_COLUMNS
                    + " стовпців, отримано " + parts.length + ": " + line);
            return Optional.empty();
        }

        try {
            // 10.6. Перетворення полів до типів
            // equipment_id → Long (nullable: id створює MySQL через AUTO_INCREMENT)
            Long id = parts[0].isBlank() ? null : Long.parseLong(parts[0].trim());

            // 10.7. Створення клієнтської моделі
            Equipment equipment = new Equipment(
                    id,
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].trim()
            );
            return Optional.of(equipment);
        } catch (NumberFormatException e) {
            System.err.println("CSV: пропущено рядок — некоректний equipment_id: " + line);
            return Optional.empty();
        }
    }

    // =====================================================================
    // 10.8. Передача моделей серверу (повний цикл по файлу)
    // =====================================================================

    /**
     * Читає весь CSV-файл і передає кожен коректний рядок серверу.
     * CSV не містить прямого JDBC-доступу — усі записи йдуть через HTTP.
     */
    public List<Equipment> importFromCsv(Path csvPath) throws Exception {
        List<Equipment> imported = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            reader.readLine(); // пропускаємо заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                Optional<Equipment> parsed = parseRow(line);
                if (parsed.isPresent()) {
                    imported.add(client.addEquipment(parsed.get()));
                }
            }
        }
        return imported;
    }

    // =====================================================================
    // Точка входу для ручної перевірки
    // =====================================================================
    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient();
        ImportMeasurementsTask task = new ImportMeasurementsTask(client);

        System.out.println("CSV-файл: " + CSV_PATH.toAbsolutePath());
        System.out.println("Розділювач: '" + DELIMITER + "', очікуваних стовпців: " + EXPECTED_COLUMNS);
        System.out.println();

        // 10.4. Один тестовий рядок
        System.out.println("--- Один тестовий рядок ---");
        task.readFirstRow(CSV_PATH).ifPresentOrElse(
                e -> System.out.println("  Прочитано: id=" + e.getId()
                        + ", name=" + e.getName()
                        + ", model=" + e.getModel()
                        + ", serial=" + e.getSerialNumber()
                        + ", status=" + e.getStatus()
                        + ", location=" + e.getLocation()),
                () -> System.out.println("  Не знайдено жодного коректного рядка")
        );

        // 10.8. Передача одного рядка серверу
        System.out.println();
        System.out.println("--- Передача рядка серверу через HTTP ---");
        Equipment created = task.importFirstRow(CSV_PATH);
        System.out.println("  Створено на сервері: id=" + created.getId()
                + ", name=" + created.getName()
                + ", serial=" + created.getSerialNumber());
    }
}
