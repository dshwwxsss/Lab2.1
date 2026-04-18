package javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cli.Environment;

public class JavaFXApp extends Application {

    public static void main(String[] args) {
        launch(args); //открыть окно
    }

    @Override // читает файл и заполняет окно
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader( //читалка FXML-файла
                getClass().getResource("/javafx/main-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 1200, 700); //загружаем FXML, упаковываем результат в сцену,  сначала создаём сцену, а потом кладём её в окно

        javafx.controller.ReportTableController controller = loader.getController();

        // создаём сервисы
        service.SampleService sampleService = new service.SampleService();
        service.ReportService reportService = new service.ReportService(sampleService);
        service.ReportLineService reportLineService = new service.ReportLineService(reportService);

        // передаём в контроллер контейнером
        cli.Environment env = new cli.Environment(
                sampleService,
                reportService,
                reportLineService,
                new java.util.Scanner(System.in) //сканер для чтения с клавиатуры
        );
        controller.setEnvironment(env);

        stage.setTitle("Лабораторная работа 2.1 - Отчёты");
        stage.setScene(scene);
        stage.show();
    }
}