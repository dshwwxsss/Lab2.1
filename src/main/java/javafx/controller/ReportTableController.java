package javafx.controller; //соединяет все, кнопки, таблицу, операции с отчетами и строками, загрузку

import cli.Environment;
import domain.ReportStatus;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.ReportLineService;
import service.ReportService;
import service.SampleService;

public class ReportTableController { //поля класса, связаны с элементом из FXML-файла
    @FXML private TableView<domain.Report> tableView;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel; //количест о отчетов
    @FXML private ComboBox<String> filterComboBox;

    private ReportTableViewManager tableManager;
    private ReportOperationHandler reportOps;
    private ReportLineOperationHandler lineOps;
    private FileOperationHandler fileOps;
    private ReportService reportService;
    private ReportLineService reportLineService;
    private SampleService sampleService;

    public void setEnvironment(Environment env) { //контейнер с сервисами из JavaFXApp
        this.reportService = env.getReportService();
        this.reportLineService = env.getReportLineService();
        this.sampleService = env.getSampleService();
//создаем помощников
        this.tableManager = new ReportTableViewManager(tableView, sampleService);
        this.reportOps = new ReportOperationHandler(reportService, sampleService);
        this.lineOps = new ReportLineOperationHandler(reportLineService);
        this.fileOps = new FileOperationHandler(sampleService, reportService, reportLineService,
                progressBar, this::updateTableFromServices); //ссылка на метод, который обновит таблицу после загрузки

        filterComboBox.getItems().addAll("Все", "DRAFT", "FINAL", "SIGNED");
        filterComboBox.setValue("Все");
        filterComboBox.setOnAction(e -> applyFilter());

        updateTableFromServices();
    }

    private void applyFilter() {
        String selected = filterComboBox.getValue();
        if (selected == null || selected.equals("Все")) tableManager.setStatusFilter(null);
        else tableManager.setStatusFilter(ReportStatus.valueOf(selected)); //превращаем строку в специальный тип
    }

    private void updateTableFromServices() {
        tableManager.refresh(reportService.getAllReports());
        statusLabel.setText("Отчётов: " + reportService.getAllReports().size());
    }

    // кнопка "Обновить" перечитывает данные из файла
    @FXML private void refreshTable() {
        fileOps.loadFromFile(); // загружаем из data.csv
    }

    // кнопка "Сохранить" записывает в файл
    @FXML private void handleSave() {
        fileOps.saveToFile();
    }

    // остальные операции
    @FXML private void handleCreateReport() {
        domain.Report r = reportOps.createReport();
        if (r != null) updateTableFromServices();
    }
    @FXML private void handleAddLine() { lineOps.addLine(tableManager.getSelectedReport()); updateTableFromServices(); }
    @FXML private void handleUpdateLine() { lineOps.updateLine(tableManager.getSelectedReport()); updateTableFromServices(); }
    @FXML private void handleShowReport() { lineOps.showReport(tableManager.getSelectedReport(), reportLineService); }
    @FXML private void handleEditReport() { reportOps.editReport(tableManager.getSelectedReport()); updateTableFromServices(); }
    @FXML private void handleDeleteReport() { reportOps.deleteReport(tableManager.getSelectedReport()); updateTableFromServices(); }
    @FXML private void handleDeleteLine() { lineOps.deleteLine(tableManager.getSelectedReport()); updateTableFromServices(); }
    @FXML private void handleFinalize() { reportOps.finalizeReport(tableManager.getSelectedReport()); updateTableFromServices(); }
    @FXML private void handleSign() { reportOps.signReport(tableManager.getSelectedReport()); updateTableFromServices(); }

    @FXML private void handleShowSamples() {
        var samples = sampleService.getSamples();
        StringBuilder sb = new StringBuilder("Список образцов:\n");
        for (var s : samples) sb.append(s.getId()).append(": ").append(s.getName()).append("\n");
        DialogManager.showAlert("Образцы", sb.toString()); //показываем диалог с этим текстом
    }
}