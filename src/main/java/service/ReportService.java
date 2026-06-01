package service;

import domain.Report;
import domain.ReportStatus;
import db.ReportRepository;
import validation.ReportValidator;
import validation.ValidationException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private final ReportRepository repository;
    private final SampleService sampleService;
    private final AuthService authService;
    private final Set<Report> cache = new HashSet<>();

    public ReportService(ReportRepository repository, SampleService sampleService, AuthService authService) throws SQLException {
        this.repository = repository;
        this.sampleService = sampleService;
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

    public Report createReport(String name, long sampleId, long experimentId, String ownerUsername) throws ValidationException, SQLException {
        if (sampleId != 0 && !sampleService.exists(sampleId)) {
            throw new ValidationException("Образец не найден");
        }
        Report report = new Report(0, name, sampleId, experimentId, ownerUsername, ReportStatus.DRAFT, null, Instant.now(), Instant.now());
        new ReportValidator(sampleService).validate(report);
        Report saved = repository.save(report);
        cache.add(saved);
        return saved;
    }

    public Optional<Report> getReport(long id) {
        return cache.stream().filter(r -> r.getId() == id).findFirst();
    }

    public Set<Report> getAllReports() {
        return new HashSet<>(cache);
    }

    public Set<Report> getReportsByStatus(ReportStatus status) {
        return cache.stream().filter(r -> r.getStatus() == status).collect(Collectors.toSet());
    }

    public void finalizeReport(long id, String currentUser) throws ValidationException, SQLException {
        Report report = getReport(id).orElseThrow(() -> new ValidationException("Отчёт не найден"));
        if (!authService.canModify(report.getOwnerUsername())) {
            throw new ValidationException("Нет прав на финализацию");
        }
        new ReportValidator(sampleService).validateStatusChange(report, ReportStatus.FINAL);
        report.setStatus(ReportStatus.FINAL);
        report.setUpdatedAt(Instant.now());
        repository.update(report);
        syncCache();
    }

    public void signReport(long id, String currentUser) throws ValidationException, SQLException {
        Report report = getReport(id).orElseThrow(() -> new ValidationException("Отчёт не найден"));
        if (!authService.canModify(report.getOwnerUsername())) {
            throw new ValidationException("Нет прав на подписание");
        }
        new ReportValidator(sampleService).validateStatusChange(report, ReportStatus.SIGNED);
        report.setStatus(ReportStatus.SIGNED);
        report.setSignedBy(currentUser);
        report.setUpdatedAt(Instant.now());
        repository.update(report);
        syncCache();
    }

    public void deleteReport(long id, String currentUser) throws ValidationException, SQLException {
        Report report = getReport(id).orElseThrow(() -> new ValidationException("Отчёт не найден"));
        if (!authService.canModify(report.getOwnerUsername())) {
            throw new ValidationException("Нет прав на удаление");
        }
        repository.deleteById(id);
        cache.remove(report);
    }

    public void editReportName(long id, String newName, String currentUser) throws ValidationException, SQLException {
        Report report = getReport(id).orElseThrow(() -> new ValidationException("Отчёт не найден"));
        if (!authService.canModify(report.getOwnerUsername())) {
            throw new ValidationException("Нет прав на редактирование");
        }
        if (newName == null || newName.trim().isEmpty()) throw new ValidationException("Название не может быть пустым");
        if (newName.length() > 128) throw new ValidationException("Слишком длинное название");
        report.setName(newName);
        report.setUpdatedAt(Instant.now());
        repository.update(report);
        syncCache();
    }

    public void editReportSample(long id, long newSampleId, String currentUser) throws ValidationException, SQLException {
        Report report = getReport(id).orElseThrow(() -> new ValidationException("Отчёт не найден"));
        if (!authService.canModify(report.getOwnerUsername())) {
            throw new ValidationException("Нет прав на редактирование");
        }
        if (!sampleService.exists(newSampleId)) throw new ValidationException("Образец не найден");
        report.setSampleId(newSampleId);
        report.setUpdatedAt(Instant.now());
        repository.update(report);
        syncCache();
    }
}