package cli.commands;

import cli.Command;
import cli.Environment;
import cli.CommandInterpreter;
import validation.ValidationException;
import java.util.List;

public class ExitCommand extends Command {
    private final CommandInterpreter interpreter;

    public ExitCommand(Environment env, CommandInterpreter interpreter) {
        super(env);
        this.interpreter = interpreter;
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        System.out.println("До свидания!");
        interpreter.stop();
    }

    @Override
    public String getHelp() {
        return "выход из программы";
    }
}