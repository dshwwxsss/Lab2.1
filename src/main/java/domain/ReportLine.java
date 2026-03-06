package domain;

public class ReportLine {
    private long id;
    private String param;
    private double value;
    private String unit;

    public ReportLine(long id, String param, double value, String unit) {
        this.id = id;
        this.param = param;
        this.value = value;
        this.unit = unit;
    }

    public String getParam() { return param; }
    public double getValue() { return value; }
    public String getUnit() { return unit; }
}
