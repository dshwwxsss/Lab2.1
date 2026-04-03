package storage;

import domain.*;
import validation.ValidationException;
import java.util.*;

public class FileValidator {

    public void validate(FileStorage.LoadedData data) throws ValidationException {
        Set<Sample> samples = data.samples;
        Set<Report> reports = data.reports;
        Set<ReportLine> lines = data.lines;

        //нет дубликатов ID
        checkUniqueSampleIds(samples);
        checkUniqueReportIds(reports);
        checkUniqueReportLineIds(lines);
        //обязательные поля не пустые
        validateRequiredFields(samples, reports, lines);
        //связи между сущностями
        validateReferences(reports, lines, samples);
        //диапазонов значений
        validateValueRanges(lines);
    }
    private void checkUniqueSampleIds(Set<Sample> samples) throws ValidationException {
        Set<Long> ids = new HashSet<>();
        for (Sample s : samples) {
            if (!ids.add(s.getId())) {
                throw new ValidationException(
                        "Ошибка загрузки: дубликат Sample id=" + s.getId()
                );
            }
        }
    }
    private void checkUniqueReportIds(Set<Report> reports) throws ValidationException {
        Set<Long> ids = new HashSet<>();
        for (Report r : reports) {
            if (!ids.add(r.getId())) {
                throw new ValidationException(
                        "Ошибка загрузки: дубликат Report id=" + r.getId()
                );
            }
        }
    }
    private void checkUniqueReportLineIds(Set<ReportLine> lines) throws ValidationException {
        Set<Long> ids = new HashSet<>();
        for (ReportLine l : lines) {
            if (!ids.add(l.getId())) {
                throw new ValidationException(
                        "Ошибка загрузки: дубликат ReportLine id=" + l.getId()
                );
            }
        }
    }
    private void validateRequiredFields(Set<Sample> samples, Set<Report> reports, Set<ReportLine> lines)
            throws ValidationException {

        for (Sample s : samples) {
            if (s.getName() == null || s.getName().trim().isEmpty()) {
                throw new ValidationException(
                        "Ошибка загрузки: название образца пустое (id=" + s.getId() + ")"
                );
            }
        }
        for (Report r : reports) {
            if (r.getName() == null || r.getName().trim().isEmpty()) {
                throw new ValidationException(
                        "Ошибка загрузки: название отчёта пустое (id=" + r.getId() + ")"
                );
            }
            if (r.getSampleId() == 0 && r.getExperimentId() == 0) {
                throw new ValidationException(
                        "Ошибка загрузки: у отчёта id=" + r.getId() +
                                " не указан ни sampleId, ни experimentId"
                );
            }
        }
        for (ReportLine l : lines) {
            if (l.getParam() == null) {
                throw new ValidationException(
                        "Ошибка загрузки: параметр строки пустой (id=" + l.getId() + ")"
                );
            }
            if (l.getUnit() == null || l.getUnit().trim().isEmpty()) {
                throw new ValidationException(
                        "Ошибка загрузки: единицы измерения пустые (id=" + l.getId() + ")"
                );
            }
        }
    }
    private void validateReferences(Set<Report> reports, Set<ReportLine> lines, Set<Sample> samples)
            throws ValidationException {

        Set<Long> reportIds = new HashSet<>();
        for (Report r : reports) reportIds.add(r.getId());

        for (ReportLine l : lines) {
            if (!reportIds.contains(l.getReportId())) {
                throw new ValidationException(
                        "Ошибка загрузки: строка id=" + l.getId() +
                                " ссылается на несуществующий отчёт id=" + l.getReportId()
                );
            }
        }
        Set<Long> sampleIds = new HashSet<>();
        for (Sample s : samples) sampleIds.add(s.getId());

        for (Report r : reports) {
            if (r.getSampleId() != 0 && !sampleIds.contains(r.getSampleId())) {
                throw new ValidationException(
                        "Ошибка загрузки: отчёт id=" + r.getId() +
                                " ссылается на несуществующий образец id=" + r.getSampleId()
                );
            }
        }
    }
    private void validateValueRanges(Set<ReportLine> lines) throws ValidationException {
        for (ReportLine l : lines) {
            if (l.getParam() == MeasurementParam.PH && (l.getValue() < 0 || l.getValue() > 14)) {
                throw new ValidationException(
                        "Ошибка загрузки: pH=" + l.getValue() +
                                " вне диапазона [0,14] (строка id=" + l.getId() + ")"
                );
            }
            if (l.getParam() == MeasurementParam.CONDUCTIVITY && l.getValue() < 0) {
                throw new ValidationException(
                        "Ошибка загрузки: электропроводность не может быть отрицательной " +
                                "(строка id=" + l.getId() + ")"
                );
            }
            if (l.getParam() == MeasurementParam.TEMPERATURE &&
                    (l.getValue() < -50 || l.getValue() > 1200)) {
                throw new ValidationException(
                        "Ошибка загрузки: температура=" + l.getValue() +
                                " вне диапазона [-50,1200] (строка id=" + l.getId() + ")"
                );
            }
        }
    }
}