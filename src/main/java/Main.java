import domain.Report;
import service.ReportService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ReportService service = new ReportService();

        while (true) {
            try {
                System.out.print("> ");
                String cmd = sc.next();
                if (cmd.equals("rep_create_sample")) {
                    long sId = sc.nextLong();
                    sc.nextLine();
                    System.out.print("Название: ");
                    String name = sc.nextLine();
                    long id = service.createSampleReport(name, sId);
                    System.out.println("ОК report_id=" + id);
                }
                else if (cmd.equals("rep_addLine")) {
                    long rId = sc.nextLong();
                    sc.nextLine();
                    System.out.print("Параметр: ");
                    String p = sc.next();
                    System.out.print("Значение: ");
                    double v = sc.nextDouble();
                    System.out.print("Единицы: ");
                    String u = sc.next();
                    System.out.println("ОК line_id=" +service.addLine(rId, p, v, u));
                }
                else if (cmd.equals("rep_list")) {
                    for (Report r : service.getAllReports())
                        System.out.println(r.getId() + " " + r.getName() + " " + r.getStatus());
                }
                else if (cmd.equals("rep_finalize")) {
                    service.finalizeReport(sc.nextLong());
                    System.out.println("ОК");
                }
                else if (cmd.equals("exit")) break;
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}
