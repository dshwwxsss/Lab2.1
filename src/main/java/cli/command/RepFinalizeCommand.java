package cli.command; //переводит отчёт в статус FINAL

import cli.Command;
import cli.Environment;
import validation.ValidationException;
import java.util.List;

public class RepFinalizeCommand extends Command {
    public RepFinalizeCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (args.size() != 1) {
            throw new ValidationException("Использование: rep_finalize <report_id>");
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
        env.getReportService().finalizeReport(id);
        System.out.println("OK report " + id + " FINAL");
    }

    @Override
    public String getHelp() {
        return "перевести отчёт в FINAL";
    }
}