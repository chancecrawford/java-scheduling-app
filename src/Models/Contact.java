package Models;

public class Contact {
    private int contactID;
    private String contactName, contactEmail;

    public Contact (int id, String name, String email) {
        this.contactID = id;
        this.contactName = name;
        this.contactEmail = email;
    }

    public Contact () { }

    public int getContactID() { return contactID; }
    public String getContactName() { return contactName; }
    public String getContactEmail() { return contactEmail; }

    public void setContactID(int id ) { this.contactID = id; }
    public void setContactName(String name) { this.contactName = name; }
    public void setContactEmail(String email) { this.contactEmail = email; }
}
