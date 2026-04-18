package javafx.controller;

import domain.Report;
import domain.Sample;
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

    public Report createReport() {
        Sample sample = DialogManager.showSampleChoice(sampleService.getSamples(), "Выберите образец");
        if (sample == null) return null;
        String name = DialogManager.showTextInput("Название отчёта", "Введите название:", "");
        if (name == null || name.trim().isEmpty()) {
            DialogManager.showAlert("Ошибка", "Название не может быть пустым или из пробелов");
            return null;
        }
        try {
            Report report = reportService.createReport(name, sample.getId(), 0L);
            DialogManager.showAlert("Успех", "Отчёт создан: ID=" + report.getId());
            return report;
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
            return null;
        }
    }

    public void editReport(Report report) {
        if (report == null) return;
        String newName = DialogManager.showTextInput("Редактирование названия", "Новое название:", report.getName());
        if (newName == null || newName.trim().isEmpty()) {
            DialogManager.showAlert("Ошибка", "Название не может быть пустым или состоять из пробелов");
            return;
        }
        report.setName(newName);
        DialogManager.showAlert("Успех", "Название изменено");
    }

    public void deleteReport(Report report) {
        if (report == null) return;
        if (!DialogManager.showConfirm("Удалить отчёт \"" + report.getName() + "\"?")) return;
        reportService.deleteReport(report.getId());
        DialogManager.showAlert("Успех", "Отчёт удалён");
    }

    public void finalizeReport(Report report) {
        if (report == null) return;
        try {
            reportService.finalizeReport(report.getId());
            DialogManager.showAlert("Успех", "Отчёт переведён в статус FINAL");
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
        }
    }

    public void signReport(Report report) {
        if (report == null) return;
        try {
            reportService.signReport(report.getId());
            DialogManager.showAlert("Успех", "Отчёт подписан (SIGNED)");
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
        }
    }
}