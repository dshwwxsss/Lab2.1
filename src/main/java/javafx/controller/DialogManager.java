package javafx.controller;

import domain.MeasurementParam;
import domain.ReportLine;
import domain.Sample;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.Set;

public class DialogManager {
    //Показать информационное сообщение (просто уведомление)
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    //Запросить подтверждение действия (кнопки Да/Нет)
    public static boolean showConfirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(null);
        return alert.showAndWait().filter(r -> r == ButtonType.YES).isPresent();
    }

    //Получить строку от пользователя (текстовое поле)
    public static String showTextInput(String title, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setContentText(content);
        return dialog.showAndWait().orElse(null);
    }

    //Выбрать один образец из выпадающего списка
    public static Sample showSampleChoice(Set<Sample> samples, String title) {
        ChoiceDialog<Sample> dialog = new ChoiceDialog<>(null, samples);
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        return dialog.showAndWait().orElse(null);
    }

    //Выбрать параметр измерения (PH, CONDUCTIVITY, TEMPERATURE)
    public static MeasurementParam showParamChoice(String title) {
        ChoiceDialog<MeasurementParam> dialog = new ChoiceDialog<>(MeasurementParam.PH, MeasurementParam.values());
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        return dialog.showAndWait().orElse(null);
    }

    //Выбрать строку отчёта из списка
    public static ReportLine showLineChoice(Set<ReportLine> lines, String title) {
        ChoiceDialog<ReportLine> dialog = new ChoiceDialog<>(null, lines);
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        return dialog.showAndWait().orElse(null);
    }

    //Универсальный выбор строки из массива вариантов
    public static String showChoice(String title, String[] options) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(options[0], options);
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}