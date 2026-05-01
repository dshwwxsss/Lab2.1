package domain;

import java.time.Instant;
import java.util.Objects;

public final class ReportLine {
    private final long id;
    private long reportId;
    private MeasurementParam param;
    private double value;
    private String unit;
    private String ownerUsername;
    private Instant updatedAt;
    private Instant createdAt;

    public ReportLine(long id, long reportId, MeasurementParam param, double value, String unit, String ownerUsername) {
        this.id = id;
        this.reportId = reportId;
        this.param = param;
        this.value = value;
        this.unit = unit;
        this.ownerUsername = ownerUsername;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // Геттеры
    public long getId() { return id; }
    public long getReportId() { return reportId; }
    public MeasurementParam getParam() { return param; }
    public double getValue() { return value; }
    public String getUnit() { return unit; }
    public String getOwnerUsername() { return ownerUsername; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Сеттеры
    public void setReportId(long reportId) { this.reportId = reportId; }
    public void setParam(MeasurementParam param) { this.param = param; }
    public void setValue(double value) { this.value = value; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReportLine line = (ReportLine) o;
        return id == line.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}