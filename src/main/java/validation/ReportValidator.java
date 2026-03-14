package validation;

import domain.Report;
import domain.ReportStatus;

public class ReportValidator {

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: название не может быть пустым");
        }
        if (name.length()>128) {
            throw new IllegalArgumentException("Ошибка: название слишком длинное (макс 128)");
        }
    }

    public static void validateSampleAndExperiment(long sampleId, long experimentId) {

        if (sampleId == 0 && experimentId == 0) {
            throw new IllegalArgumentException(
                    "Ошибка: должен быть указан sampleId или experimentId"
            );
        }
    }
}
