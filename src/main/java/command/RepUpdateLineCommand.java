package command; //изменяет строку

import cli.Command;
import cli.Environment;
import validation.ValidationException;
import java.util.List;

public class RepUpdateLineCommand extends Command {
    public RepUpdateLineCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (args.size() < 2) {
            throw new ValidationException("Использование: rep_updateline <line_id> field=value ...");
        }
        try {
            Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            throw new ValidationException("ID строки должен быть целым числом");
        }
        for (int i = 1; i < args.size(); i++) {
            if (!args.get(i).contains("=")) {
                throw new ValidationException("Аргумент должен быть вида field=value");
            }
        }
    }

    //выполнение команды
    @Override
    public void execute(List<String> args) throws ValidationException {
        long lineId = Long.parseLong(args.get(0));
        for (int i = 1; i < args.size(); i++) {
            String[] kv = args.get(i).split("=", 2);
            env.getReportLineService().updateLine(lineId, kv[0], kv[1]);
        }
        System.out.println("OK");
    }

    @Override
    public String getHelp() {
        return "изменить строку";
    }
}