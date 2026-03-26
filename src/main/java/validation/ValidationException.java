package validation; //собственное исключение для ошибок ввода/валидации

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
