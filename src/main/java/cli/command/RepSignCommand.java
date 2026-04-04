package cli.command; //подписывает отчёт (статус SIGNED)

import cli.Command;
import cli.Environment;
import validation.ValidationException;
import java.util.List;

public class RepSignCommand extends Command {
    public RepSignCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (args.size() != 1) {
            throw new ValidationException("Использование: rep_sign <report_id>");
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
        env.getReportService().signReport(id);
        System.out.println("OK report " + id + " SIGNED by SYSTEM");
    }

    @Override
    public String getHelp() {
        return "подписать отчёт (SIGNED)";
    }
}