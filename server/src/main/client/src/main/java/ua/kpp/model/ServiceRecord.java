package ua.kpp.model;

import java.time.LocalDate;

public class ServiceRecord {
    private Long id;
    private Long requestId;
    private Long specialistId;
    private String workDescription;
    private LocalDate completionDate;
    private Integer timeSpent;

    public ServiceRecord() {
    }

    public ServiceRecord(Long id, Long requestId, Long specialistId, String workDescription, LocalDate completionDate, Integer timeSpent) {
        this.id = id;
        this.requestId = requestId;
        this.specialistId = specialistId;
        this.workDescription = workDescription;
        this.completionDate = completionDate;
        this.timeSpent = timeSpent;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }

    public Long getSpecialistId() { return specialistId; }
    public void setSpecialistId(Long specialistId) { this.specialistId = specialistId; }

    public String getWorkDescription() { return workDescription; }
    public void setWorkDescription(String workDescription) { this.workDescription = workDescription; }

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }

    public Integer getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Integer timeSpent) { this.timeSpent = timeSpent; }

    @Override
    public String toString() {
        return "ServiceRecord{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", specialistId=" + specialistId +
                ", completionDate=" + completionDate +
                '}';
    }
}