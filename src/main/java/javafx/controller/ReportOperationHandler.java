package javafx.controller;

import domain.Report;
import domain.ReportLine;
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

    public Report createReport() {
        Sample sample = DialogManager.showSampleChoice(sampleService.getSamples(), "Выберите образец");
        if (sample == null) return null;
        String name = DialogManager.showTextInput("Название отчёта", "Введите название:", "");
        if (name == null || name.isBlank()) {
            DialogManager.showAlert("Ошибка", "Название не может быть пустым");
            return null;
        }
        try {
            return reportService.createReport(name, sample.getId(), 0L);
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
            return null;
        }
    }

    public void editReport(Report report) {
        if (report == null) return;
        String newName = DialogManager.showTextInput("Редактирование названия", "Новое название:", report.getName());
        if (newName == null || newName.isBlank()) {
            DialogManager.showAlert("Ошибка", "Название не может быть пустым");
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

    public void exportReport(Report report, ReportLineService lineService) {
        if (report == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("=== ОТЧЁТ #").append(report.getId()).append(" ===\n");
        sb.append("Название: ").append(report.getName()).append("\n");
        sb.append("Статус: ").append(report.getStatus()).append("\n\n");
        for (ReportLine line : lineService.getLinesByReport(report.getId())) {
            sb.append(line.getParam()).append(": ").append(line.getValue()).append(" ").append(line.getUnit()).append("\n");
        }
        DialogManager.showAlert("Экспорт отчёта", sb.toString());
    }
}