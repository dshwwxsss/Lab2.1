import cli.*;
import cli.command.*;
import service.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) { //создание сервисов
        SampleService sampleService = new SampleService();
        ReportService reportService = new ReportService(sampleService);
        ReportLineService reportLineService = new ReportLineService(reportService);
        Scanner scanner = new Scanner(System.in); //чтение ввода с клавиатуры
        Environment env = new Environment(sampleService, reportService, reportLineService, scanner);

        CommandRegistry registry = new CommandRegistry(); //пустой словарь, в который мы будем добавлять команды
        CommandInterpreter interpreter = new CommandInterpreter(registry, env, scanner); //создаётся с реестром

        //регистрация команд
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
// 3 этап
        registry.register("save", new SaveCommand(env));
        registry.register("load", new LoadCommand(env));

        interpreter.start(); //запуск интерпретатора
    }
}
