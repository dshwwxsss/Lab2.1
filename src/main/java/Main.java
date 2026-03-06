import domain.Report;
import domain.ReportLine;
import domain.ReportStatus;

public class Main {
    public static void main(String[] args) {
        Report report = new Report(1, "Лабораторная работа №1", ReportStatus.IN_PROGRESS);
        ReportLine line = new ReportLine(101, "Кислотность", 7.4, "pH");
        System.out.println("Отчёт: " + report.getName() + "[" + report.getStatus() + "]");
        System.out.println("Данные: " + line.getParam() + " = " + line.getValue() + " " + line.getUnit());
    }
}
