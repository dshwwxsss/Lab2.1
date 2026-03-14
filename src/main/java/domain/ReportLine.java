package domain;

import java.time.Instant;
import java.util.Objects;

public final class ReportLine {
    // Уникальный номер строки отчёта. Программа назначает сама.
    private long id;
    // К какому отчёту относится строка (id отчёта).
    // Должен ссылаться на реально существующий Report.
    private long reportId;
    // Параметр (PH/CONDUCTIVITY/...). Выбирается из списка MeasurementParam.
    private MeasurementParam param;
    // Значение (число).
    private double value;
    // Единицы (например "pH"). Нельзя пустое. До 16 символов.
    private String unit;
    // Когда строку добавили/обновили. Программа ставит автоматически.
    private Instant updatedAt;
    // Когда строка создана. Программа ставит автоматически.
    private Instant createdAt;

    public ReportLine(long id, long reportId, MeasurementParam param, double value, String unit, Instant updatedAt, Instant createdAt) {
        this.id = id;
        this.reportId = reportId;
        this.param = param;
        this.value = value;
        this.unit = unit;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getReportId() {
        return reportId;
    }

    public MeasurementParam getParam() {
        return param;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public void setParam(MeasurementParam param) {
        this.param = param;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReportLine line = (ReportLine) o;
        return id == line.id && reportId == line.reportId && Double.compare(value, line.value) == 0 && param == line.param && Objects.equals(unit, line.unit) && Objects.equals(updatedAt, line.updatedAt) && Objects.equals(createdAt, line.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reportId, param, value, unit, updatedAt, createdAt);
    }
}
