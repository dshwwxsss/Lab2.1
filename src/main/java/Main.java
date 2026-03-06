import domain.Report;
import domain.ReportStatus;

public class Main {
    public static void main(String[] args) {
        Report myReport = new Report(1, "Лабораторная работа №1", ReportStatus.NEW);
        System.out.println("Отчёт успешно создан");
        System.out.println("ID отчёта: " + myReport.getId());
        System.out.println("Название: " + myReport.getName());
        System.out.println("Статус: " + myReport.getStatus());
        System.out.println("Проверка пройдена");
    }
}
