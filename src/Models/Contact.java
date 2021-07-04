package Models;

public class Contact {
    private Integer contactID;
    private String contactName, contactEmail;

    public Contact (int id, String name, String email) {
        this.contactID = id;
        this.contactName = name;
        this.contactEmail = email;
    }

    public Contact () { }

    public Integer getContactID() { return contactID; }
    public String getContactName() { return contactName; }
    public String getContactEmail() { return contactEmail; }

    public void setContactID(int id ) { this.contactID = id; }
    public void setContactName(String name) { this.contactName = name; }
    public void setContactEmail(String email) { this.contactEmail = email; }

    // for display in when populating choice boxes in views
    @Override
    public String toString() {
        return contactName;
    }
}
