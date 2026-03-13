import domain.Report;
import service.ReportService;

import java.time.Instant;

public class Main {
    public static void main(String[] args){
        ReportService reportService = new ReportService();
        Report report = new Report(0L, Instant.now());

    }
}