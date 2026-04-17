package javafx.controller;

import domain.MeasurementParam;
import domain.Report;
import domain.ReportLine;
import domain.ReportStatus;
import service.ReportLineService;
import validation.ValidationException;

public class ReportLineOperationHandler {
    private final ReportLineService reportLineService;

    public ReportLineOperationHandler(ReportLineService reportLineService) {
        this.reportLineService = reportLineService;
    }

    public void addLine(Report report) {
        if (report == null) return;
        if (report.getStatus() != ReportStatus.DRAFT) {
            DialogManager.showAlert("Ошибка", "Строки можно добавлять только в черновик (DRAFT)");
            return;
        }
        MeasurementParam param = DialogManager.showParamChoice("Выберите параметр");
        if (param == null) return;
        String valueStr = DialogManager.showTextInput("Значение", "Введите число:", "");
        if (valueStr == null) return;
        double value;
        try {
            value = Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            DialogManager.showAlert("Ошибка", "Неверный формат числа");
            return;
        }
        String unit = DialogManager.showTextInput("Единицы измерения", "Введите единицы:", "");
        if (unit == null || unit.isBlank()) {
            DialogManager.showAlert("Ошибка", "Единицы не могут быть пустыми");
            return;
        }
        try {
            ReportLine line = reportLineService.addLine(report.getId(), param, value, unit);
            DialogManager.showAlert("Успех", "Строка добавлена: ID=" + line.getId());
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
        }
    }

    public void deleteLine(Report report) {
        if (report == null) return;
        var lines = reportLineService.getLinesByReport(report.getId());
        if (lines.isEmpty()) {
            DialogManager.showAlert("Ошибка", "У этого отчёта нет строк");
            return;
        }
        ReportLine line = DialogManager.showLineChoice(lines, "Выберите строку для удаления:");
        if (line == null) return;
        try {
            reportLineService.deleteLine(line.getId());
            DialogManager.showAlert("Успех", "Строка удалена");
        } catch (ValidationException e) {
            DialogManager.showAlert("Ошибка", e.getMessage());
        }
    }

    public void showReport(Report report, ReportLineService lineService) {
        if (report == null) return;
        var lines = lineService.getLinesByReport(report.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("Отчёт #").append(report.getId()).append("\n\n");
        sb.append("Название: ").append(report.getName()).append("\n");
        sb.append("Статус: ").append(report.getStatus()).append("\n");
        sb.append("Образец: ").append(report.getSampleId()).append("\n");
        sb.append("Автор: ").append(report.getOwnerUsername()).append("\n");
        sb.append("\n--- Строки отчёта (").append(lines.size()).append(") ---\n");
        for (ReportLine line : lines) {
            sb.append(line.getParam()).append(": ").append(line.getValue()).append(" ").append(line.getUnit()).append("\n");
        }
        DialogManager.showAlert("Детали отчёта", sb.toString());
    }
}