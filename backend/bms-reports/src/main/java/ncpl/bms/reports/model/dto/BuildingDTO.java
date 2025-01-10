package ncpl.bms.reports.model.dto;

public class BuildingDTO {
    private Integer id;
    private String buildingName;
    private String address;
    private Double totalArea;
    private String email;
    private String personOfContact;
    private String phoneNumber;

    // Default Constructor
    public BuildingDTO() {}

    // Parameterized Constructor
    public BuildingDTO(Integer id, String buildingName, String address, Double totalArea, String email, String personOfContact, String phoneNumber) {
        this.id = id;
        this.buildingName = buildingName;
        this.address = address;
        this.totalArea = totalArea;
        this.email = email;
        this.personOfContact = personOfContact;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(Double totalArea) {
        this.totalArea = totalArea;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonOfContact() {
        return personOfContact;
    }

    public void setPersonOfContact(String personOfContact) {
        this.personOfContact = personOfContact;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
