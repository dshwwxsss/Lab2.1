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

    public Report createReport(String name, long sampleId, long experimentId, String ownerUsername) throws ValidationException {
        Report report = new Report(generateId(), name, sampleId, experimentId, ownerUsername);
        validator.validate(report);
        reports.add(report);
        return report;
    }

    public Optional<Report> getReport(long id) {
        return reports.stream().filter(r -> r.getId() == id).findFirst();
    }

    public Set<Report> getAllReports() {
        return new HashSet<>(reports);
    }

    public Set<Report> getReportsByStatus(ReportStatus status) {
        return reports.stream().filter(r -> r.getStatus() == status).collect(Collectors.toSet());
    }

    public void finalizeReport(long id, String currentUser) throws ValidationException {
        Report report = getReport(id)
                .orElseThrow(() -> new ValidationException("Отчёт с id=" + id + " не найден"));

        if (!report.getOwnerUsername().equals(currentUser)) {
            throw new ValidationException("Ошибка: у вас нет прав на финализацию этого отчёта");
        }

        validator.validateStatusChange(report, ReportStatus.FINAL);
        report.setStatus(ReportStatus.FINAL);
        report.setUpdatedAt(Instant.now());
        reports.remove(report);
        reports.add(report);
    }

    public void signReport(long id, String currentUser) throws ValidationException {
        Report report = getReport(id)
                .orElseThrow(() -> new ValidationException("Отчёт с id=" + id + " не найден"));

        if (!report.getOwnerUsername().equals(currentUser)) {
            throw new ValidationException("Ошибка: у вас нет прав на подписание этого отчёта");
        }

        validator.validateStatusChange(report, ReportStatus.SIGNED);
        report.setStatus(ReportStatus.SIGNED);
        report.setSignedBy(currentUser);
        report.setUpdatedAt(Instant.now());
    }

    public void deleteReport(long id, String currentUser) throws ValidationException {
        Report report = getReport(id)
                .orElseThrow(() -> new ValidationException("Отчёт с id=" + id + " не найден"));

        if (!report.getOwnerUsername().equals(currentUser)) {
            throw new ValidationException("Ошибка: у вас нет прав на удаление этого отчёта");
        }

        reports.remove(report);
    }

    public void editReportName(long id, String newName, String currentUser) throws ValidationException {
        Report report = getReport(id)
                .orElseThrow(() -> new ValidationException("Отчёт с id=" + id + " не найден"));

        if (!report.getOwnerUsername().equals(currentUser)) {
            throw new ValidationException("Ошибка: у вас нет прав на редактирование этого отчёта");
        }

        if (newName == null || newName.trim().isEmpty()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (newName.length() > 128) {
            throw new ValidationException("Название слишком длинное (макс. 128 символов)");
        }

        report.setName(newName);
        report.setUpdatedAt(Instant.now());
    }

    public void replaceAll(Set<Report> newReports) {
        reports.clear();
        reports.addAll(newReports);
    }
}