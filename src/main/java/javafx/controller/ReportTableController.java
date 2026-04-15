package javafx.controller;

import cli.Environment;
import domain.Report;
import domain.Sample;
import javafx.JavaFXApp;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.ReportService;
import service.SampleService;
import javafx.scene.control.TextInputDialog;

public class ReportTableController {

    private Environment env;
    private ReportService reportService;
    private SampleService sampleService;

    // Связываем элементы из FXML
    @FXML
    private TableView<Report> tableView;
    @FXML
    private TableColumn<Report, Long> idColumn;
    @FXML
    private TableColumn<Report, String> nameColumn;
    @FXML
    private TableColumn<Report, String> sampleColumn;
    @FXML
    private TableColumn<Report, String> statusColumn;
    @FXML
    private ProgressBar progressBar;

    // Конструктор (вызывается автоматически при загрузке FXML)
    public void initialize() {
        // Инициализация колонок таблицы
        initializeColumns();
    }

    // Метод для установки окружения (сервисов)
    public void setEnvironment(Environment env) {
        this.env = env;
        this.reportService = env.getReportService();
        this.sampleService = env.getSampleService();
        refreshTable();
    }

    // Настройка колонок таблицы
    private void initializeColumns() {
        idColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        nameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        sampleColumn.setCellValueFactory(data -> {
            long sampleId = data.getValue().getSampleId();
            String sampleName = sampleService.getSample(sampleId)  // ← правильный метод
                    .map(Sample::getName)
                    .orElse("Sample #" + sampleId);  // если не найден — покажем ID
            return new javafx.beans.property.SimpleStringProperty(sampleName);
        });

        statusColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatus().name()));
    }

    // === Методы для кнопок из FXML ===

    // Кнопка Refresh
    @FXML
    private void refreshTable() {
        if (reportService != null) {
            Platform.runLater(() -> {
                // Показываем неопределённый прогресс
                progressBar.setProgress(-1);

                // Обновляем таблицу
                tableView.setItems(FXCollections.observableArrayList(
                        reportService.getAllReports()
                ));

                // Скрываем прогресс после загрузки
                progressBar.setProgress(0);
            });
        }
    }

    // Кнопка Add
    @FXML
    private void handleAdd() {
        // Диалог ввода названия отчёта
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Новый отчёт");
        dialog.setHeaderText("Создание отчёта");
        dialog.setContentText("Название отчёта:");

        // Когда пользователь нажал ОК
        dialog.showAndWait().ifPresent(name -> {
            try {
                // Берём первый доступный образец (для простоты)
                long sampleId = sampleService.getSamples().stream()
                        .findFirst()
                        .map(Sample::getId)
                        .orElse(1L);

                // Создаём отчёт через сервис
                reportService.createReport(name, sampleId, 0L);

                // Обновляем таблицу
                refreshTable();

                showAlert("Success", "Отчёт \"" + name + "\" создан");

            } catch (Exception e) {
                // Ошибку показываем в диалоге
                showAlert("Ошибка", e.getMessage());
            }
        });
    }

    // Кнопка Edit
    @FXML
    private void handleEdit() {
        Report selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Выберите отчёт для редактирования");
            return;
        }

        // Диалог с текущим названием
        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Редактирование отчёта");
        dialog.setHeaderText("Изменение названия");
        dialog.setContentText("Новое название:");

        dialog.showAndWait().ifPresent(newName -> {
            if (newName.isBlank()) {
                showAlert("Ошибка", "Название не может быть пустым");
                return;
            }

            try {
                // Обновляем название через сервис
                selected.setName(newName);

                // Обновляем таблицу
                refreshTable();

                showAlert("Успех", "Отчёт обновлён");
                // Форсируем полную перезагрузку таблицы
                tableView.refresh();

            } catch (Exception e) {
                showAlert("Ошибка", e.getMessage());
            }
        });
    }

    // Кнопка Delete
    @FXML
    private void handleDelete() {
        Report selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Выберите отчёт для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить отчёт \"" + selected.getName() + "\"?");
        var result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            reportService.deleteReport(selected.getId());
            refreshTable();
            showAlert("Success", "Отчёт удалён");
        }
    }
    // Кнопка Save
    @FXML
    private void handleSave() {
        try {
            storage.FileStorage fileStorage = new storage.FileStorage();
            fileStorage.saveAll(JavaFXApp.DATA_FILE,
                    env.getSampleService().getSamples(),
                    env.getReportService().getAllReports(),
                    env.getReportLineService().getAllLines());

            showAlert("Успех", "Данные сохранены в " + JavaFXApp.DATA_FILE);

        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось сохранить: " + e.getMessage());
        }
    }

    // Вспомогательный метод для показа сообщений
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}