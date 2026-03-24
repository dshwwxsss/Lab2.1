package validation;

import domain.MeasurementParam;
import domain.Report;
import domain.ReportLine;
import domain.ReportStatus;
import service.ReportService;

import java.util.Optional;

public class ReportLineValidator {
    private final ReportService reportManager;

    public ReportLineValidator(ReportService reportManager) {
        this.reportManager = reportManager;
    }

    public void validate(ReportLine line) throws ValidationException {
        if (line.getParam() == null) {
            throw new ValidationException("Параметр не может быть пустым");
        }
        if (Double.isNaN(line.getValue()) || Double.isInfinite(line.getValue())) {
            throw new ValidationException("Значение должно быть конечным числом");
        }
        validateRange(line.getParam(), line.getValue());

        if (line.getUnit() == null || line.getUnit().trim().isEmpty()) {
            throw new ValidationException("Единицы измерения не могут быть пустыми");
        }
        if (line.getUnit().length() > 16) {
            throw new ValidationException("Единицы измерения слишком длинные (макс. 16 символов)");
        }

        Optional<Report> opt = reportManager.getReport(line.getReportId());
        if (opt.isEmpty()) {
            throw new ValidationException("Отчёт с id=" + line.getReportId() + " не найден");
        }
        Report report = opt.get();

        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new ValidationException("Изменять строки можно только у черновика (DRAFT)");
        }
    }

    private void validateRange(MeasurementParam param, double value) throws ValidationException {
        switch (param) {
            case PH:
                if (value < 0.0 || value > 14.0) {
                    throw new ValidationException("Значение pH должно быть в диапазоне [0, 14]");
                }
                break;
            case CONDUCTIVITY:
                if (value < 0.0) {
                    throw new ValidationException("Электропроводность не может быть отрицательной");
                }
                break;
            case TEMPERATURE:
                if (value < -50.0 || value > 1200.0) {
                    throw new ValidationException("Температура должна быть в диапазоне [-50, 1200]");
                }
                break;
        }
    }
}
