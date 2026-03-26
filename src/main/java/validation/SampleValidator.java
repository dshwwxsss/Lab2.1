package validation; //проверяет, что образец заполнен правильно

import domain.Sample;

public class SampleValidator {
    public static void validate(Sample sample) throws ValidationException {
        if (sample.getName() == null || sample.getName().trim().isEmpty()) {
            throw new ValidationException("Название образца не может быть пустым");
        }
    }
}
