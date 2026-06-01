package service;

import domain.*;
import db.ReportLineRepository;
import validation.ReportLineValidator;
import validation.ValidationException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportLineService {
    private final ReportLineRepository repository;
    private final ReportService reportService;
    private final AuthService authService;
    private final Set<ReportLine> cache = new HashSet<>();

    public ReportLineService(ReportLineRepository repository, ReportService reportService, AuthService authService) throws SQLException {
        this.repository = repository;
        this.reportService = reportService;
        this.authService = authService;
        loadAllFromDb();
    }

    private void loadAllFromDb() throws SQLException {
        cache.clear();
        cache.addAll(repository.findAll());
    }

    public void syncCache() throws SQLException {
        loadAllFromDb();
    }

    public ReportLine addLine(long reportId, MeasurementParam param, double value, String unit, String ownerUsername) throws ValidationException, SQLException {
        Report report = reportService.getReport(reportId).orElseThrow(() -> new ValidationException("Отчёт не найден"));
        if (report.getStatus() != ReportStatus.DRAFT) throw new ValidationException("Только черновик");
        ReportLine line = new ReportLine(0, reportId, param, value, unit, ownerUsername, Instant.now(), Instant.now());
        new ReportLineValidator(reportService).validate(line);
        ReportLine saved = repository.save(line);
        cache.add(saved);
        return saved;
    }

    public Optional<ReportLine> getLine(long id) {
        return cache.stream().filter(l -> l.getId() == id).findFirst();
    }

    public Set<ReportLine> getLinesByReport(long reportId) {
        return cache.stream().filter(l -> l.getReportId() == reportId).collect(Collectors.toSet());
    }

    public void updateLine(long id, String field, String value, String currentUser) throws ValidationException, SQLException {
        ReportLine line = getLine(id).orElseThrow(() -> new ValidationException("Строка не найдена"));
        if (!authService.canModify(line.getOwnerUsername())) {
            throw new ValidationException("Нет прав на редактирование строки");
        }
        Report report = reportService.getReport(line.getReportId()).orElseThrow(() -> new ValidationException("Отчёт не найден"));
        if (report.getStatus() != ReportStatus.DRAFT) throw new ValidationException("Только черновик");

        ReportLine updated = new ReportLine(line.getId(), line.getReportId(), line.getParam(), line.getValue(), line.getUnit(), line.getOwnerUsername(), line.getCreatedAt(), line.getUpdatedAt());
        updated.setUpdatedAt(Instant.now());

        switch (field) {
            case "param":
                updated.setParam(MeasurementParam.valueOf(value.toUpperCase()));
                break;
            case "value":
                updated.setValue(Double.parseDouble(value));
                break;
            case "unit":
                if (value == null || value.trim().isEmpty()) throw new ValidationException("Единицы не могут быть пустыми");
                updated.setUnit(value);
                break;
            default:
                throw new ValidationException("Нельзя менять поле " + field);
        }
        new ReportLineValidator(reportService).validate(updated);
        repository.update(updated);
        syncCache();
    }

    public void deleteLine(long id, String currentUser) throws ValidationException, SQLException {
        ReportLine line = getLine(id).orElseThrow(() -> new ValidationException("Строка не найдена"));
        if (!authService.canModify(line.getOwnerUsername())) {
            throw new ValidationException("Нет прав на удаление строки");
        }
        Report report = reportService.getReport(line.getReportId()).orElseThrow(() -> new ValidationException("Отчёт не найден"));
        if (report.getStatus() != ReportStatus.DRAFT) throw new ValidationException("Только черновик");
        repository.deleteById(id);
        cache.remove(line);
    }
}