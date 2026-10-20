package ua.kpp.model;

import java.time.LocalDate;

public class ServiceRequest {
    private Long id;
    private Long equipmentId;
    private String description;
    private String priority;
    private String status;
    private LocalDate createdDate;

    public ServiceRequest() {
    }

    public ServiceRequest(Long id, Long equipmentId, String description, String priority, String status, LocalDate createdDate) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.createdDate = createdDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "id=" + id +
                ", equipmentId=" + equipmentId +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}