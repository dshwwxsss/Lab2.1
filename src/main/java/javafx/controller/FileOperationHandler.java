package javafx.controller;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import service.ReportLineService;
import service.ReportService;
import service.SampleService;
import storage.FileStorage;

import java.io.File;

public class FileOperationHandler {
    private static final String DATA_FILE = "data.csv";
    private final SampleService sampleService;
    private final ReportService reportService;
    private final ReportLineService reportLineService;
    private final ProgressBar progressBar;
    private final Runnable updateUITask;

    public FileOperationHandler(SampleService sampleService, ReportService reportService,
                                ReportLineService reportLineService, ProgressBar progressBar,
                                Runnable updateUITask) {
        this.sampleService = sampleService;
        this.reportService = reportService;
        this.reportLineService = reportLineService;
        this.progressBar = progressBar;
        this.updateUITask = updateUITask;
    }

    // Загрузка из файла (по требованию пользователя, например из Refresh)
    public void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            DialogManager.showAlert("Загрузка", "Файл data.csv не найден.");
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(-1, 0);
                var loaded = new FileStorage().loadAll(file.getAbsolutePath());
                javafx.application.Platform.runLater(() -> {
                    sampleService.replaceAll(loaded.samples);
                    reportService.replaceAll(loaded.reports);
                    reportLineService.replaceAll(loaded.lines);
                    updateUITask.run();
                });
                return null;
            }
        };
        task.setOnFailed(e -> {
            progressBar.progressProperty().unbind();
            DialogManager.showAlert("Ошибка", task.getException().getMessage());
        });
        task.setOnSucceeded(e -> {
            progressBar.progressProperty().unbind();
            DialogManager.showAlert("Успех", "Данные загружены из " + DATA_FILE);
        });
        bindProgress(task);
        new Thread(task).start();
    }

    // Сохранение в файл
    public void saveToFile() {
        File file = new File(DATA_FILE);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(-1, 0);
                new FileStorage().saveAll(file.getAbsolutePath(),
                        sampleService.getSamples(),
                        reportService.getAllReports(),
                        reportLineService.getAllLines());
                return null;
            }
        };
        task.setOnFailed(e -> {
            progressBar.progressProperty().unbind();
            DialogManager.showAlert("Ошибка", task.getException().getMessage());
        });
        task.setOnSucceeded(e -> {
            progressBar.progressProperty().unbind();
            DialogManager.showAlert("Успех", "Данные сохранены в " + DATA_FILE);
        });
        bindProgress(task);
        new Thread(task).start();
    }

    private void bindProgress(Task<?> task) {
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> progressBar.progressProperty().unbind());
        task.setOnFailed(e -> progressBar.progressProperty().unbind());
        task.setOnCancelled(e -> progressBar.progressProperty().unbind());
    }
}