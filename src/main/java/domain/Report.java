package domain;

import java.time.Instant;

public class Report {
    private long id;
    private String name;
    private ReportStatus status;
    private Instant createdAt;

    private List<ReportLine> lines = new ArrayList<>();

    public Report(long id, String name, ReportStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public void addLine(ReportLine line) {
        return lines;
    }

    public List<ReportLine> getLines() {
        return lines;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public ReportStatus getStatus() { return status; }
}
