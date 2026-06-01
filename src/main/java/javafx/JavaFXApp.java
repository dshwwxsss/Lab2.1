package javafx;

import db.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import service.*;

public class JavaFXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            db.SampleRepository sampleRepository = new db.SampleRepository();
            service.SampleService sampleService = new service.SampleService(sampleRepository);

            db.ReportRepository reportRepository = new db.ReportRepository();
            service.ReportService reportService = new service.ReportService(reportRepository, sampleService);

            db.ReportLineRepository reportLineRepository = new db.ReportLineRepository();
            service.ReportLineService reportLineService = new service.ReportLineService(reportLineRepository, reportService);

            service.AuthService authService = new service.AuthService();

            javafx.controller.LoginDialog loginDialog = new javafx.controller.LoginDialog(authService);
            if (!loginDialog.showAndWait()) {
                primaryStage.close();
                return;
            }

            // Загружаем FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/main-view.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.controller.ReportTableController controller = loader.getController();

            cli.Environment env = new cli.Environment(
                    sampleService,
                    reportService,
                    reportLineService,
                    new java.util.Scanner(System.in),
                    authService
            );
            controller.setEnvironment(env);

            primaryStage.setTitle("Лабораторная работа 2.1 - Отчёты");
            primaryStage.setScene(new javafx.scene.Scene(root, 1200, 700));
            primaryStage.show();

        } catch (Exception e) {
            javafx.controller.DialogManager.showAlert(
                    "Ошибка запуска",
                    "Не удалось запустить приложение:\n" + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}