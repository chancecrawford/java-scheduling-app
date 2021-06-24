package Models;

import java.time.LocalDateTime;

public class Appointment {
    private int apptID;
    private int userID;
    private int customerID;
    private int contactID;

    private String title;
    private String description;
    private String location;
    private String type;

    private LocalDateTime start;
    private LocalDateTime end;

    public Appointment (int apptID, int userID, int customerID, int contactID, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end) {
        this.apptID = apptID;
        this.userID = userID;
        this.customerID = customerID;
        this.contactID = contactID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    public Appointment() { }

    public int getApptID() { return apptID; }
    public int getUserID() { return userID; }
    public int getCustomerID() { return customerID; }
    public int getContactID() { return contactID; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    public void setApptID(int apptID) { this.apptID = apptID; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public void setContactID(int contactID) { this.contactID = contactID; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setType(String type) { this.type = type; }
    public void setStart(LocalDateTime start) { this.start = start; }
    public void setEnd(LocalDateTime end) { this.end = end; }
}
