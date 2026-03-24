package service;

import domain.MeasurementParam;
import domain.Report;
import domain.ReportLine;
import domain.ReportStatus;
import validation.ReportLineValidator;
import validation.ValidationException;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportLineService {
    private final Set<ReportLine> lines = new HashSet<>();
    private final ReportLineValidator validator;
    private final ReportService reportService;

    public ReportLineService(ReportService reportService) {
        this.reportService = reportService;
        this.validator = new ReportLineValidator(reportService);
    }

    private long generateId() {
        return System.currentTimeMillis() + lines.size();
    }

    public ReportLine addLine(long reportId, MeasurementParam param, double value, String unit)
        throws ValidationException {
        ReportLine line = new ReportLine(generateId(), reportId, param, value, unit);
        validator.validate(line);
        lines.add(line);
        return line;
    }

    public Optional<ReportLine> getLine(long id) {
        return lines.stream()
                .filter(l -> l.getId() == id)
                .findFirst();
    }

    public Set<ReportLine> getLinesByReport(long reportId) {
        return lines.stream()
                .filter(l -> l.getReportId() == reportId)
                .collect(Collectors.toSet());
    }

    public void updateLine(long id, String field, String value) throws ValidationException {
        ReportLine line = getLine(id)
                .orElseThrow(() -> new ValidationException("Строка с id=" + id + " не найдена"));

        ReportLine updated = new ReportLine(
                line.getId(),
                line.getReportId(),
                line.getParam(),
                line.getValue(),
                line.getUnit()
        );
        updated.setUpdatedAt(Instant.now());

        switch (field) {
            case "param":
                try {
                    updated.setParam(MeasurementParam.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Неизвестный параметр. Допустимые: PH, CONDUCTIVITY, TEMPERATURE");
                }
                break;
            case "value":
                try {
                    updated.setValue(Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    throw new ValidationException("Значение должно быть числом");
                }
                break;
            case "unit":
                if (value == null || value.trim().isEmpty())
                    throw new ValidationException("Единицы не могут быть пустыми");
                updated.setUnit(value);
                break;
            default:
                throw new ValidationException("Нельзя менять поле '" + field + "'");
        }

        validator.validate(updated);
        lines.remove(line);
        lines.add(updated);
    }

    public void deleteLine(long id) throws ValidationException {
        ReportLine line = getLine(id)
                .orElseThrow(() -> new ValidationException("Строка с id=" + id + " не найдена"));

        Report report = reportService.getReport(line.getReportId())
                .orElseThrow(() -> new ValidationException("Отчёт не найден"));

        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new ValidationException("Удалять строки можно только у черновика (DRAFT)");
        }
        lines.remove(line);
    }
}
