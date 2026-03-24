package cli;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public void register(String name, Command command) {
        commands.put(name, command);
    }

    public Command get(String name) {
        return commands.get(name);
    }

    public boolean contains(String name) {
        return commands.containsKey(name);
    }

    public Map<String, Command> getAll() {
        return commands;
    }
}
