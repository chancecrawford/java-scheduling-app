package Models;

import java.time.LocalDateTime;

/**
 * Class object for appointments. Allows for easier data population for cache.
 */
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

    /**
     * Constructor for appointment object
     * @param apptID
     * @param title
     * @param description
     * @param location
     * @param type
     * @param start
     * @param end
     * @param customerID
     * @param userID
     * @param contactID
     */
    public Appointment (int apptID, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {
        this.apptID = apptID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    /**
     * Default constructor
     */
    public Appointment() { }

    // getters for appointment attributes

    /**
     * Gets requested appt id
     * @return appt id
     */
    public int getApptID() { return apptID; }
    /**
     * Gets requested appt user id
     * @return appt user id
     */
    public int getUserID() { return userID; }
    /**
     * Gets requested appt customer id
     * @return appt customer id
     */
    public int getCustomerID() { return customerID; }
    /**
     * Gets requested appt contact id
     * @return appt contact id
     */
    public int getContactID() { return contactID; }
    /**
     * Gets requested appt title
     * @return appt title
     */
    public String getTitle() { return title; }
    /**
     * Gets requested appt description
     * @return appt description
     */
    public String getDescription() { return description; }
    /**
     * Gets requested appt location
     * @return appt location
     */
    public String getLocation() { return location; }
    /**
     * Gets requested appt type
     * @return appt type
     */
    public String getType() { return type; }
    /**
     * Gets requested appt start time
     * @return appt start time
     */
    public LocalDateTime getStart() { return start; }
    /**
     * Gets requested appt end time
     * @return appt end time
     */
    public LocalDateTime getEnd() { return end; }
    // setters for appointment attributes

    /**
     * Sets new appt id (not used not but could have uses for later developers)
     * @param apptID
     */
    public void setApptID(int apptID) { this.apptID = apptID; }

    /**
     * Sets new appt user id
     * @param userID
     */
    public void setUserID(int userID) { this.userID = userID; }

    /**
     * Sets new appt customer id
     * @param customerID
     */
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    /**
     * Sets new appt contact id
     * @param contactID
     */
    public void setContactID(int contactID) { this.contactID = contactID; }

    /**
     * Sets new appt title
     * @param title
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Sets new appt description
     * @param description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Sets new appt location
     * @param location
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * Sets new appt type
     * @param type
     */
    public void setType(String type) { this.type = type; }

    /**
     * Sets new appt start time
     * @param start
     */
    public void setStart(LocalDateTime start) { this.start = start; }

    /**
     * Sets new appt end time
     * @param end
     */
    public void setEnd(LocalDateTime end) { this.end = end; }
}
