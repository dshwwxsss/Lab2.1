package command; //показывает список отчётов, можно отфильтровать по статусу

import cli.Command;
import cli.Environment;
import domain.ReportStatus;
import validation.ValidationException;
import java.util.List;

public class RepListCommand extends Command {
    public RepListCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (!args.isEmpty() && !(args.size() == 2 && args.get(0).equals("--status"))) {
            throw new ValidationException("Использование: rep_list [--status DRAFT|FINAL|SIGNED]");
        }
        if (args.size() == 2) {
            try {
                ReportStatus.valueOf(args.get(1).toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Неизвестный статус. Используйте DRAFT, FINAL или SIGNED");
            }
        }
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        ReportStatus filter = null;
        if (args.size() == 2 && args.get(0).equals("--status")) {
            filter = ReportStatus.valueOf(args.get(1).toUpperCase());
        }

        var reports = (filter == null)
                ? env.getReportService().getAllReports()
                : env.getReportService().getReportsByStatus(filter);

        System.out.println("ID\tНазвание\tСтатус");
        for (var r : reports) {
            System.out.println(r.getId() + "\t" + r.getName() + "\t" + r.getStatus());
        }
    }

    @Override
    public String getHelp() {
        return "список отчётов (с фильтром по статусу)";
    }
}