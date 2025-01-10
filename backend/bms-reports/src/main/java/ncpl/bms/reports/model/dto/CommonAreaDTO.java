package ncpl.bms.reports.model.dto;

public class CommonAreaDTO {

    private Integer id;
    private String name;
    private String address;
    private String personOfContact;
    private String mobileNumber;
    private String unitAddress;
    private Integer totalArea;// New field
    private String email;       // New field


    // Default constructor
    public CommonAreaDTO() {}

    // Parameterized constructor
    public CommonAreaDTO(Integer id, String name, String address, String personOfContact, String mobileNumber, String unitAddress, Integer totalArea, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.personOfContact = personOfContact;
        this.mobileNumber = mobileNumber;
        this.unitAddress = unitAddress;
        this.totalArea = totalArea;
        this.email = email;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPersonOfContact() {
        return personOfContact;
    }

    public void setPersonOfContact(String personOfContact) {
        this.personOfContact = personOfContact;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUnitAddress() {
        return unitAddress;
    }

    public void setUnitAddress(String unitAddress) {
        this.unitAddress = unitAddress;
    }

    public Integer getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(Integer totalArea) {
        this.totalArea = totalArea;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
