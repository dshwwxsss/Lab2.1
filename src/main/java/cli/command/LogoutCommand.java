package cli.command;

import cli.Command;
import cli.Environment;
import validation.ValidationException;

import java.util.List;

public class LogoutCommand extends Command {

    public LogoutCommand(Environment env) {
        super(env);
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        if (!env.getAuthService().isAuthenticated()) {
            throw new ValidationException("Вы не авторизованы");
        }
        env.getAuthService().logout();
    }

    @Override
    public String getHelp() {
        return "выход из системы";
    }
}
