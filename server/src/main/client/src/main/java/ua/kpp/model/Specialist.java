package ua.kpp.model;

public class Specialist {
    private Long id;
    private String fullName;
    private String specialization;
    private String phone;
    private String availabilityStatus;

    public Specialist() {
    }

    public Specialist(Long id, String fullName, String specialization, String phone, String availabilityStatus) {
        this.id = id;
        this.fullName = fullName;
        this.specialization = specialization;
        this.phone = phone;
        this.availabilityStatus = availabilityStatus;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    @Override
    public String toString() {
        return "Specialist{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}