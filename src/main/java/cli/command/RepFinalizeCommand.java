package cli.command;

import cli.Command;
import cli.Environment;
import validation.ValidationException;
import java.sql.SQLException;
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
        if (!env.getAuthService().isAuthenticated()) {
            throw new ValidationException("Вы не авторизованы. Используйте команду 'login'");
        }
        String currentUser = env.getAuthService().getCurrentUsername();
        try {
            env.getReportService().finalizeReport(id, currentUser);
            System.out.println("OK report " + id + " FINAL");
        } catch (SQLException e) {
            throw new ValidationException("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "перевести отчёт в FINAL";
    }
}