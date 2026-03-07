package domain;

import java.util.*;

public class ReportService {
    private Set<Report> reports = new HashSet<>();
    private Set<ReportLine> lines = new HashSet<>();
    private long reportCounter = 1;
    private long lineCounter = 1;

    private ReportValidator validator = new ReportValidator();

    public long createSampleReport(String name, long sampleId) {
        validator.validaterNewReport(name);
        Report r = new Report(reportCounter++, name, sampleId, "SYSTEM");
        reports.add(r);
        return r.getId();
    }

    public long addLine(long reportId, String paramStr, double value, String unit) {
        Report r = findReport(reportId);
        validator.validateLineData(r, unit, value);
        MeasurementParam p;
        try {
            p = MeasurementParam.valueOf(paramStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Параметр ~" + paramStr + "~ не поддерживается системой");
        }
        ReportLine line = new ReportLine(lineCounter++, reportId, p, value, unit);
        lines.add(line);
        return line.getId();
    }
    public Report findReport(long id) {
        for (Report r : reports) {
            if (r.getId() == id) return r;
        }
        return null;
    }
    public void finalizeReport(long id){
        Report r = findReport(id);
        if (r != null) {
            r.setStatus(ReportStatus.FINAL);
        } else {
            throw new RuntimeException("Отчёт с ID " + id + " не существует");
        }
    }
    public void signReport(long id, String user) {
        Report r = findReport(id);
        validator.validateSigning(r);
        r.setStatus(ReportStatus.SIGNED);
        r.setSignedBy(user);
    }
    public Collection<Report> getAllReports() {
        return reports;
    }

    public void printLines(long reportId) {
        for (ReportLine l : lines) {
            if (l.getReportId() == reportId)
                System.out.println(l.getId() + "|" + l.getReportId() + "|" + l.getValue());
        }
    }
}
