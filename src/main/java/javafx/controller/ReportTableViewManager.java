package javafx.controller;

import domain.Report;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import service.SampleService;

import java.util.Set;

public class ReportTableViewManager {
    private final TableView<Report> tableView;
    private final SampleService sampleService;

    public ReportTableViewManager(TableView<Report> tableView, SampleService sampleService) {
        this.tableView = tableView;
        this.sampleService = sampleService;
        initializeColumns();
    }

    private void initializeColumns() {
        // настройка колонок (id, name, sample, status, owner, createdAt)
        // аналогично коду из initializeColumns()
    }

    public void refresh(Set<Report> reports) {
        tableView.setItems(FXCollections.observableArrayList(reports));
        tableView.refresh();
    }

    public Report getSelectedReport() {
        return tableView.getSelectionModel().getSelectedItem();
    }
}