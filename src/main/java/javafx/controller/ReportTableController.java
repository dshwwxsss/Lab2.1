package javafx.controller;

import cli.Environment;
import domain.Report;
import domain.ReportLine;
import domain.ReportStatus;
import domain.Sample;
import javafx.JavaFXApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.ReportLineService;
import service.ReportService;
import service.SampleService;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportTableController {
    @FXML private TableView<Report> tableView;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TextField searchField;
    @FXML private Label userLabel;
    private Environment env;
    private ReportTableViewManager tableManager;
    private ReportOperationHandler reportOps;
    private ReportLineOperationHandler lineOps;
    private ReportService reportService;
    private ReportLineService reportLineService;
    private SampleService sampleService;
    private String currentUser = "SYSTEM";
    private String currentSearchQuery = "";

    public void setEnvironment(Environment env) {
        this.reportService = env.getReportService();
        this.reportLineService = env.getReportLineService();
        this.sampleService = env.getSampleService();
        this.env = env;

        if (env.getAuthService().isAuthenticated()) {
            this.currentUser = env.getAuthService().getCurrentUsername();
            userLabel.setText("Пользователь: " + currentUser);
        }

        this.tableManager = new ReportTableViewManager(tableView, sampleService);
        this.reportOps = new ReportOperationHandler(reportService, sampleService);
        this.lineOps = new ReportLineOperationHandler(reportLineService);

        filterComboBox.getItems().addAll("Все", "DRAFT", "FINAL", "SIGNED");
        filterComboBox.setValue("Все");
        filterComboBox.setOnAction(e -> applyFilter());

        updateTableFromServices();
    }

    private void applyFilter() {
        applyFilterAndSearch();
    }

    private void applyFilterAndSearch() {
        var allReports = reportService.getAllReports();

        String selected = filterComboBox.getValue();
        ReportStatus statusFilter = (selected != null && !selected.equals("Все"))
                ? ReportStatus.valueOf(selected)
                : null;

        var filtered = allReports.stream()
                .filter(r -> statusFilter == null || r.getStatus() == statusFilter)
                .filter(r -> matchesSearch(r))
                .collect(Collectors.toSet());

        tableManager.refresh(filtered);
        statusLabel.setText("Отчётов: " + filtered.size() + " (всего: " + allReports.size() + ")");
    }

    private boolean matchesSearch(Report report) {
        if (currentSearchQuery.isEmpty()) return true;

        if (String.valueOf(report.getId()).contains(currentSearchQuery)) return true;
        if (report.getName().toLowerCase().contains(currentSearchQuery)) return true;
        String sampleName = sampleService.getSample(report.getSampleId())
                .map(Sample::getName).orElse("");
        if (sampleName.toLowerCase().contains(currentSearchQuery)) return true;
        if (report.getStatus().name().toLowerCase().contains(currentSearchQuery)) return true;
        if (report.getOwnerUsername().toLowerCase().contains(currentSearchQuery)) return true;

        return false;
    }

    @FXML private void handleSearch() {
        currentSearchQuery = searchField.getText().trim().toLowerCase();
        applyFilterAndSearch();
    }

    private void updateTableFromServices() {
        applyFilterAndSearch();
    }

    @FXML
    private void refreshTable() {
        try {
            // Перечитываем данные из базы данных
            if (progressBar != null) progressBar.setProgress(-1);
            if (statusLabel != null) statusLabel.setText("Обновление...");

            // Синхронизируем кэш с базой данных
            sampleService.syncCache();
            reportService.syncCache();
            reportLineService.syncCache();

            // Обновляем таблицу
            applyFilterAndSearch();

            if (progressBar != null) progressBar.setProgress(0);
            if (statusLabel != null) statusLabel.setText(
                    "Отчётов: " + reportService.getAllReports().size());

        } catch (Exception e) {
            DialogManager.showAlert("Ошибка обновления",
                    "Не удалось обновить данные:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void handleCreateReport() {
        Report r = reportOps.createReport(currentUser);
        if (r != null) updateTableFromServices();
    }

    @FXML private void handleAddLine() {
        lineOps.addLine(tableManager.getSelectedReport(), currentUser);
        updateTableFromServices();
    }

    @FXML private void handleUpdateLine() {
        Report report = tableManager.getSelectedReport();
        if (report == null) return;
        Set<ReportLine> lines = reportLineService.getLinesByReport(report.getId());
        if (lines.isEmpty()) {
            DialogManager.showAlert("Ошибка", "У этого отчёта нет строк");
            return;
        }
        ReportLine line = DialogManager.showLineChoice(lines, "Выберите строку для редактирования:");
        if (line == null) return;
        lineOps.updateLine(line, currentUser);
        updateTableFromServices();
    }

    @FXML private void handleShowReport() {
        lineOps.showReport(tableManager.getSelectedReport(), reportLineService);
    }

    @FXML private void handleEditReport() {
        reportOps.editReport(tableManager.getSelectedReport(), currentUser);
        updateTableFromServices();
    }

    @FXML private void handleDeleteReport() {
        reportOps.deleteReport(tableManager.getSelectedReport(), currentUser);
        updateTableFromServices();
    }

    @FXML private void handleDeleteLine() {
        lineOps.deleteLine(tableManager.getSelectedReport(), currentUser);
        updateTableFromServices();
    }

    @FXML
    private void handleCreateSample() {
        String name = DialogManager.showTextInput("Новый образец", "Название:", "");
        if (name == null || name.isBlank()) return;

        try {
            var sample = sampleService.addSample(name, currentUser);
            DialogManager.showAlert("Успех", "Образец создан: ID=" + sample.getId());
        } catch (Exception e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML private void handleFinalize() {
        reportOps.finalizeReport(tableManager.getSelectedReport(), currentUser);
        updateTableFromServices();
    }

    @FXML private void handleSign() {
        reportOps.signReport(tableManager.getSelectedReport(), currentUser);
        updateTableFromServices();
    }

    @FXML private void handleShowSamples() {
        var samples = sampleService.getSamples();
        StringBuilder sb = new StringBuilder("Список образцов:\n");
        for (var s : samples) sb.append(s.getId()).append(": ").append(s.getName()).append("\n");
        DialogManager.showAlert("Образцы", sb.toString());
    }

    @FXML private void handleLogout() {
        env.getAuthService().logout();
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();

        try {
            new JavaFXApp().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}