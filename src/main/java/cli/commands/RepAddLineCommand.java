package cli.commands;

import cli.Command;
import cli.Environment;
import domain.MeasurementParam;
import domain.ReportLine;
import domain.ReportStatus;
import validation.ReportLineValidator;
import validation.ValidationException;

import java.util.List;

public class RepAddLineCommand extends Command {
    private final ReportLineValidator validator;

    public RepAddLineCommand(Environment env) {
        super(env);
        this.validator = new ReportLineValidator(env.getReportService());
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

        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new ValidationException("Строки можно добавлять только в черновик (DRAFT)");
        }

        MeasurementParam param = null;
        while (param == null) {
            System.out.print("Параметр (PH/CONDUCTIVITY/TEMPERATURE): ");
            String paramStr = env.getScanner().nextLine().trim().toUpperCase();
            try {
                param = MeasurementParam.valueOf(paramStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: Неизвестный параметр. Допустимые: PH, CONDUCTIVITY, TEMPERATURE");
            }
        }

        double value = 0;
        boolean valueValid = false;
        while (!valueValid) {
            System.out.print("Значение: ");
            String valueStr = env.getScanner().nextLine().trim();
            try {
                double val = Double.parseDouble(valueStr);
                ReportLine tempLine = new ReportLine(0, reportId, param, val, "temp");
                try {
                    validator.validate(tempLine);
                    value = val;
                    valueValid = true;
                } catch (ValidationException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Значение должно быть числом");
            }
        }

        String unit = null;
        while (unit == null) {
            System.out.print("Единицы: ");
            String unitStr = env.getScanner().nextLine().trim();
            ReportLine tempLine = new ReportLine(0, reportId, param, value, unitStr);
            try {
                validator.validate(tempLine);
                unit = unitStr;
            } catch (ValidationException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }


        var line = env.getReportLineService().addLine(reportId, param, value, unit);
        System.out.println("OK line_id=" + line.getId());
    }

    @Override
    public String getHelp() {
        return "добавить строку в отчёт (интерактивно)";
    }
}