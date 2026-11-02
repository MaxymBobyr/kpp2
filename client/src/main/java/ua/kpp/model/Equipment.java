package ua.kpp.model;

public class Equipment {
    private Long id;
    private String name;
    private String model;
    private String serialNumber;
    private String status;
    private String location;

    public Equipment() {
    }

    public Equipment(Long id, String name, String model, String serialNumber, String status, String location) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.serialNumber = serialNumber;
        this.status = status;
        this.location = location;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}