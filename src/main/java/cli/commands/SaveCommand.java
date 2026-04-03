package cli.commands;

import cli.Command;
import cli.Environment;
import storage.FileStorage;
import validation.ValidationException;

import java.io.IOException;
import java.util.List;

public class SaveCommand extends Command {
    private final FileStorage fileStorage;

    public SaveCommand(Environment env) {
        super(env);
        this.fileStorage = new FileStorage();
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        // Проверяем, что пользователь указал путь к файлу
        if (args.size() != 1) {
            throw new ValidationException("Использование: save <путь_к_файлу>");
        }
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        String path = args.get(0);
        try {
            // Берём данные из сервисов и передаём в хранилище
            fileStorage.saveAll(path,
                    env.getSampleService().getSamples(),
                    env.getReportService().getAllReports(),
                    env.getReportLineService().getAllLines());
            System.out.println("OK данные сохранены в " + path);
        } catch (IOException e) {
            throw new ValidationException("Ошибка сохранения: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "сохранить данные в CSV-файл";
    }
}