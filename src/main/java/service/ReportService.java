package service;

import domain.Report;
import domain.ReportStatus;
import validation.ReportValidator;
import validation.ValidationException;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportService {
    private final Set<Report> reports = new HashSet<>();
    private final ReportValidator validator;

    public ReportService(SampleService sampleService) {
        this.validator = new ReportValidator(sampleService);
    }
    private long generateId() {
        return System.currentTimeMillis() + reports.size();
    }

    public Report createReport(String name, long sampleId, long experimentId) throws ValidationException {
        Report report = new Report(generateId(), name, sampleId, experimentId, "SYSTEM");
        validator.validate(report);
        reports.add(report);
        return report;
    }

    public Optional<Report> getReport(long id) {
        return reports.stream()
                .filter(r -> r.getId() == id)
                .findFirst();
    }

    public Set<Report> getAllReports() {
        return new HashSet<>(reports);
    }

    public Set<Report> getReportsByStatus(ReportStatus status) {
        return reports.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toSet());
    }

    public void finalizeReport(long id) throws ValidationException {
        Report report = getReport(id)
                .orElseThrow(() -> new ValidationException("Отчёт с id=" + id + " не найден"));
        validator.validateStatusChange(report, ReportStatus.FINAL);
        report.setStatus(ReportStatus.FINAL);
        report.setUpdatedAt(Instant.now());
        // обновление в Set
        reports.remove(report);
        reports.add(report);
    }

    public void signReport(long id) throws ValidationException {
        Report report = getReport(id)
                .orElseThrow(() -> new ValidationException("Отчёт с id=" + id + " не найден"));
        validator.validateStatusChange(report, ReportStatus.SIGNED);
        report.setStatus(ReportStatus.SIGNED);
        report.setSignedBy("SYSTEM");
        report.setUpdatedAt(Instant.now());
    }
}