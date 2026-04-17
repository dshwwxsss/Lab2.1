package javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cli.Environment;

public class JavaFXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/javafx/main-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 1200, 700);

        javafx.controller.ReportTableController controller = loader.getController();

        // Создаём сервисы (ТЕ ЖЕ, что использует CLI!)
        service.SampleService sampleService = new service.SampleService();
        service.ReportService reportService = new service.ReportService(sampleService);
        service.ReportLineService reportLineService = new service.ReportLineService(reportService);

        // Передаём в контроллер
        cli.Environment env = new cli.Environment(
                sampleService,
                reportService,
                reportLineService,
                new java.util.Scanner(System.in)
        );
        controller.setEnvironment(env);

        stage.setTitle("Лабораторная работа 2.1 - Отчёты");
        stage.setScene(scene);
        stage.show();
    }
}