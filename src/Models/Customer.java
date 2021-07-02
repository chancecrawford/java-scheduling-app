package Models;

public class Customer {
    private int custID, divisionID;
    private String name, address, postalCode, phoneNum;

    public Customer (int custID, int divisionID, String name, String address, String postalCode, String phoneNum) {
        this.custID = custID;
        this.divisionID = divisionID;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;
    }

    // default constructor
    public Customer() { }

    public int getCustID() { return custID; }
    public int getDivisionID() { return divisionID; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPostalCode() { return postalCode; }
    public String getPhoneNum() { return phoneNum; }

    public void setCustID(int custID) { this.custID = custID; }
    public void setDivisionID(int divisionID) { this.divisionID = divisionID; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }
}
