package domain;

import java.time.Instant;

public class AuditLog {
    private final long id;
    private final String userLogin;
    private final String action;
    private final String entityType;
    private final Long entityId;
    private final String details;
    private final Instant createdAt;

    public AuditLog(long id, String userLogin, String action, String entityType,
                    Long entityId, String details, Instant createdAt) {
        this.id = id;
        this.userLogin = userLogin;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.createdAt = createdAt;
    }

    // геттеры
    public long getId() { return id; }
    public String getUserLogin() { return userLogin; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getDetails() { return details; }
    public Instant getCreatedAt() { return createdAt; }
}