package javafx.controller;

import cli.Environment;
import domain.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import service.ReportService;
import service.SampleService;
import service.ReportLineService;
import validation.ValidationException;

import java.io.File;
import java.util.Set;

public class ReportTableController {

    private ReportService reportService;
    private SampleService sampleService;
    private ReportLineService reportLineService;

    @FXML private TableView<Report> tableView;
    @FXML private TableColumn<Report, Long> idColumn;
    @FXML private TableColumn<Report, String> nameColumn;
    @FXML private TableColumn<Report, String> sampleColumn;
    @FXML private TableColumn<Report, String> statusColumn;
    @FXML private TableColumn<Report, String> ownerColumn;
    @FXML private TableColumn<Report, String> createdAtColumn;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    public void initialize() {
        initializeColumns();
    }

    public void setEnvironment(Environment env) {
        this.reportService = env.getReportService();
        this.sampleService = env.getSampleService();
        this.reportLineService = env.getReportLineService();
        refreshTable();
    }

    private void initializeColumns() {
        idColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));
        nameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        sampleColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        sampleService.getSample(data.getValue().getSampleId())
                                .map(Sample::getName)
                                .orElse("Sample #" + data.getValue().getSampleId())));
        statusColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().name()));
        ownerColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getOwnerUsername()));
        createdAtColumn.setCellValueFactory(data -> {
            var createdAt = data.getValue().getCreatedAt();
            String formatted = "";
            if (createdAt != null) {
                formatted = createdAt.toString()
                        .replace("T", " ")
                        .replaceAll("\\..*", "");
            }
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
    }

    @FXML private void refreshTable() {
        Platform.runLater(() -> {
            if (progressBar != null) progressBar.setProgress(-1);
            tableView.setItems(FXCollections.observableArrayList(reportService.getAllReports()));
            tableView.refresh();
            if (progressBar != null) progressBar.setProgress(0);
            if (statusLabel != null) statusLabel.setText("Отчётов: " + reportService.getAllReports().size());
        });
    }

    @FXML private void handleLoad() {
        File file = new FileChooser().showOpenDialog(tableView.getScene().getWindow());
        if (file == null) return;
        try {
            var loaded = new storage.FileStorage().loadAll(file.getAbsolutePath());
            sampleService.replaceAll(loaded.samples);
            reportService.replaceAll(loaded.reports);
            reportLineService.replaceAll(loaded.lines);
            refreshTable();
            showAlert("Успех", "Загружено: " + loaded.reports.size() + " отчётов");
        } catch (Exception e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML private void handleSave() {
        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(tableView.getScene().getWindow());
        if (file == null) return;
        try {
            new storage.FileStorage().saveAll(file.getAbsolutePath(),
                    sampleService.getSamples(), reportService.getAllReports(), reportLineService.getAllLines());
            showAlert("Успех", "Сохранено в " + file.getName());
        } catch (Exception e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML private void handleCreateReport() {
        var sample = showSampleChoice("Выберите образец:");
        if (sample == null) return;
        var name = showTextInput("Название отчёта:", "Введите название:");
        if (name == null || name.isBlank()) { showAlert("Ошибка", "Название не может быть пустым"); return; }
        try {
            Report report = reportService.createReport(name, sample.getId(), 0L);
            long id = report.getId();
            refreshTable();
            showAlert("Успех", "Отчёт создан: ID=" + id);
        } catch (ValidationException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML private void handleAddLine() {
        Report report = getSelectedReport();
        if (report == null) return;
        if (report.getStatus() != ReportStatus.DRAFT) { showAlert("Ошибка", "Только DRAFT"); return; }

        var param = showParamChoice("Параметр:");
        if (param == null) return;
        var valueStr = showTextInput("Значение:", "Введите число:");
        if (valueStr == null) return;
        double value;
        try { value = Double.parseDouble(valueStr); }
        catch (NumberFormatException e) { showAlert("Ошибка", "Не число"); return; }

        var unit = showTextInput("Единицы:", "Введите единицы:");
        if (unit == null || unit.isBlank()) { showAlert("Ошибка", "Единицы не пустые"); return; }

        try {
            ReportLine line = reportLineService.addLine(report.getId(), param, value, unit);
            long id = line.getId();
            refreshTable();
            showAlert("Успех", "Строка добавлена: ID=" + id);
        } catch (ValidationException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML private void handleShowReport() {
        Report r = getSelectedReport();
        if (r == null) return;

        var lines = reportLineService.getLinesByReport(r.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("=== ОТЧЁТ #" + r.getId() + " ===\n\n");
        sb.append("Название: ").append(r.getName()).append("\n");
        sb.append("Статус: ").append(r.getStatus()).append("\n");
        sb.append("Образец ID: ").append(r.getSampleId()).append("\n");
        sb.append("Автор: ").append(r.getOwnerUsername()).append("\n");
        sb.append("Создан: ").append(r.getCreatedAt() != null ?
                r.getCreatedAt().toString().replace("T", " ").replaceAll("\\..*", "") : "").append("\n");
        sb.append("\n--- СТРОКИ ОТЧЁТА (" + lines.size() + ") ---\n");

        if (lines.isEmpty()) {
            sb.append("Нет строк\n");
        } else {
            sb.append(String.format("%-20s %-12s %-10s\n", "Параметр", "Значение", "Единицы"));
            sb.append("-".repeat(45)).append("\n");
            for (var line : lines) {
                sb.append(String.format("%-20s %-12.2f %-10s\n",
                        line.getParam(), line.getValue(), line.getUnit()));
            }
        }

        showAlert("Отчёт #" + r.getId(), sb.toString());
    }

    @FXML private void handleShowLines() {
        handleShowReport();  // Теперь это то же самое
    }

    @FXML private void handleEditReport() {
        Report r = getSelectedReport();
        if (r == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Редактирование отчёта");
        alert.setHeaderText("Что изменить?");
        alert.setContentText("Можно изменить только название отчёта.\n\nТекущее название: " + r.getName());

        var result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        var name = showTextInput("Новое название:", r.getName());
        if (name == null || name.isBlank()) {
            showAlert("Ошибка", "Название не может быть пустым");
            return;
        }

        try {
            r.setName(name);
            refreshTable();
            showAlert("Успех", "Отчёт обновлён");
        } catch (Exception e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML private void handleDeleteReport() {
        Report r = getSelectedReport();
        if (r == null) return;
        if (!showConfirm("Удалить отчёт \"" + r.getName() + "\"?")) return;
        try { reportService.deleteReport(r.getId()); refreshTable(); showAlert("Успех", "Удалён"); }
        catch (Exception e) { showAlert("Ошибка", e.getMessage()); }
    }

    @FXML private void handleDeleteLine() {
        Report report = getSelectedReport();
        if (report == null) return;
        Set<ReportLine> lines = reportLineService.getLinesByReport(report.getId());
        if (lines.isEmpty()) {
            showAlert("Ошибка", "У этого отчёта нет строк");
            return;
        }
        ReportLine line = showLineChoice(lines, "Выберите строку для удаления:");
        if (line == null) return;
        try {
            reportLineService.deleteLine(line.getId());
            refreshTable();
            showAlert("Успех", "Строка удалена");
        } catch (ValidationException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML private void handleFinalize() {
        Report r = getSelectedReport();
        if (r == null) return;
        try { reportService.finalizeReport(r.getId()); refreshTable(); showAlert("Успех", "FINAL"); }
        catch (ValidationException e) { showAlert("Ошибка", e.getMessage()); }
    }

    @FXML private void handleSign() {
        Report r = getSelectedReport();
        if (r == null) return;
        try { reportService.signReport(r.getId()); refreshTable(); showAlert("Успех", "SIGNED"); }
        catch (ValidationException e) { showAlert("Ошибка", e.getMessage()); }
    }

    @FXML private void handleExport() {
        Report r = getSelectedReport();
        if (r == null) return;
        StringBuilder sb = new StringBuilder("=== ОТЧЁТ #" + r.getId() + " ===\n");
        sb.append("Название: ").append(r.getName()).append("\n");
        sb.append("Статус: ").append(r.getStatus()).append("\n");
        for (ReportLine l : reportLineService.getLinesByReport(r.getId()))
            sb.append(l.getParam()).append(": ").append(l.getValue()).append(" ").append(l.getUnit()).append("\n");
        showAlert("Экспорт", sb.toString());
    }

    // === Вспомогательные методы UI ===
    private Report getSelectedReport() {
        Report r = tableView.getSelectionModel().getSelectedItem();
        if (r == null) showAlert("Ошибка", "Выберите отчёт");
        return r;
    }

    private Sample showSampleChoice(String msg) {
        var d = new ChoiceDialog<>(sampleService.getSamples().stream().findFirst().orElse(null), sampleService.getSamples());
        d.setHeaderText(msg);
        return d.showAndWait().orElse(null);
    }

    private MeasurementParam showParamChoice(String msg) {
        var d = new ChoiceDialog<>(MeasurementParam.PH, MeasurementParam.values());
        d.setHeaderText(msg);
        return d.showAndWait().orElse(null);
    }

    private ReportLine showLineChoice(Set<ReportLine> lines, String msg) {
        var d = new ChoiceDialog<>(lines.stream().findFirst().orElse(null), lines);
        d.setHeaderText(msg);
        return d.showAndWait().orElse(null);
    }

    private String showTextInput(String header, String content) {
        var d = new TextInputDialog();
        d.setHeaderText(header);
        d.setContentText(content);
        return d.showAndWait().orElse(null);
    }

    private boolean showConfirm(String msg) {
        var d = new Alert(Alert.AlertType.CONFIRMATION, msg);
        return d.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setTitle(title);
        a.setHeaderText(null);
        a.initOwner(tableView.getScene().getWindow());
        a.showAndWait();
    }
}