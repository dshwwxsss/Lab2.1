package cli; //цикл чтения команд и их выполнения


import validation.ValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CommandInterpreter {
    private final CommandRegistry registry; //словарь, где по имени команды лежит объект команды
    private final Environment env;
    private final Scanner scanner;
    private boolean running = true;

    //конструктор сохраняет переданные зависимости
    public CommandInterpreter(CommandRegistry registry, Environment env, Scanner scanner) {
        this.registry = registry;
        this.env = env;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("Добро пожаловать! Введите 'help' для списка команд.");
        while (running) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            String commandName = parts[0];
            List<String> args = Arrays.stream(parts)
                    .skip(1)
                    .collect(Collectors.toList());
            if (!registry.contains(commandName)) {
                System.out.println("Неизвестная команда. Введите 'help'.");
                continue;
            }
            Command command = registry.get(commandName);

            try {
                command.checkArgs(args);
                if (command.isReqAdditionalInput()) {
                    command.performAdditionalInput(args);
                }

                command.execute(args);
            } catch (ValidationException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage());

            }

        }
    }
    public void stop() {
            running = false;

    }
}
