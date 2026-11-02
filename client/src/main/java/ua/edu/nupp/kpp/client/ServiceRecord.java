package ua.edu.nupp.kpp.client;

import java.time.LocalDate;

public class ServiceRecord {

    private Long id;
    private long requestId;
    private long specialistId;
    private String workDescription;
    private LocalDate completionDate;
    private int timeSpent;

    public ServiceRecord() {
    }

    public ServiceRecord(Long id, long requestId, long specialistId, String workDescription, LocalDate completionDate, int timeSpent) {
        this.id = id;
        this.requestId = requestId;
        this.specialistId = specialistId;
        this.workDescription = workDescription;
        this.completionDate = completionDate;
        this.timeSpent = timeSpent;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getRequestId() { return requestId; }
    public void setRequestId(long requestId) { this.requestId = requestId; }
    public long getSpecialistId() { return specialistId; }
    public void setSpecialistId(long specialistId) { this.specialistId = specialistId; }
    public String getWorkDescription() { return workDescription; }
    public void setWorkDescription(String workDescription) { this.workDescription = workDescription; }
    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
    public int getTimeSpent() { return timeSpent; }
    public void setTimeSpent(int timeSpent) { this.timeSpent = timeSpent; }
}
