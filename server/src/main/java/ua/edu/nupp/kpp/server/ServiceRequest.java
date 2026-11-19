package ua.edu.nupp.kpp.server;

import java.time.LocalDate;

public class ServiceRequest {

    private Long id;
    private long equipmentId;
    private String description;
    private double f1;
    private double f2;
    private double f3;
    private double integralIndicator;
    private ServiceRequestState state;
    private LocalDate createdDate;

    public ServiceRequest() {
    }

    public ServiceRequest(Long id, long equipmentId, String description, double f1, double f2, double f3, double integralIndicator, ServiceRequestState state, LocalDate createdDate) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.description = description;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.integralIndicator = integralIndicator;
        this.state = state;
        this.createdDate = createdDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(long equipmentId) { this.equipmentId = equipmentId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }
    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }
    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }
    public double getIntegralIndicator() { return integralIndicator; }
    public void setIntegralIndicator(double integralIndicator) { this.integralIndicator = integralIndicator; }
    public ServiceRequestState getState() { return state; }
    public void setState(ServiceRequestState state) { this.state = state; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
}
