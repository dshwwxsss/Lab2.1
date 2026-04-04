package cli.command; //удаляет строку
import cli.Command;
import cli.Environment;
import validation.ValidationException;
import java.util.List;

public class RepDellineCommand extends Command {
    public RepDellineCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (args.size() != 1) {
            throw new ValidationException("Использование: rep_delline <line_id>");
        }
        try {
            Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            throw new ValidationException("ID строки должен быть целым числом");
        }
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        long lineId = Long.parseLong(args.get(0));
        env.getReportLineService().deleteLine(lineId);
        System.out.println("OK deleted");
    }

    @Override
    public String getHelp() {
        return "удалить строку";
    }
}