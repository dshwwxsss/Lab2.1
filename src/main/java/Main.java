import domain.Report;
import domain.ReportStatus;
import service.ReportService;

import java.time.Instant;

public class Main {
    public static void main(String[] args){
        ReportService reportService = new ReportService();
        long id = reportService.generateId();
        Report report = new Report(
                id,
                "Test report",
                1,
                0,
                ReportStatus.DRAFT,
                "SYSTEM",
                null,
                Instant.now(),
                Instant.now());

    }
}