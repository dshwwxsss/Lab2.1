package javafx.controller;

import domain.MeasurementParam;
import domain.Report;
import domain.ReportLine;
import domain.ReportStatus;
import service.ReportLineService;
import service.ReportService;
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

        Double value = null;
        while (value == null) {
            String valueStr = DialogManager.showTextInput("Значение", "Введите число:", "");
            if (valueStr == null) return;
            try {
                double v = Double.parseDouble(valueStr);
                if (param == MeasurementParam.PH && (v < 0 || v > 14)) {
                    DialogManager.showAlert("Ошибка", "Значение pH должно быть в диапазоне [0, 14]");
                    continue;
                }
                if (param == MeasurementParam.CONDUCTIVITY && v < 0) {
                    DialogManager.showAlert("Ошибка", "Электропроводность не может быть отрицательной");
                    continue;
                }
                if (param == MeasurementParam.TEMPERATURE && (v < -50 || v > 1200)) {
                    DialogManager.showAlert("Ошибка", "Температура должна быть в диапазоне [-50, 1200]");
                    continue;
                }
                value = v;
            } catch (NumberFormatException e) {
                DialogManager.showAlert("Ошибка", "Неверный формат числа");
            }
        }

        String unit = null;
        while (unit == null) {
            String input = DialogManager.showTextInput("Единицы измерения", "Введите единицы:", "");
            if (input == null) return;
            if (input.isBlank()) {
                DialogManager.showAlert("Ошибка", "Единицы не могут быть пустыми");
                continue;
            }
            if (input.length() > 16) {
                DialogManager.showAlert("Ошибка", "Единицы слишком длинные (макс. 16 символов)");
                continue;
            }
            unit = input;
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

    public void updateLine(ReportLine line) {
        if (line == null) return;

        MeasurementParam param = DialogManager.showParamChoice("Выберите параметр (текущий: " + line.getParam() + ")");
        if (param == null) param = line.getParam();

        Double value = null;
        while (value == null) {
            String valueStr = DialogManager.showTextInput("Значение", "Введите число (текущее: " + line.getValue() + "):", String.valueOf(line.getValue()));
            if (valueStr == null) return;
            try {
                double v = Double.parseDouble(valueStr);
                if (param == MeasurementParam.PH && (v < 0 || v > 14)) {
                    DialogManager.showAlert("Ошибка", "Значение pH должно быть в диапазоне [0, 14]");
                    continue;
                }
                if (param == MeasurementParam.CONDUCTIVITY && v < 0) {
                    DialogManager.showAlert("Ошибка", "Электропроводность не может быть отрицательной");
                    continue;
                }
                if (param == MeasurementParam.TEMPERATURE && (v < -50 || v > 1200)) {
                    DialogManager.showAlert("Ошибка", "Температура должна быть в диапазоне [-50, 1200]");
                    continue;
                }
                value = v;
            } catch (NumberFormatException e) {
                DialogManager.showAlert("Ошибка", "Неверный формат числа");
            }
        }

        String unit = null;
        while (unit == null) {
            String input = DialogManager.showTextInput("Единицы измерения", "Введите единицы (текущие: " + line.getUnit() + "):", line.getUnit());
            if (input == null) return;
            if (input.isBlank()) {
                DialogManager.showAlert("Ошибка", "Единицы не могут быть пустыми");
                continue;
            }
            if (input.length() > 16) {
                DialogManager.showAlert("Ошибка", "Единицы слишком длинные (макс. 16 символов)");
                continue;
            }
            unit = input;
        }

        try {
            reportLineService.updateLine(line.getId(), "param", param.name());
            reportLineService.updateLine(line.getId(), "value", String.valueOf(value));
            reportLineService.updateLine(line.getId(), "unit", unit);
            DialogManager.showAlert("Успех", "Строка обновлена");
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
        sb.append("Образец ID: ").append(report.getSampleId()).append("\n\n--- Строки ---\n");
        for (ReportLine line : lines) {
            sb.append(line.getParam()).append(": ").append(line.getValue()).append(" ").append(line.getUnit()).append("\n");
        }
        DialogManager.showAlert("Детали отчёта", sb.toString());
    }
}