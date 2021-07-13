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
    /**
     * Gets requested customer id
     * @return customer id
     */
    public Integer getCustID() { return custID; }

    /**
     * Gets requested customer division id
     * @return customer division id
     */
    public int getDivisionID() { return divisionID; }

    /**
     * Gets requested customer name
     * @return customer name
     */
    public String getName() { return name; }

    /**
     * Gets requested customer address
     * @return customer address
     */
    public String getAddress() { return address; }

    /**
     * Gets requested customer postal code
     * @return customer postal code
     */
    public String getPostalCode() { return postalCode; }

    /**
     * Gets requested customer phone number
     * @return customer phone number
     */
    public String getPhoneNum() { return phoneNum; }

    // setters for customer attributes
    /**
     * Sets new customer id (not used not but could have uses for later developers)
     * @param custID
     */
    public void setCustID(int custID) { this.custID = custID; }

    /**
     * Sets new customer division id
     * @param divisionID
     */
    public void setDivisionID(int divisionID) { this.divisionID = divisionID; }

    /**
     * Sets new customer name
     * @param name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets new customer address
     * @param address
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * Sets new customer postal code
     * @param postalCode
     */
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    /**
     * Sets new customer phone number
     * @param phoneNum
     */
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
