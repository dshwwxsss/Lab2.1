package app;

import cli.*;
import cli.command.*;
import service.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SampleService sampleService = new SampleService();
        ReportService reportService = new ReportService(sampleService);
        ReportLineService reportLineService = new ReportLineService(reportService);
        AuthService authService = new AuthService();  // НОВОЕ
        Scanner scanner = new Scanner(System.in);

        Environment env = new Environment(sampleService, reportService, reportLineService, scanner, authService);

        CommandRegistry registry = new CommandRegistry();
        CommandInterpreter interpreter = new CommandInterpreter(registry, env, scanner);

        // Существующие команды
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
        registry.register("save", new SaveCommand(env));
        registry.register("load", new LoadCommand(env));

        // НОВЫЕ КОМАНДЫ для этапа 5
        registry.register("register", new RegisterCommand(env));
        registry.register("login", new LoginCommand(env));
        registry.register("logout", new LogoutCommand(env));

        if (args.length > 0 && args[0].equals("--gui")) {
            javafx.Launcher.main(args);
        } else {
            interpreter.start();
        }
    }
}