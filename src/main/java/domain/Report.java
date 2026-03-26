package domain; //описывает отчёт (ID, имя, образец, статус, даты)

import validation.ReportValidator;

import java.time.Instant;
import java.util.Objects;

public final class Report { //поля класса
    // Уникальный номер отчёта. Программа назначает сама.
    private final long id;
    // Название отчёта. Нельзя пустое. До 128 символов.
    private String name;
    // На какой образец относится (если это отчёт по образцу).
    //Может быть 0, если отчёт по эксперименту.
    private long sampleId;
    // На какой эксперимент относится (если это отчёт по эксперименту). Может быть 0, если отчёт по образцу.
    private long experimentId;
    // Статус отчёта: DRAFT, FINAL, SIGNED.
    private ReportStatus status;
    // Кто создал (логин). На ранних этапах можно "SYSTEM".
    private String ownerUsername;
    // Кто подписал (логин). Может быть null, если ещё не подписан.
    private String signedBy;
    // Когда создали. Программа ставит автоматически.
    private final Instant createdAt;
    // Когда обновляли. Программа обновляет автоматически.
    private Instant updatedAt;

    public Report(long id, String name, long sampleId, long experimentId, String ownerUsername) { //конструктор
        this.id = id;
        this.name = name;
        this.sampleId = sampleId;
        this.experimentId = experimentId;
        this.ownerUsername = ownerUsername;
        this.status = ReportStatus.DRAFT; // новый отчёт всегда черновик
        this.signedBy = null;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
//для id и createdAt сеттеров нет – их нельзя изменить после создания

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getExperimentId() {
        return experimentId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public long getSampleId() {
        return sampleId;
    }

    public String getSignedBy() {
        return signedBy;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSampleId(long sampleId) {
        this.sampleId = sampleId;
    }

    public void setExperimentId(long experimentId) {
        this.experimentId = experimentId;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /*если два объекта имеют одинаковый equals(поля) и hashCode(число) возвращает true,
    HashSet считает их одним и тем же объектом и не добавляет второй*/

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id == report.id; /*
                && sampleId == report.sampleId
                && experimentId == report.experimentId
                && Objects.equals(name, report.name)
                && status == report.status
                && Objects.equals(ownerUsername, report.ownerUsername)
                && Objects.equals(signedBy, report.signedBy)
                && Objects.equals(createdAt, report.createdAt)
                && Objects.equals(updatedAt, report.updatedAt); */
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sampleId, experimentId, status, ownerUsername, signedBy, createdAt, updatedAt);
    }

    //вывести отчёт (содержимое, а не его адрес в памяти) в консоль в процессе разработки
    /*@Override
    public String toString() {
        return "Report{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", sampleId=" + sampleId +
                ", experimentId=" + experimentId +
                ", status=" + status +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", signedBy='" + signedBy + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    } */
}
