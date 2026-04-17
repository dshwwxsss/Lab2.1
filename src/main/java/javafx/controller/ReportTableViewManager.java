package javafx.controller;

import domain.Report;
import domain.ReportStatus;
import domain.Sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import service.SampleService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class ReportTableViewManager {
    private final TableView<Report> tableView;
    private final SampleService sampleService;
    private ObservableList<Report> masterData;
    private FilteredList<Report> filteredData;

    public ReportTableViewManager(TableView<Report> tableView, SampleService sampleService) {
        this.tableView = tableView;
        this.sampleService = sampleService;
        initializeColumns();
        masterData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(masterData, p -> true);
        tableView.setItems(filteredData);
        tableView.setStyle("-fx-text-fill: black; -fx-control-inner-background: white;");
    }

    private void initializeColumns() {
        TableColumn<Report, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Report, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Report, String> sampleCol = new TableColumn<>("Образец");
        sampleCol.setCellValueFactory(data -> {
            long sid = data.getValue().getSampleId();
            String sampleName = sampleService.getSample(sid).map(Sample::getName).orElse("Образец #" + sid);
            return new javafx.beans.property.SimpleStringProperty(sampleName);
        });

        TableColumn<Report, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().name()));

        TableColumn<Report, String> ownerCol = new TableColumn<>("Автор");
        ownerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getOwnerUsername()));

        TableColumn<Report, String> createdCol = new TableColumn<>("Создан");
        createdCol.setCellValueFactory(data -> {
            var createdAt = data.getValue().getCreatedAt();
            String formatted = "";
            if (createdAt != null) {
                ZonedDateTime localTime = createdAt.atZone(ZoneId.of("UTC"))
                        .withZoneSameInstant(ZoneId.systemDefault());
                formatted = localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        tableView.getColumns().setAll(idCol, nameCol, sampleCol, statusCol, ownerCol, createdCol);
    }

    public void refresh(Set<Report> reports) {
        masterData.setAll(reports);
        tableView.refresh();
    }

    public void setStatusFilter(ReportStatus status) {
        if (status == null) {
            filteredData.setPredicate(report -> true);
        } else {
            filteredData.setPredicate(report -> report.getStatus() == status);
        }
    }

    public Report getSelectedReport() {
        return tableView.getSelectionModel().getSelectedItem();
    }
}