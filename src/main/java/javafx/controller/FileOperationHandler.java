package javafx.controller;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import service.ReportLineService;
import service.ReportService;
import service.SampleService;
import storage.FileStorage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;

import java.io.File;

    public class FileOperationHandler {
        private final SampleService sampleService;
        private final ReportService reportService;
        private final ReportLineService reportLineService;
        private final ProgressBar progressBar;
        private final Scene scene;
        private final Runnable refreshCallback;

        public FileOperationHandler(SampleService sampleService, ReportService reportService,
                                    ReportLineService reportLineService, ProgressBar progressBar,
                                    Scene scene, Runnable refreshCallback) {
            this.sampleService = sampleService;
            this.reportService = reportService;
            this.reportLineService = reportLineService;
            this.progressBar = progressBar;
            this.scene = scene;
            this.refreshCallback = refreshCallback;
        }

        public void handleLoad() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Загрузить данные");
            File file = fileChooser.showOpenDialog(scene.getWindow());
            if (file == null) return;

            Task<Void> loadTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    updateProgress(-1, 0);
                    var loaded = new FileStorage().loadAll(file.getAbsolutePath());
                    javafx.application.Platform.runLater(() -> {
                        sampleService.replaceAll(loaded.samples);
                        reportService.replaceAll(loaded.reports);
                        reportLineService.replaceAll(loaded.lines);
                        refreshCallback.run();
                    });
                    return null;
                }
            };
            loadTask.setOnFailed(e -> DialogManager.showAlert("Ошибка загрузки", loadTask.getException().getMessage()));
            loadTask.setOnSucceeded(e -> DialogManager.showAlert("Успех", "Загружено из " + file.getName()));
            bindProgress(loadTask);
            new Thread(loadTask).start();
        }

        public void handleSave() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить данные");
            File file = fileChooser.showSaveDialog(scene.getWindow());
            if (file == null) return;

            Task<Void> saveTask = new Task<>() {
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
            saveTask.setOnFailed(e -> DialogManager.showAlert("Ошибка сохранения", saveTask.getException().getMessage()));
            saveTask.setOnSucceeded(e -> DialogManager.showAlert("Успех", "Сохранено в " + file.getName()));
            bindProgress(saveTask);
            new Thread(saveTask).start();
        }

        private void bindProgress(Task<?> task) {
            progressBar.progressProperty().bind(task.progressProperty());
            task.setOnFailed(e -> progressBar.progressProperty().unbind());
        }
    }