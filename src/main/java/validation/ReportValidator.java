package validation;

import domain.Report;
import domain.ReportStatus;

public class ReportValidator {
    public void validaterNewReport(String name) {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Название не может быть пустым");
        }
    }

    public void validateLineData(Report report, String unit, double value) {
        if (report == null) {
            throw new RuntimeException("Отчёт не найден");
        }
        if (report.getStatus() != ReportStatus.NEW) {
            throw new RuntimeException("Редактирование запрещено");
        }
        if (unit == null || unit.isEmpty() || unit.length() > 16) {
            throw new RuntimeException("Ошибка в единицах измерения (должны быть от 1 до 16 символов)");
        }
        if (value < - 273.15) {
            throw new RuntimeException("Значение параметра физически невозможно");
        }
    }

    public void validateSigning(Report report) {
        if (report == null) {
            throw new RuntimeException("Отчёт не найден");
        }
        if (report.getStatus() != ReportStatus.FINAL) {
            throw new RuntimeException("Нельзя подписать отчёт, пока он не финализирован");
        }
    }
}
