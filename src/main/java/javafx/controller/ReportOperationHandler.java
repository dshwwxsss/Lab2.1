package javafx.controller;

import domain.Report;
import domain.Sample;
import service.ReportLineService;
import service.ReportService;
import service.SampleService;
import validation.ValidationException;

public class ReportOperationHandler {
    private final ReportService reportService;
    private final SampleService sampleService;

    public ReportOperationHandler(ReportService reportService, SampleService sampleService) {
        this.reportService = reportService;
        this.sampleService = sampleService;
    }

    public Report createReport(String currentUser) {
        Sample sample = DialogManager.showSampleChoice(sampleService.getSamples(), "Выберите образец");
        if (sample == null) return null;

        String name = DialogManager.showTextInput("Название отчёта", "Введите название:", "");
        if (name == null || name.trim().isEmpty()) {
            DialogManager.showAlert("Ошибка", "Название не может быть пустым");
            return null;
        }

        try {
            Report report = reportService.createReport(name, sample.getId(), 0L, currentUser);
            DialogManager.showAlert("Успех", "Отчёт создан: ID=" + report.getId());
            return report;
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
            return null;
        }
    }

    public void editReport(Report report, String currentUser) {
        if (report == null) return;
        String newName = DialogManager.showTextInput("Редактирование названия", "Новое название:", report.getName());
        if (newName == null || newName.trim().isEmpty()) {
            DialogManager.showAlert("Ошибка", "Название не может быть пустым");
            return;
        }
        try {
            reportService.editReportName(report.getId(), newName, currentUser);
            DialogManager.showAlert("Успех", "Название изменено");
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
        }
    }

    public void deleteReport(Report report, String currentUser) {
        if (report == null) return;
        if (!DialogManager.showConfirm("Удалить отчёт \"" + report.getName() + "\"?")) return;
        try {
            reportService.deleteReport(report.getId(), currentUser);
            DialogManager.showAlert("Успех", "Отчёт удалён");
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
        }
    }

    public void finalizeReport(Report report, String currentUser) {
        if (report == null) return;
        try {
            reportService.finalizeReport(report.getId(), currentUser);
            DialogManager.showAlert("Успех", "Отчёт переведён в статус FINAL");
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
        }
    }

    public void signReport(Report report, String currentUser) {
        if (report == null) return;
        try {
            reportService.signReport(report.getId(), currentUser);
            DialogManager.showAlert("Успех", "Отчёт подписан (SIGNED)");
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
        }
    }

    public void exportReport(Report report, ReportLineService lineService) {
        if (report == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("=== ОТЧЁТ #").append(report.getId()).append(" ===\n");
        sb.append("Название: ").append(report.getName()).append("\n");
        sb.append("Статус: ").append(report.getStatus()).append("\n");
        sb.append("Образец ID: ").append(report.getSampleId()).append("\n\n--- Строки ---\n");
        for (var line : lineService.getLinesByReport(report.getId())) {
            sb.append(line.getParam()).append(": ").append(line.getValue()).append(" ").append(line.getUnit()).append("\n");
        }
        DialogManager.showAlert("Экспорт отчёта", sb.toString());
    }
}