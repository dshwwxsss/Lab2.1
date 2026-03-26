package cli.commands; //показывает подробную информацию об одном отчёте

import cli.Command;
import cli.Environment;
import validation.ValidationException;
import java.util.List;

public class RepShowCommand extends Command {
    public RepShowCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (args.size() != 1) {
            throw new ValidationException("Использование: rep_show <report_id>");
        }
        try {
            Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            throw new ValidationException("ID отчёта должен быть целым числом");
        }
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        long id = Long.parseLong(args.get(0));
        var report = env.getReportService().getReport(id)
                .orElseThrow(() -> new ValidationException("Отчёт с id=" + id + " не найден"));

        int lineCount = env.getReportLineService().getLinesByReport(id).size();
        System.out.println("Report #" + report.getId());
        System.out.println("name: " + report.getName());
        System.out.println("sampleId: " + report.getSampleId());
        System.out.println("experimentId: " + report.getExperimentId());
        System.out.println("status: " + report.getStatus());
        System.out.println("lines: " + lineCount);
    }

    @Override
    public String getHelp() {
        return "карточка отчёта";
    }
}
