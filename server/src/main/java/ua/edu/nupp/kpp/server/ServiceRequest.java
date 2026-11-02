package ua.edu.nupp.kpp.server;

import java.time.LocalDate;

public class ServiceRequest {

    private Long id;
    private long equipmentId;
    private String description;
    private String priority;
    private ServiceRequestState state;
    private LocalDate createdDate;

    public ServiceRequest() {
    }

    public ServiceRequest(Long id, long equipmentId, String description, String priority, ServiceRequestState state, LocalDate createdDate) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.description = description;
        this.priority = priority;
        this.state = state;
        this.createdDate = createdDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(long equipmentId) { this.equipmentId = equipmentId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public ServiceRequestState getState() { return state; }
    public void setState(ServiceRequestState state) { this.state = state; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
}
