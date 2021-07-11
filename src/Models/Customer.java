package Models;

/**
 * Class object for customers. Allows for easier data population for cache and displaying.
 */
public class Customer {
    private Integer custID;
    private int divisionID;
    private String name, address, postalCode, phoneNum;

    /**
     * Constructor for customer object
     * @param custID
     * @param name
     * @param address
     * @param postalCode
     * @param phoneNum
     * @param divisionID
     */
    public Customer (int custID, String name, String address, String postalCode, String phoneNum, int divisionID) {
        this.custID = custID;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;
        this.divisionID = divisionID;
    }

    /**
     * Default constructor
     */
    public Customer() { }

    // getters for customer attributes
    public Integer getCustID() { return custID; }
    public int getDivisionID() { return divisionID; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPostalCode() { return postalCode; }
    public String getPhoneNum() { return phoneNum; }
    // setters for customer attributes
    public void setCustID(int custID) { this.custID = custID; }
    public void setDivisionID(int divisionID) { this.divisionID = divisionID; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

    /**
     * For display when populating choice boxes in views
     * @return customer name attribute
     */
    @Override
    public String toString() {
        return name;
    }
}
