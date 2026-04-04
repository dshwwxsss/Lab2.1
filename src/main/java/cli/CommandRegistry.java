package cli; //словарь команд (имя - объект, который ее выполняет)


import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    //добавляет команду в словарь
    public void register(String name, Command command) {
        commands.put(name, command);
    }

    //возвращает объект команды по её имен
    public Command get(String name) {
        return commands.get(name);
    }

    //проверяет, есть ли команда с таким именем в словаре
    public boolean contains(String name) {
        return commands.containsKey(name);
    }

    //возвращает весь словарь команд
    public Map<String, Command> getAll() {
        return commands;
    }
}
