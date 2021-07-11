package Models;

/**
 * Class object for contacts. Allows for easier data population for cache and displaying.
 */
public class Contact {
    private Integer contactID;
    private String contactName, contactEmail;

    /**
     * Constructor for contact object
     * @param id
     * @param name
     * @param email
     */
    public Contact (int id, String name, String email) {
        this.contactID = id;
        this.contactName = name;
        this.contactEmail = email;
    }

    /**
     * Default constructor
     */
    public Contact () { }

    // getters for contact attributes
    public Integer getContactID() { return contactID; }
    public String getContactName() { return contactName; }
    public String getContactEmail() { return contactEmail; }
    // setters for contact attributes
    public void setContactID(int id ) { this.contactID = id; }
    public void setContactName(String name) { this.contactName = name; }
    public void setContactEmail(String email) { this.contactEmail = email; }

    /**
     * For display when populating choice boxes in views
     * @return contact name attribute
     */
    @Override
    public String toString() {
        return contactName;
    }
}
