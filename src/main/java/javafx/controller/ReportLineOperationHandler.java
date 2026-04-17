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
            reportLineService.addLine(report.getId(), param, value, unit);
            DialogManager.showAlert("Успех", "Строка добавлена");
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

    public void updateLine(Report report) {
        if (report == null) return;
        var lines = reportLineService.getLinesByReport(report.getId());
        if (lines.isEmpty()) {
            DialogManager.showAlert("Ошибка", "Нет строк для редактирования");
            return;
        }
        ReportLine oldLine = DialogManager.showLineChoice(lines, "Выберите строку:");
        if (oldLine == null) return;

        String[] options = {"Параметр", "Значение", "Единицы", "Всё"};
        String choice = DialogManager.showChoice("Что редактировать?", options);
        if (choice == null) return;

        try {
            switch (choice) {
                case "Параметр":
                    MeasurementParam newParam = DialogManager.showParamChoice("Новый параметр");
                    if (newParam != null)
                        reportLineService.updateLine(oldLine.getId(), "param", newParam.name());
                    break;
                case "Значение":
                    String newValStr = DialogManager.showTextInput("Новое значение", "Число:", String.valueOf(oldLine.getValue()));
                    if (newValStr != null)
                        reportLineService.updateLine(oldLine.getId(), "value", newValStr);
                    break;
                case "Единицы":
                    String newUnit = DialogManager.showTextInput("Новые единицы", "Единицы:", oldLine.getUnit());
                    if (newUnit != null && !newUnit.isBlank())
                        reportLineService.updateLine(oldLine.getId(), "unit", newUnit);
                    break;
                case "Всё":
                    MeasurementParam p = DialogManager.showParamChoice("Параметр");
                    if (p == null) return;
                    String vStr = DialogManager.showTextInput("Значение", "Число:", String.valueOf(oldLine.getValue()));
                    if (vStr == null) return;
                    String u = DialogManager.showTextInput("Единицы", "Единицы:", oldLine.getUnit());
                    if (u == null || u.isBlank()) return;
                    reportLineService.updateLine(oldLine.getId(), "param", p.name());
                    reportLineService.updateLine(oldLine.getId(), "value", vStr);
                    reportLineService.updateLine(oldLine.getId(), "unit", u);
                    break;
            }
            DialogManager.showAlert("Успех", "Строка обновлена");
        } catch (Exception e) {
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
        sb.append("Образец ID: ").append(report.getSampleId()).append("\n\n--- Строки ---\n");
        for (ReportLine line : lines) {
            sb.append(line.getParam()).append(": ").append(line.getValue()).append(" ").append(line.getUnit()).append("\n");
        }
        DialogManager.showAlert("Детали отчёта", sb.toString());
    }
}