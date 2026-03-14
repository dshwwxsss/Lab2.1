package service;

import domain.Report;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReportService {

    private final Set<Report> reports = new HashSet<>();

    public void add(Report report) {
        reports.add(report);
    }

    public Set<Report> getAll() {
        return Collections.unmodifiableSet(reports);
    }

    public Report getById(long id) {

        for (Report r : reports) {
            if (r.getId() == id) {
                return r;
            }
        }

        return null;
    }

    public void remove(long id) {
        reports.removeIf(r -> r.getId() == id);
    }

    public void update(long id, String newName) {

        Report report = getById(id);

        if (report != null) {
            report.setName(newName);
            report.setUpdatedAt(Instant.now());
        }
    }

    public long generateId() {
        return System.currentTimeMillis() + reports.size();
    }
}