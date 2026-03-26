package cli.commands; //показывает все строки отчёта

import cli.Command;
import cli.Environment;
import validation.ValidationException;
import java.util.List;

public class RepLinesCommand extends Command {
    public RepLinesCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (args.size() != 1) {
            throw new ValidationException("Использование: rep_lines <report_id>");
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
        if (env.getReportService().getReport(id).isEmpty()) {
            throw new ValidationException("Отчёт с id=" + id + " не найден");
        }
        var lines = env.getReportLineService().getLinesByReport(id);
        System.out.println("ID\tПараметр\tЗначение\tЕдиницы");
        for (var l : lines) {
            System.out.println(l.getId() + "\t" + l.getParam() + "\t" + l.getValue() + "\t" + l.getUnit());
        }
    }

    @Override
    public String getHelp() {
        return "строки отчёта";
    }
}