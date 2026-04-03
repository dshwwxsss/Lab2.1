package cli.commands; //выводит список команд и их справку

import validation.ValidationException;
import java.util.List;
import java.util.Map;

public class HelpCommand extends Command {
    private final CommandRegistry registry;

    public HelpCommand(Environment env, CommandRegistry registry) {
        super(env);
        this.registry = registry;
    }
    @Override
    public void execute(List<String> args) throws ValidationException {
        System.out.println("Доступные команды:");
        registry.getAll().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("  %-20s %s%n", entry.getKey(), entry.getValue().getHelp()));
    }

    @Override
    public String getHelp() {
        return "показать эту справку";
    }
}