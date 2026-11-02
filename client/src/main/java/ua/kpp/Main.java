package ua.kpp;

import ua.kpp.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Спроба підключення до бази даних...");

        try (Connection connection = DatabaseConnection.getConnection()) {

            if (connection != null && !connection.isClosed()) {
                System.out.println("=========================================");
                System.out.println("✅ УСПІХ! Підключення до MySQL встановлено.");
                System.out.println("База даних: " + connection.getCatalog());
                System.out.println("Версія MySQL: " + connection.getMetaData().getDatabaseProductVersion());
                System.out.println("=========================================");
            } else {
                System.out.println("❌ Не вдалося встановити з'єднання.");
            }

        } catch (SQLException e) {
            System.err.println("❌ ПОМИЛКА ПІДКЛЮЧЕННЯ!");
            System.err.println("Текст помилки: " + e.getMessage());
            System.err.println("\nПеревірте:");
            System.err.println("1. Чи запущений сервер MySQL (Apache та MySQL у XAMPP/OpenServer).");
            System.err.println("2. Чи правильно вказано логін та пароль у DatabaseConnection.java.");
            System.err.println("3. Чи створена база даних 'equipment_db'.");
        }
    }
}