package cli.commands;

import cli.Command;
import cli.Environment;
import validation.ValidationException;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RepExportCommand extends Command {
    public RepExportCommand(Environment env) {
        super(env);
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        if (args.size() != 1) {
            throw new ValidationException("Использование: rep_export <report_id>");
        }
        try {
            Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            throw new ValidationException("ID отчёта должен быть целым числом");
        }
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        long id = Long.parseLong(args.get(0));
        var report = env.getReportService().getReport(id)
                .orElseThrow(() -> new ValidationException("Отчёт с id=" + id + " не найден"));
        var lines = env.getReportLineService().getLinesByReport(id);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        StringBuilder sb = new StringBuilder();
        sb.append("ОТЧЁТ #").append(report.getId()).append("\n");
        sb.append("Название: ").append(report.getName()).append("\n");
        sb.append("Образец ID: ").append(report.getSampleId() == 0 ? "-" : report.getSampleId()).append("\n");
        sb.append("Эксперимент ID: ").append(report.getExperimentId() == 0 ? "-" : report.getExperimentId()).append("\n");
        sb.append("Статус: ").append(report.getStatus()).append("\n");
        sb.append("Подписан: ").append(report.getSignedBy() == null ? "нет" : report.getSignedBy()).append("\n");
        sb.append("Дата создания: ").append(report.getCreatedAt() != null ? fmt.format(report.getCreatedAt().atZone(ZoneId.systemDefault())) : "").append("\n");
        sb.append("Результаты:\n");
        for (var l : lines) {
            sb.append(String.format("  %-12s %8.2f %s\n", l.getParam(), l.getValue(), l.getUnit()));
        }
        System.out.println(sb.toString());
    }

    @Override
    public String getHelp() {
        return "rep_export <report_id> – экспорт отчёта в текст";
    }
}