package app;

import cli.*;
import cli.command.*;
import db.*;
import service.*;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Репозитории
            SampleRepository sampleRepository = new SampleRepository();
            SampleService sampleService = new SampleService(sampleRepository);

            ReportRepository reportRepository = new ReportRepository();
            ReportService reportService = new ReportService(reportRepository, sampleService);

            ReportLineRepository reportLineRepository = new ReportLineRepository();
            ReportLineService reportLineService = new ReportLineService(reportLineRepository, reportService);

            AuthService authService = new AuthService();

            Scanner scanner = new Scanner(System.in);
            Environment env = new Environment(sampleService, reportService, reportLineService, scanner, authService);

            CommandRegistry registry = new CommandRegistry();
            CommandInterpreter interpreter = new CommandInterpreter(registry, env, scanner);

            // Регистрация команд (только тех, что остались, без save/load)
            registry.register("help", new HelpCommand(env, registry));
            registry.register("exit", new ExitCommand(env, interpreter));
            registry.register("sample_list", new SampleListCommand(env));
            registry.register("rep_create_sample", new RepCreateSampleCommand(env));
            registry.register("rep_addline", new RepAddLineCommand(env));
            registry.register("rep_list", new RepListCommand(env));
            registry.register("rep_show", new RepShowCommand(env));
            registry.register("rep_lines", new RepLinesCommand(env));
            registry.register("rep_updateline", new RepUpdateLineCommand(env));
            registry.register("rep_delline", new RepDellineCommand(env));
            registry.register("rep_finalize", new RepFinalizeCommand(env));
            registry.register("rep_sign", new RepSignCommand(env));
            registry.register("rep_export", new RepExportCommand(env));
            registry.register("register", new RegisterCommand(env));
            registry.register("login", new LoginCommand(env));
            registry.register("logout", new LogoutCommand(env));

            if (args.length > 0 && args[0].equals("--gui")) {
                javafx.Launcher.main(args);
            } else {
                interpreter.start();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            System.err.println("Убедитесь, что PostgreSQL запущен и настройки в database.properties верны.");
            System.exit(1);
        }
    }
}