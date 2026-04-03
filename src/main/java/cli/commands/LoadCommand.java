package cli.commands;

import cli.Command;
import cli.Environment;
import storage.FileStorage;
import storage.FileValidator;
import validation.ValidationException;

import java.io.IOException;
import java.util.List;

public class LoadCommand extends Command {
    private final FileStorage fileStorage;
    private final FileValidator fileValidator;

    public LoadCommand(Environment env) {
        super(env);
        this.fileStorage = new FileStorage();
        this.fileValidator = new FileValidator();
    }

    @Override
    public void checkArgs(List<String> args) throws ValidationException {
        // Проверяем, что пользователь указал путь к файлу
        if (args.size() != 1) {
            throw new ValidationException("Использование: load <путь_к_файлу>");
        }
    }

    @Override
    public void execute(List<String> args) throws ValidationException {
        String path = args.get(0);
        try {
            // 1. Загружаем данные во временные коллекции
            FileStorage.LoadedData loaded = fileStorage.loadAll(path);

            // 2. Валидируем (если ошибка — старые данные не затронуты)
            fileValidator.validate(loaded);

            // 3. Заменяем данные в сервисах
            env.getSampleService().replaceAll(loaded.samples);
            env.getReportService().replaceAll(loaded.reports);
            env.getReportLineService().replaceAll(loaded.lines);

            System.out.println("OK данные загружены из " + path);
        } catch (IOException e) {
            throw new ValidationException("Ошибка чтения файла: " + e.getMessage());
        }
    }

    @Override
    public String getHelp() {
        return "загрузить данные из CSV-файла";
    }
}