package javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cli.Environment;

public class JavaFXApp extends Application {

    public static final String DATA_FILE = "data.csv";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/javafx/main-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 900, 600);

        // Получаем контроллер
        javafx.controller.ReportTableController controller = loader.getController();

        // Создаём сервисы
        service.SampleService sampleService = new service.SampleService();
        service.ReportService reportService = new service.ReportService(sampleService);
        service.ReportLineService reportLineService = new service.ReportLineService(reportService);

        // === ЗАГРУЗКА ИЗ ФАЙЛА ===
        try {
            storage.FileStorage fileStorage = new storage.FileStorage();
            storage.FileStorage.LoadedData loaded = fileStorage.loadAll(DATA_FILE);

            sampleService.replaceAll(loaded.samples);
            reportService.replaceAll(loaded.reports);
            reportLineService.replaceAll(loaded.lines);

            System.out.println("✅ ЗАГРУЖЕНО образцов: " + loaded.samples.size());
            System.out.println("✅ ЗАГРУЖЕНО отчётов: " + loaded.reports.size());
            System.out.println("✅ ЗАГРУЖЕНО строк: " + loaded.lines.size());
        } catch (Exception e) {
            System.out.println("⚠️ Файл не загружен: " + e.getMessage());
            e.printStackTrace();  // ← Чтобы видеть ошибку!
        }
        // ========================

        // Создаём Environment и передаём в контроллер
        Environment env = new Environment(
                sampleService,
                reportService,
                reportLineService,
                new java.util.Scanner(System.in)
        );
        controller.setEnvironment(env);

        stage.setTitle("Лабораторная работа 2.1");
        stage.setScene(scene);
        stage.show();
    }
}