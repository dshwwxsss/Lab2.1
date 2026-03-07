package domain;

import java.util.Objects;

public class ReportLine {
    private long id;
    private long reportId;
    private MeasurementParam param;
    private double value;
    private String unit;

    public ReportLine(long id, long reportId, MeasurementParam param, double value, String unit) {
        this.id = id;
        this.reportId = reportId;
        this.param = param;
        this.value = value;
        this.unit = unit;
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

    public void setParam(MeasurementParam param) {
        this.param = param;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportLine that = (ReportLine) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
