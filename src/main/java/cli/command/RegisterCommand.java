package cli.command;

import cli.Command;
import cli.Environment;
import validation.ValidationException;

import java.util.List;

public class RegisterCommand extends Command {

    public RegisterCommand(Environment env) {
        super(env);
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        System.out.print("Введите логин: ");
        String login = env.getScanner().nextLine().trim();

        System.out.print("Введите пароль: ");
        String password = env.getScanner().nextLine().trim();

        System.out.print("Повторите пароль: ");
        String passwordConfirm = env.getScanner().nextLine().trim();

        if (!password.equals(passwordConfirm)) {
            throw new ValidationException("Пароли не совпадают");
        }

        env.getAuthService().register(login, password);
        System.out.println("OK: Пользователь " + login + " успешно зарегистрирован!");
    }

    @Override
    public String getHelp() {
        return "регистрация нового пользователя";
    }
}