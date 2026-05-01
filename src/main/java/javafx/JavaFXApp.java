package javafx;

import cli.Environment;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.*;

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

        SampleService sampleService = new SampleService();
        ReportService reportService = new ReportService(sampleService);
        ReportLineService reportLineService = new ReportLineService(reportService);
        AuthService authService = new AuthService();  // НОВОЕ

        Environment env = new Environment(
                sampleService,
                reportService,
                reportLineService,
                new java.util.Scanner(System.in),
                authService  // НОВОЕ
        );
        controller.setEnvironment(env);

        stage.setTitle("Лабораторная работа 2.1 - Отчёты");
        stage.setScene(scene);
        stage.show();
    }
}