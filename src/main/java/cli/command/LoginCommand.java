package cli.command;

import cli.Command;
import cli.Environment;
import validation.ValidationException;
import java.util.List;

public class LoginCommand extends Command {

    public LoginCommand(Environment env) {
        super(env);
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        System.out.print("Введите логин: ");
        String login = env.getScanner().nextLine().trim();

        System.out.print("Введите пароль: ");
        String password = env.getScanner().nextLine().trim();

        env.getAuthService().login(login, password);
    }

    @Override
    public String getHelp() {
        return "вход в систему";
    }
}