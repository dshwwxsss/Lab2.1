package domain;

import java.time.Instant;
public final class ReportLine {
    // Уникальный номер строки отчёта. Программа назначает сама.
    public long id;
    // К какому отчёту относится строка (id отчёта).
    // Должен ссылаться на реально существующий Report.
    public long reportId;
    // Параметр (PH/CONDUCTIVITY/...). Выбирается из списка MeasurementParam.
    public MeasurementParam param;
    // Значение (число).
    public double value;
    // Единицы (например "pH"). Нельзя пустое. До 16 символов.
    public String unit;
    // Когда строку добавили/обновили. Программа ставит автоматически.
    public Instant updatedAt;
    // Когда строка создана. Программа ставит автоматически.
    public Instant createdAt;
}
