package cli.commands; //добавляет строку в отчёт (интерактивно)

import cli.Command;
import cli.Environment;
import domain.MeasurementParam;
import validation.ValidationException;
import java.util.List;

public class RepAddLineCommand extends Command {
    public RepAddLineCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (args.size() != 1) {
            throw new ValidationException("Использование: rep_addline <report_id>");
        }
        try {
            Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            throw new ValidationException("ID отчёта должен быть целым числом");
        }
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        long reportId = Long.parseLong(args.get(0));
        var report = env.getReportService().getReport(reportId)
                .orElseThrow(() -> new ValidationException("Отчёт с id=" + reportId + " не найден"));

        if (report.getStatus() != domain.ReportStatus.DRAFT) {
            throw new ValidationException("Строки можно добавлять только в черновик (DRAFT)");
        }

        System.out.print("Параметр (PH/CONDUCTIVITY/TEMPERATURE): ");
        String paramStr = env.getScanner().nextLine().trim().toUpperCase();
        MeasurementParam param;
        try {
            param = MeasurementParam.valueOf(paramStr);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неизвестный параметр. Допустимые: PH, CONDUCTIVITY, TEMPERATURE");
        }

        System.out.print("Значение: ");
        double value;
        try {
            value = Double.parseDouble(env.getScanner().nextLine().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Значение должно быть числом");
        }

        System.out.print("Единицы: ");
        String unit = env.getScanner().nextLine().trim();
        if (unit.isEmpty()) throw new ValidationException("Единицы не могут быть пустыми");

        var line = env.getReportLineService().addLine(reportId, param, value, unit);
        System.out.println("OK line_id=" + line.getId());
    }

    @Override
    public String getHelp() {
        return "добавить строку в отчёт (интерактивно)";
    }
}