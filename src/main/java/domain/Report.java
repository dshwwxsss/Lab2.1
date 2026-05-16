package domain;

import java.time.Instant;
import java.util.Objects;

public final class Report {
    private final long id;
    private String name;
    private long sampleId;
    private long experimentId;
    private ReportStatus status;
    private String ownerUsername;
    private String signedBy;
    private Instant createdAt;
    private Instant updatedAt;

    // Конструктор для создания нового отчёта (без дат, статус DRAFT)
    public Report(long id, String name, long sampleId, long experimentId, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.sampleId = sampleId;
        this.experimentId = experimentId;
        this.ownerUsername = ownerUsername;
        this.status = ReportStatus.DRAFT;
        this.signedBy = null;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // Дополнительный конструктор для загрузки из БД (со всеми полями)
    public Report(long id, String name, long sampleId, long experimentId, String ownerUsername,
                  ReportStatus status, String signedBy, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.sampleId = sampleId;
        this.experimentId = experimentId;
        this.ownerUsername = ownerUsername;
        this.status = status;
        this.signedBy = signedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Геттеры
    public long getId() { return id; }
    public String getName() { return name; }
    public long getSampleId() { return sampleId; }
    public long getExperimentId() { return experimentId; }
    public ReportStatus getStatus() { return status; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getSignedBy() { return signedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Сеттеры
    public void setName(String name) { this.name = name; }
    public void setSampleId(long sampleId) { this.sampleId = sampleId; }
    public void setExperimentId(long experimentId) { this.experimentId = experimentId; }
    public void setStatus(ReportStatus status) { this.status = status; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    public void setSignedBy(String signedBy) { this.signedBy = signedBy; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id == report.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}