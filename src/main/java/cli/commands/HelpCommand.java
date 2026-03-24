package cli.commands;

import cli.Command;
import cli.Environment;
import cli.CommandRegistry;
import validation.ValidationException;
import java.util.List;

public class HelpCommand extends Command {
    private final CommandRegistry registry;

    public HelpCommand(Environment env, CommandRegistry registry) {
        super(env);
        this.registry = registry;
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        System.out.println("Доступные команды:");
        for (var entry : registry.getAll().entrySet()) {
            System.out.printf("  %-20s %s%n", entry.getKey(), entry.getValue().getHelp());
        }
    }

    @Override
    public String getHelp() {
        return "показать эту справку";
    }
}