package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    private final HttpClient api = new HttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private TableView<Equipment> equipmentTable = new TableView<>();
    private TextField eqNameField = new TextField();
    private TextField eqModelField = new TextField();
    private TextField eqSerialField = new TextField();
    private TextField eqStatusField = new TextField();
    private TextField eqLocationField = new TextField();
    private Long selectedEquipmentId = null;

    private ProgressBar importProgressBar = new ProgressBar(0);
    private Label importStatusLabel = new Label("Готово до імпорту");
    private Button importButton = new Button("Імпорт CSV");

    private TableView<Specialist> specialistTable = new TableView<>();
    private TextField spFullNameField = new TextField();
    private TextField spSpecField = new TextField();
    private TextField spPhoneField = new TextField();
    private TextField spAvailField = new TextField();
    private Long selectedSpecialistId = null;

    private TableView<ServiceRequest> requestTable = new TableView<>();
    private TextField srEquipmentIdField = new TextField();
    private TextField srDescField = new TextField();
    private TextField srPriorityField = new TextField();
    private ComboBox<ServiceRequestState> srStateCombo = new ComboBox<>();
    private TextField srDateField = new TextField();
    private Long selectedRequestId = null;

    private TableView<ServiceRecord> recordTable = new TableView<>();
    private TextField recRequestIdField = new TextField();
    private TextField recSpecialistIdField = new TextField();
    private TextField recWorkField = new TextField();
    private TextField recDateField = new TextField();
    private TextField recTimeField = new TextField();
    private Long selectedRecordId = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Курсова робота — О обладнанні");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                createEquipmentTab(),
                createSpecialistTab(),
                createServiceRequestTab(),
                createServiceRecordTab()
        );

        Scene scene = new Scene(tabPane, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshAll();
    }

    private Tab createEquipmentTab() {
        Tab tab = new Tab("Обладнання");

        TableColumn<Equipment, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Equipment, String> nameCol = new TableColumn<>("Назва");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Equipment, String> modelCol = new TableColumn<>("Модель");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        TableColumn<Equipment, String> serialCol = new TableColumn<>("Серійний №");
        serialCol.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        TableColumn<Equipment, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Equipment, String> locCol = new TableColumn<>("Локація");
        locCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        equipmentTable.getColumns().addAll(idCol, nameCol, modelCol, serialCol, statusCol, locCol);
        equipmentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedEquipmentId = selected.getId();
                eqNameField.setText(selected.getName());
                eqModelField.setText(selected.getModel());
                eqSerialField.setText(selected.getSerialNumber());
                eqStatusField.setText(selected.getStatus());
                eqLocationField.setText(selected.getLocation());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("Назва:"), 0, 0); form.add(eqNameField, 1, 0);
        form.add(new Label("Модель:"), 0, 1); form.add(eqModelField, 1, 1);
        form.add(new Label("Серійний №:"), 0, 2); form.add(eqSerialField, 1, 2);
        form.add(new Label("Статус:"), 0, 3); form.add(eqStatusField, 1, 3);
        form.add(new Label("Локація:"), 0, 4); form.add(eqLocationField, 1, 4);

        Button btnAdd = new Button("Додати");
        Button btnUpdate = new Button("Змінити");
        Button btnDelete = new Button("Видалити");
        Button btnRefresh = new Button("Оновити");

        btnAdd.setOnAction(e -> handleAddEquipment());
        btnUpdate.setOnAction(e -> handleUpdateEquipment());
        btnDelete.setOnAction(e -> handleDeleteEquipment());
        btnRefresh.setOnAction(e -> refreshEquipment());

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER_LEFT);

        importButton.setOnAction(e -> startCsvImport());
        importButton.setMaxWidth(Double.MAX_VALUE);
        importProgressBar.setPrefWidth(220);
        importStatusLabel.setMaxWidth(Double.MAX_VALUE);
        HBox importRow = new HBox(10, importButton, importProgressBar, importStatusLabel);
        importRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(importStatusLabel, Priority.ALWAYS);

        VBox left = new VBox(10, equipmentTable, buttons, importRow);
        left.setPadding(new Insets(10));
        left.setFillWidth(true);
        VBox.setVgrow(equipmentTable, Priority.ALWAYS);

        VBox right = new VBox(10, new Label("Форма обладнання"), form);
        right.setPadding(new Insets(10));

        HBox content = new HBox(10, left, right);
        content.setFillHeight(true);
        HBox.setHgrow(left, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private void handleAddEquipment() {
        try {
            Equipment e = new Equipment(
                    null,
                    eqNameField.getText(),
                    eqModelField.getText(),
                    eqSerialField.getText(),
                    eqStatusField.getText(),
                    eqLocationField.getText()
            );
            api.addEquipment(e);
            clearEquipmentForm();
            refreshEquipment();
        } catch (Exception ex) {
            showError("Помилка додавання обладнання", ex.getMessage());
        }
    }

    private void handleUpdateEquipment() {
        if (selectedEquipmentId == null) {
            showError("Виберіть запис", "Оберіть обладнання зі списку для зміни.");
            return;
        }
        try {
            Equipment e = new Equipment(
                    selectedEquipmentId,
                    eqNameField.getText(),
                    eqModelField.getText(),
                    eqSerialField.getText(),
                    eqStatusField.getText(),
                    eqLocationField.getText()
            );
            api.updateEquipment(e);
            selectedEquipmentId = null;
            clearEquipmentForm();
            refreshEquipment();
        } catch (Exception ex) {
            showError("Помилки оновлення обладнання", ex.getMessage());
        }
    }

    private void handleDeleteEquipment() {
        if (selectedEquipmentId == null) {
            showError("Виберіть запис", "Оберіть обладнання зі списку для видалення.");
            return;
        }
        try {
            api.deleteEquipment(selectedEquipmentId);
            selectedEquipmentId = null;
            clearEquipmentForm();
            refreshEquipment();
        } catch (Exception ex) {
            showError("Помилки видалення обладнання", ex.getMessage());
        }
    }

    private void startCsvImport() {
        ImportMeasurementsTask task = new ImportMeasurementsTask(api);

        importButton.setDisable(true);
        importProgressBar.progressProperty().bind(task.progressProperty());
        importStatusLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(e -> {
            int imported = task.getValue();
            importProgressBar.progressProperty().unbind();
            importStatusLabel.textProperty().unbind();
            importProgressBar.setProgress(0);
            importButton.setDisable(false);
            refreshEquipment();
            showInfo("Імпорт завершено", "Успішно імпортовано записів: " + imported
                    + "\nДані оновлено з сервера.");
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            importProgressBar.progressProperty().unbind();
            importStatusLabel.textProperty().unbind();
            importProgressBar.setProgress(0);
            importStatusLabel.setText("Помилка імпорту");
            importButton.setDisable(false);
            showError("Помилка імпорту CSV",
                    ex != null ? ex.getMessage() : "Невідома помилка");
        });

        Thread thread = new Thread(task, "csv-import");
        thread.setDaemon(true);
        thread.start();
    }

    private void clearEquipmentForm() {
        eqNameField.clear();
        eqModelField.clear();
        eqSerialField.clear();
        eqStatusField.clear();
        eqLocationField.clear();
        equipmentTable.getSelectionModel().clearSelection();
        selectedEquipmentId = null;
    }

    private void refreshEquipment() {
        try {
            List<Equipment> list = api.getEquipments();
            equipmentTable.getItems().setAll(list);
        } catch (Exception ex) {
            showError("Помилки завантаження обладнання", ex.getMessage());
        }
    }

    private Tab createSpecialistTab() {
        Tab tab = new Tab("Фахівці");

        TableColumn<Specialist, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Specialist, String> nameCol = new TableColumn<>("ПІБ");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        TableColumn<Specialist, String> specCol = new TableColumn<>("Спеціалізація");
        specCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        TableColumn<Specialist, String> phoneCol = new TableColumn<>("Телефон");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Specialist, String> availCol = new TableColumn<>("Статус");
        availCol.setCellValueFactory(new PropertyValueFactory<>("availabilityStatus"));

        specialistTable.getColumns().addAll(idCol, nameCol, specCol, phoneCol, availCol);
        specialistTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedSpecialistId = selected.getId();
                spFullNameField.setText(selected.getFullName());
                spSpecField.setText(selected.getSpecialization());
                spPhoneField.setText(selected.getPhone());
                spAvailField.setText(selected.getAvailabilityStatus());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("ПІБ:"), 0, 0); form.add(spFullNameField, 1, 0);
        form.add(new Label("Спеціалізація:"), 0, 1); form.add(spSpecField, 1, 1);
        form.add(new Label("Телефон:"), 0, 2); form.add(spPhoneField, 1, 2);
        form.add(new Label("Статус:"), 0, 3); form.add(spAvailField, 1, 3);

        Button btnAdd = new Button("Додати");
        Button btnUpdate = new Button("Змінити");
        Button btnDelete = new Button("Видалити");
        Button btnRefresh = new Button("Оновити");

        btnAdd.setOnAction(e -> handleAddSpecialist());
        btnUpdate.setOnAction(e -> handleUpdateSpecialist());
        btnDelete.setOnAction(e -> handleDeleteSpecialist());
        btnRefresh.setOnAction(e -> refreshSpecialist());

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(10, specialistTable, buttons);
        left.setPadding(new Insets(10));
        left.setFillWidth(true);
        VBox.setVgrow(specialistTable, Priority.ALWAYS);

        VBox right = new VBox(10, new Label("Форма фахівця"), form);
        right.setPadding(new Insets(10));

        HBox content = new HBox(10, left, right);
        content.setFillHeight(true);
        HBox.setHgrow(left, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private void handleAddSpecialist() {
        try {
            Specialist s = new Specialist(
                    null,
                    spFullNameField.getText(),
                    spSpecField.getText(),
                    spPhoneField.getText(),
                    spAvailField.getText()
            );
            api.addSpecialist(s);
            clearSpecialistForm();
            refreshSpecialist();
        } catch (Exception ex) {
            showError("Помилки додавання фахівця", ex.getMessage());
        }
    }

    private void handleUpdateSpecialist() {
        if (selectedSpecialistId == null) {
            showError("Виберіть запис", "Оберіть фахівця зі списку для зміни.");
            return;
        }
        try {
            Specialist s = new Specialist(
                    selectedSpecialistId,
                    spFullNameField.getText(),
                    spSpecField.getText(),
                    spPhoneField.getText(),
                    spAvailField.getText()
            );
            api.updateSpecialist(s);
            selectedSpecialistId = null;
            clearSpecialistForm();
            refreshSpecialist();
        } catch (Exception ex) {
            showError("Помилки оновлення фахівця", ex.getMessage());
        }
    }

    private void handleDeleteSpecialist() {
        if (selectedSpecialistId == null) {
            showError("Виберіть запис", "Оберіть фахівця зі списку для видалення.");
            return;
        }
        try {
            api.deleteSpecialist(selectedSpecialistId);
            selectedSpecialistId = null;
            clearSpecialistForm();
            refreshSpecialist();
        } catch (Exception ex) {
            showError("Помилки видалення фахівця", ex.getMessage());
        }
    }

    private void clearSpecialistForm() {
        spFullNameField.clear();
        spSpecField.clear();
        spPhoneField.clear();
        spAvailField.clear();
        specialistTable.getSelectionModel().clearSelection();
        selectedSpecialistId = null;
    }

    private void refreshSpecialist() {
        try {
            List<Specialist> list = api.getSpecialists();
            specialistTable.getItems().setAll(list);
        } catch (Exception ex) {
            showError("Помилки завантаження фахівців", ex.getMessage());
        }
    }

    private Tab createServiceRequestTab() {
        Tab tab = new Tab("Заявки");

        TableColumn<ServiceRequest, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ServiceRequest, Long> equipCol = new TableColumn<>("Обладнання ID");
        equipCol.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        TableColumn<ServiceRequest, String> descCol = new TableColumn<>("Опис");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<ServiceRequest, String> prioCol = new TableColumn<>("Пріоритет");
        prioCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        TableColumn<ServiceRequest, ServiceRequestState> stateCol = new TableColumn<>("Стан");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        TableColumn<ServiceRequest, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdDate"));

        requestTable.getColumns().addAll(idCol, equipCol, descCol, prioCol, stateCol, dateCol);
        requestTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedRequestId = selected.getId();
                srEquipmentIdField.setText(String.valueOf(selected.getEquipmentId()));
                srDescField.setText(selected.getDescription());
                srPriorityField.setText(selected.getPriority());
                populateAllowedStates(selected.getState());
                srDateField.setText(selected.getCreatedDate().toString());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("Обладнання ID:"), 0, 0); form.add(srEquipmentIdField, 1, 0);
        form.add(new Label("Опис:"), 0, 1); form.add(srDescField, 1, 1);
        form.add(new Label("Пріоритет:"), 0, 2); form.add(srPriorityField, 1, 2);
        form.add(new Label("Стан:"), 0, 3); form.add(srStateCombo, 1, 3);
        form.add(new Label("Дата (YYYY-MM-DD):"), 0, 4); form.add(srDateField, 1, 4);

        Button btnAdd = new Button("Додати");
        Button btnUpdate = new Button("Змінити");
        Button btnDelete = new Button("Видалити");
        Button btnRefresh = new Button("Оновити");

        btnAdd.setOnAction(e -> handleAddServiceRequest());
        btnUpdate.setOnAction(e -> handleUpdateServiceRequest());
        btnDelete.setOnAction(e -> handleDeleteServiceRequest());
        btnRefresh.setOnAction(e -> refreshServiceRequest());

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(10, requestTable, buttons);
        left.setPadding(new Insets(10));
        left.setFillWidth(true);
        VBox.setVgrow(requestTable, Priority.ALWAYS);

        VBox right = new VBox(10, new Label("Форма заявки"), form);
        right.setPadding(new Insets(10));

        HBox content = new HBox(10, left, right);
        content.setFillHeight(true);
        HBox.setHgrow(left, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private void handleAddServiceRequest() {
        try {
            ServiceRequest r = new ServiceRequest(
                    null,
                    Long.parseLong(srEquipmentIdField.getText()),
                    srDescField.getText(),
                    srPriorityField.getText(),
                    srStateCombo.getValue(),
                    java.time.LocalDate.parse(srDateField.getText())
            );
            api.addServiceRequest(r);
            clearServiceRequestForm();
            refreshServiceRequest();
        } catch (Exception ex) {
            showError("Помилки додавання заявки", ex.getMessage());
        }
    }

    private void handleUpdateServiceRequest() {
        if (selectedRequestId == null) {
            showError("Виберіть запис", "Оберіть заявку зі списку для зміни.");
            return;
        }
        try {
            ServiceRequest r = new ServiceRequest(
                    selectedRequestId,
                    Long.parseLong(srEquipmentIdField.getText()),
                    srDescField.getText(),
                    srPriorityField.getText(),
                    srStateCombo.getValue(),
                    java.time.LocalDate.parse(srDateField.getText())
            );
            api.updateServiceRequest(r);
            selectedRequestId = null;
            clearServiceRequestForm();
            refreshServiceRequest();
        } catch (Exception ex) {
            showError("Помилки оновлення заявки", ex.getMessage());
        }
    }

    private void handleDeleteServiceRequest() {
        if (selectedRequestId == null) {
            showError("Виберіть запис", "Оберіть заявку зі списку для видалення.");
            return;
        }
        try {
            api.deleteServiceRequest(selectedRequestId);
            selectedRequestId = null;
            clearServiceRequestForm();
            refreshServiceRequest();
        } catch (Exception ex) {
            showError("Помилки видалення заявки", ex.getMessage());
        }
    }

    private void clearServiceRequestForm() {
        srEquipmentIdField.clear();
        srDescField.clear();
        srPriorityField.clear();
        srStateCombo.setValue(null);
        srDateField.clear();
        requestTable.getSelectionModel().clearSelection();
        selectedRequestId = null;
    }

    private void populateAllowedStates(ServiceRequestState current) {
        srStateCombo.getItems().clear();
        switch (current) {
            case NEW -> srStateCombo.getItems().add(ServiceRequestState.DIAGNOSED);
            case DIAGNOSED -> srStateCombo.getItems().add(ServiceRequestState.IN_PROGRESS);
            case IN_PROGRESS -> srStateCombo.getItems().add(ServiceRequestState.COMPLETED);
            case COMPLETED -> {
            }
        }
        if (!srStateCombo.getItems().isEmpty()) {
            srStateCombo.setValue(srStateCombo.getItems().get(0));
        } else {
            srStateCombo.setValue(null);
        }
    }

    private void refreshServiceRequest() {
        try {
            List<ServiceRequest> list = api.getServiceRequests();
            requestTable.getItems().setAll(list);
        } catch (Exception ex) {
            showError("Помилки завантаження заявок", ex.getMessage());
        }
    }

    private Tab createServiceRecordTab() {
        Tab tab = new Tab("Сервісні записи");

        TableColumn<ServiceRecord, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ServiceRecord, Long> reqCol = new TableColumn<>("Заявка ID");
        reqCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        TableColumn<ServiceRecord, Long> specCol = new TableColumn<>("Фахівець ID");
        specCol.setCellValueFactory(new PropertyValueFactory<>("specialistId"));
        TableColumn<ServiceRecord, String> workCol = new TableColumn<>("Робота");
        workCol.setCellValueFactory(new PropertyValueFactory<>("workDescription"));
        TableColumn<ServiceRecord, String> dateCol = new TableColumn<>("Дата завершення");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("completionDate"));
        TableColumn<ServiceRecord, Integer> timeCol = new TableColumn<>("Час (хв)");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSpent"));

        recordTable.getColumns().addAll(idCol, reqCol, specCol, workCol, dateCol, timeCol);
        recordTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedRecordId = selected.getId();
                recRequestIdField.setText(String.valueOf(selected.getRequestId()));
                recSpecialistIdField.setText(String.valueOf(selected.getSpecialistId()));
                recWorkField.setText(selected.getWorkDescription());
                recDateField.setText(selected.getCompletionDate().toString());
                recTimeField.setText(String.valueOf(selected.getTimeSpent()));
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.add(new Label("Заявка ID:"), 0, 0); form.add(recRequestIdField, 1, 0);
        form.add(new Label("Фахівець ID:"), 0, 1); form.add(recSpecialistIdField, 1, 1);
        form.add(new Label("Робота:"), 0, 2); form.add(recWorkField, 1, 2);
        form.add(new Label("Дата (YYYY-MM-DD):"), 0, 3); form.add(recDateField, 1, 3);
        form.add(new Label("Час (хв):"), 0, 4); form.add(recTimeField, 1, 4);

        Button btnAdd = new Button("Додати");
        Button btnUpdate = new Button("Змінити");
        Button btnDelete = new Button("Видалити");
        Button btnRefresh = new Button("Оновити");

        btnAdd.setOnAction(e -> handleAddServiceRecord());
        btnUpdate.setOnAction(e -> handleUpdateServiceRecord());
        btnDelete.setOnAction(e -> handleDeleteServiceRecord());
        btnRefresh.setOnAction(e -> refreshServiceRecord());

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnRefresh);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(10, recordTable, buttons);
        left.setPadding(new Insets(10));
        left.setFillWidth(true);
        VBox.setVgrow(recordTable, Priority.ALWAYS);

        VBox right = new VBox(10, new Label("Форма сервісного запису"), form);
        right.setPadding(new Insets(10));

        HBox content = new HBox(10, left, right);
        content.setFillHeight(true);
        HBox.setHgrow(left, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private void handleAddServiceRecord() {
        try {
            ServiceRecord r = new ServiceRecord(
                    null,
                    Long.parseLong(recRequestIdField.getText()),
                    Long.parseLong(recSpecialistIdField.getText()),
                    recWorkField.getText(),
                    java.time.LocalDate.parse(recDateField.getText()),
                    Integer.parseInt(recTimeField.getText())
            );
            api.addServiceRecord(r);
            clearServiceRecordForm();
            refreshServiceRecord();
        } catch (Exception ex) {
            showError("Помилки додавання сервісного запису", ex.getMessage());
        }
    }

    private void handleUpdateServiceRecord() {
        if (selectedRecordId == null) {
            showError("Виберіть запис", "Оберіть сервісний запис зі списку для зміни.");
            return;
        }
        try {
            ServiceRecord r = new ServiceRecord(
                    selectedRecordId,
                    Long.parseLong(recRequestIdField.getText()),
                    Long.parseLong(recSpecialistIdField.getText()),
                    recWorkField.getText(),
                    java.time.LocalDate.parse(recDateField.getText()),
                    Integer.parseInt(recTimeField.getText())
            );
            api.updateServiceRecord(r);
            selectedRecordId = null;
            clearServiceRecordForm();
            refreshServiceRecord();
        } catch (Exception ex) {
            showError("Помилки оновлення сервісного запису", ex.getMessage());
        }
    }

    private void handleDeleteServiceRecord() {
        if (selectedRecordId == null) {
            showError("Виберіть запис", "Оберіть сервісний запис зі списку для видалення.");
            return;
        }
        try {
            api.deleteServiceRecord(selectedRecordId);
            selectedRecordId = null;
            clearServiceRecordForm();
            refreshServiceRecord();
        } catch (Exception ex) {
            showError("Помилки видалення сервісного запису", ex.getMessage());
        }
    }

    private void clearServiceRecordForm() {
        recRequestIdField.clear();
        recSpecialistIdField.clear();
        recWorkField.clear();
        recDateField.clear();
        recTimeField.clear();
        recordTable.getSelectionModel().clearSelection();
        selectedRecordId = null;
    }

    private void refreshServiceRecord() {
        try {
            List<ServiceRecord> list = api.getServiceRecords();
            recordTable.getItems().setAll(list);
        } catch (Exception ex) {
            showError("Помилки завантаження сервісних записів", ex.getMessage());
        }
    }

    private void refreshAll() {
        refreshEquipment();
        refreshSpecialist();
        refreshServiceRequest();
        refreshServiceRecord();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
