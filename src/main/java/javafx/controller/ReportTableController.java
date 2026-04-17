package javafx.controller;

import cli.Environment;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.ReportLineService;
import service.ReportService;
import service.SampleService;

public class ReportTableController {
    @FXML private TableView<domain.Report> tableView;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    private ReportTableViewManager tableManager;
    private ReportOperationHandler reportOps;
    private ReportLineOperationHandler lineOps;
    private FileOperationHandler fileOps;
    private ReportService reportService;
    private ReportLineService reportLineService;

    public void setEnvironment(Environment env) {
        this.reportService = env.getReportService();
        this.reportLineService = env.getReportLineService();
        SampleService sampleService = env.getSampleService();

        this.tableManager = new ReportTableViewManager(tableView, sampleService);
        this.reportOps = new ReportOperationHandler(reportService, sampleService);
        this.lineOps = new ReportLineOperationHandler(reportLineService);
        this.fileOps = new FileOperationHandler(sampleService, reportService, reportLineService,
                progressBar, tableView.getScene(), this::refreshTable);

        refreshTable();
    }

    @FXML private void refreshTable() {
        tableManager.refresh(reportService.getAllReports());
        statusLabel.setText("Отчётов: " + reportService.getAllReports().size());
    }

    @FXML private void handleLoad() { fileOps.handleLoad(); }
    @FXML private void handleSave() { fileOps.handleSave(); }
    @FXML private void handleCreateReport() {
        domain.Report r = reportOps.createReport();
        if (r != null) refreshTable();
    }
    @FXML private void handleAddLine() { lineOps.addLine(tableManager.getSelectedReport()); }
    @FXML private void handleShowReport() { lineOps.showReport(tableManager.getSelectedReport(), reportLineService); }
    @FXML private void handleEditReport() { reportOps.editReport(tableManager.getSelectedReport()); refreshTable(); }
    @FXML private void handleDeleteReport() { reportOps.deleteReport(tableManager.getSelectedReport()); refreshTable(); }
    @FXML private void handleDeleteLine() { lineOps.deleteLine(tableManager.getSelectedReport()); refreshTable(); }
    @FXML private void handleFinalize() { reportOps.finalizeReport(tableManager.getSelectedReport()); refreshTable(); }
    @FXML private void handleSign() { reportOps.signReport(tableManager.getSelectedReport()); refreshTable(); }
    @FXML private void handleExport() { reportOps.exportReport(tableManager.getSelectedReport(), reportLineService); }
}