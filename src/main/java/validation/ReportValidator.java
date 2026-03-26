package validation; //проверяет, что отчёт заполнен правильно и можно менять статус

import domain.Report;
import domain.ReportStatus;
import service.SampleService;

public class ReportValidator {
    private final SampleService sampleService;

    public ReportValidator(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    public void validate(Report report) throws ValidationException {
        if (report.getName() == null || report.getName().trim().isEmpty()) {
            throw new ValidationException("Ошибка: название не может быть пустым");
        }
        if (report.getName().length() > 128) {
            throw new ValidationException("Ошибка: название слишком длинное (макс 128)");
        }

        long sampleId = report.getSampleId();
        long experimentId = report.getExperimentId();
        if (sampleId == 0 && experimentId == 0) {
            throw new ValidationException("Ошибка: должен быть указан sampleId или experimentId");
        }
        if (sampleId != 0 && !sampleService.exists(sampleId)) {
            throw new ValidationException("Образец с id=" + sampleId + " не найден");
        }
    }
    public void validateStatusChange(Report report, ReportStatus newStatus) throws ValidationException {
        if (report.getStatus() == ReportStatus.SIGNED) {
            throw new ValidationException("Нельзя изменить подписанный отчёт");
        }
        if (newStatus == ReportStatus.FINAL && report.getStatus() != ReportStatus.DRAFT) {
            throw new ValidationException("Финальным можно сделать только черновик");
        }
        if (newStatus == ReportStatus.SIGNED && report.getStatus() != ReportStatus.FINAL) {
            throw new ValidationException("Подписать можно только финальный отчёт");
        }
    }
}
