package domain;

import java.time.Instant;
import java.util.Objects;

public class Report {
    private long id;
    private String name;
    private long sampleId;
    private ReportStatus status;
    private String ownerUsername;
    private String signedBy;
    private Instant createdAt;

    public Report(long id, String name, long sampleId, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.sampleId = sampleId;
        this.ownerUsername = ownerUsername;
        this.status = ReportStatus.NEW;
        this.createdAt = Instant.now();
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getSignedBy() {
        return signedBy;
    }

    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }
    public ReportStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id == report.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
