package cli.commands; //создаёт отчёт по образцу (интерактивно запрашивает название)

import cli.Command;
import cli.Environment;
import validation.ValidationException;
import java.util.List;

public class RepCreateSampleCommand extends Command {
    public RepCreateSampleCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (args.size() != 1) {
            throw new ValidationException("Использование: rep_create_sample <sample_id>");
        }
        try {
            Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            throw new ValidationException("ID образца должен быть целым числом");
        }
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        long sampleId = Long.parseLong(args.get(0));
        if (!env.getSampleService().exists(sampleId)) {
            throw new ValidationException("Образец с id=" + sampleId + " не найден");
        }

        System.out.print("Название отчёта: ");
        String name = env.getScanner().nextLine().trim();
        if (name.isEmpty()) {
            throw new ValidationException("Название не может быть пустым");
        }

        var report = env.getReportService().createReport(name, sampleId, 0);
        System.out.println("OK report_id=" + report.getId());
    }

    @Override
    public String getHelp() {
        return "создать отчёт по образцу (интерактивно)";
    }
}