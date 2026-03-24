package cli;

import service.ReportLineService;
import service.ReportService;
import service.SampleService;

import java.util.Scanner;

public class Environment {
    private final SampleService sampleService;
    private final ReportService reportService;
    private final ReportLineService reportLineService;
    private final Scanner scanner;

    public Environment(SampleService sampleService, ReportService reportService, ReportLineService reportLineService, Scanner scanner) {
        this.sampleService = sampleService;
        this.reportService = reportService;
        this.reportLineService = reportLineService;
        this.scanner = scanner;
    }

    public SampleService getSampleService() {
        return sampleService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public ReportLineService getReportLineService() {
        return reportLineService;
    }

    public Scanner getScanner() {
        return scanner;
    }
}
