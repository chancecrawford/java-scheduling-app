package Utils;

import Main.SchedulingApplication;
import Models.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * Handles all local data manipulation, imports data from database, contains some static information for easier maintenance
 * for future developers.
 */
public class CachedData {
    // lists for holding all major data to be used in application
    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static final ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static final ObservableList<Country> allCountries = FXCollections.observableArrayList();
    private static final ObservableList<Division> allDivisions = FXCollections.observableArrayList();
    // methods for adding and deleting objects/models from relevant lists
    public void addAppointment(Appointment newAppointment) {
        allAppointments.add(newAppointment);
    }
    public void addContact(Contact newContact) {
        allContacts.add(newContact);
    }
    public void addCustomer(Customer newCustomer) {
        allCustomers.add(newCustomer);
    }
    public void addDivision(Division division) { allDivisions.add(division); }
    public void deleteAppointment(Appointment appointment) { allAppointments.remove(appointment); }
    public void deleteCustomer(Customer customer) { allCustomers.remove(customer); }
    // methods for retrieving lists of specific model/object data
    public ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }
    public ObservableList<Contact> getAllContacts() {
        return allContacts;
    }
    public ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }
    public ObservableList<Country> getAllCountries() { return allCountries; }

    // methods for clearing specific datasets or entire cache
    public void clearAppointments() { allAppointments.clear(); }
    public void clearContacts() {
        allContacts.clear();
    }
    public void clearCustomers() {
        allCustomers.clear();
    }
    public void clearCountries() { allCountries.clear(); }
    public void clearDivisions() { allDivisions.clear(); }
    public void clearCache() { allAppointments.clear(); allContacts.clear(); allCustomers.clear(); allCountries.clear(); allDivisions.clear(); }

    /**
     * Grabs all appointments from database and populates cache with it
     * @throws SQLException if connection can't be made or query can't be executed
     */
    public void importAppointments() throws SQLException {
        // create query for all appointments
        PreparedStatement appointmentsStatement = Database.getDBConnection().prepareStatement("SELECT * FROM appointments");
        ResultSet appointmentResult = appointmentsStatement.executeQuery();
        while (appointmentResult.next()) {
            // convert to ldt (still UTC hours here)
            LocalDateTime startUTC = LocalDateTime.parse(appointmentResult.getString("Start"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endUTC = LocalDateTime.parse(appointmentResult.getString("End"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // offset from UTC to local timezone
            LocalDateTime startLocalTime = startUTC.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime endLocalTime = endUTC.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            // create appointment object
            Appointment appt = new Appointment(
                    appointmentResult.getInt("Appointment_ID"),
                    appointmentResult.getString("Title"),
                    appointmentResult.getString("Description"),
                    appointmentResult.getString("Location"),
                    appointmentResult.getString("Type"),
                    startLocalTime,
                    endLocalTime,
                    appointmentResult.getInt("Customer_ID"),
                    appointmentResult.getInt("User_ID"),
                    appointmentResult.getInt("Contact_ID")
            );
            // add appointment to cache
            addAppointment(appt);
        }
    }

    /**
     * Grabs all contacts from database and populates cache with it
     */
    public void importContacts() {
        try {
            // create query for all contacts
            PreparedStatement contactsStatement = Database.getDBConnection().prepareStatement("SELECT * FROM contacts");
            ResultSet contactsResult = contactsStatement.executeQuery();
            while (contactsResult.next()) {
                // create contact object
                Contact contact = new Contact(
                        contactsResult.getInt("Contact_ID"),
                        contactsResult.getString("Contact_Name"),
                        contactsResult.getString("Email")
                );
                // add contact to cache
                addContact(contact);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Grabs all customers from database and populates cache with it
     */
    public void importCustomers() {
        try {
            // create query for all customers
            PreparedStatement customersStatement = Database.getDBConnection().prepareStatement("SELECT * FROM customers");
            ResultSet customerResult = customersStatement.executeQuery();
            while (customerResult.next()) {
                // create customer object
                Customer customer = new Customer(
                        customerResult.getInt("Customer_ID"),
                        customerResult.getString("Customer_Name"),
                        customerResult.getString("Address"),
                        customerResult.getString("Postal_Code"),
                        customerResult.getString("Phone"),
                        customerResult.getInt("Division_ID")
                );
                // add customer to cache
                addCustomer(customer);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Grabs all divisions from database and populates cache with it
     */
    public void importDivisions() {
        try {
            // create query for all divisions
            PreparedStatement divisionStatement = Database.getDBConnection().prepareStatement("SELECT * FROM first_level_divisions");
            ResultSet divisions = divisionStatement.executeQuery();
            while (divisions.next()) {
                // create division object
                Division division = new Division(
                        divisions.getInt("Division_ID"),
                        divisions.getString("Division"),
                        divisions.getInt("Country_ID")
                );
                // add division object to cache
                addDivision(division);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Grabs appointments that match current user to date passed to it
     * @param date desired date to get appointments for
     * @return list of matching appointments for that date
     */
    public ObservableList<Appointment> getAppointmentsByDate(String date) {
        // temp list to hold date matches
        ObservableList<Appointment> datesMatched = FXCollections.observableArrayList();
        // iterate through all appointments for comparison to date requested
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getUserID() == SchedulingApplication.getUser().getId()
                    && DateFormatter.formatLocalDateTime(appointment.getStart(), "iso").equals(date)) {
                // if dates match, add to temp list
                datesMatched.add(appointment);
            }
        }
        // return list of appointments
        return datesMatched;
    }

    /**
     * Grabs list of appointments that contain customer id passed to it
     * @param customerID desired customer id to find appointments for
     * @return list of appointments for that customer id
     */
    public ObservableList<Appointment> getAppointmentsByCustomerID(int customerID) {
        // temp list to hold matching appointments
        ObservableList<Appointment> customerAppointments = FXCollections.observableArrayList();
        // iterate through all appointments for ones with passed customer id
        for (Appointment appointment: getAllAppointments()) {
            if (customerID == appointment.getCustomerID()) {
                // if id matches, add to temp list
                customerAppointments.add(appointment);
            }
        }
        // return list of specified customer appointments
        return customerAppointments;
    }

    /**
     * Grabs list of appointments that match specified date and customer id
     * @param date requested date
     * @param customerID requested customer id
     * @return list of appointments containg both
     */
    public ObservableList<Appointment> getCustomerAppointmentsByDate(String date, int customerID) {
        // temp list to hold date matches
        ObservableList<Appointment> datesMatched = FXCollections.observableArrayList();
        // iterate through all appointments for matches
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getCustomerID() == customerID
                    && DateFormatter.formatLocalDateTime(appointment.getStart(), "iso").equals(date)) {
                // if appt contains customer id and reside on specified date,
                // add to temp list
                datesMatched.add(appointment);
            }
        }
        // return list of appointments
        return datesMatched;
    }

    /**
     * Gets list of appointments in specified month
     * @param date specified date for determining month to compare against
     * @return list of appointments in that month
     */
    public ObservableList<Appointment> getAppointmentsByMonth(String date) {
        // temp list to hold matches
        ObservableList<Appointment> monthMatches = FXCollections.observableArrayList();
        // iterate through all appointments
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getUserID() == SchedulingApplication.getUser().getId()
                    && appointment.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM")).equals(date)) {
                // if appointment is in that month, add to temp list
                monthMatches.add(appointment);
            }
        }
        // return list of that month's appointments
        return monthMatches;
    }

    /**
     * Grabs list of appointments in specified month that contain contact id passed to it
     * @param date requested date for determining month
     * @param contactID requested contact id
     * @return list of appointments containing both
     */
    public ObservableList<Appointment> getAppointmentsByContactForMonth(String date, int contactID) {
        // temp list to hold matches
        ObservableList<Appointment> monthMatches = FXCollections.observableArrayList();
        // iterate through all appointments
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getContactID() == contactID
                    && appointment.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM")).equals(date)) {
                // if appt contains contact id and reside in specified month,
                // add to temp list
                monthMatches.add(appointment);
            }
        }
        // return list of matching appointments
        return monthMatches;
    }

    /**
     * Grabs list of appointments in specified month that contain customer id passed to it
     * @param date requested date for determining month
     * @param customerID requested customer id
     * @return list of appointments containg both
     */
    public ObservableList<Appointment> getAppointmentsByCustomerForMonth(String date, int customerID) {
        // Create a list of results that matches
        ObservableList<Appointment> monthMatches = FXCollections.observableArrayList();
        // iterate through all appointments
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getCustomerID() == customerID
                    && appointment.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM")).equals(date)) {
                // if appt contains customer id and reside in specified month,
                // add to temp list
                monthMatches.add(appointment);
            }
        }
        // return list of matching appointments
        return monthMatches;
    }

    /**
     * Grabs all distinct appointment types from database
     * @return list of appt types
     */
    public ObservableList<String> getAppointmentTypes() {
        ObservableList<String> appointmentTypes = FXCollections.observableArrayList();
        // want a fresh pull from db in case new appt types have been added
        try {
            // create query
            PreparedStatement appointmentTypesStatement = Database.getDBConnection().prepareStatement("SELECT DISTINCT Type FROM appointments");
            ResultSet appointmentTypesResult = appointmentTypesStatement.executeQuery();
            while (appointmentTypesResult.next()) {
                // add results to list
                appointmentTypes.add(appointmentTypesResult.getString("Type"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // return list of types
        return appointmentTypes;
    }

    /**
     * Totals appointment types for report data.
     * @param appointments list of appointments to total type by
     * @param type specified type to total
     * @return int equalling total appointments of that type
     */
    public int getAppointmentTotalByType(ObservableList<Appointment> appointments, String type) {
        int total = 0;
        // iterate through provided list of appointments
        for (Appointment appointment:appointments) {
            if (appointment.getType().equals(type)) {
                // add to total if type matches
                total++;
            }
        }
        return total;
    }

    /**
     * Gets contact object by id from cache
     * @param id requested id
     * @return contact object containing that id or null if one not found
     */
    public Contact getContactByID(int id) {
        for (Contact contact:allContacts) {
            if (contact.getContactID() == id) {
                return contact;
            }
        }
        return null;
    }

    /**
     * Gets customer object by id from cache
     * @param id requested id
     * @return customer object containing that id or null if one not found
     */
    public Customer getCustomerByID(int id) {
        for (Customer customer:allCustomers) {
            if (customer.getCustID() == id) {
                return customer;
            }
        }
        return null;
    }

    /**
     * Gets division object by id from cache
     * @param id requested id
     * @return division object containing that id or null if one not found
     */
    public Division getDivisionByID(int id) {
        for (Division division:allDivisions) {
            if (division.getDivisionID() == id) {
                return division;
            }
        }
        return null;
    }

    /**
     * Grabs list of divisions containing passed country code
     * @param id country code to grab divisions with
     * @return list of division objects containing that country code
     */
    public ObservableList<Division> getDivisionsByCountryID(int id) {
        ObservableList<Division> divisionsByCountry = FXCollections.observableArrayList();
        for (Division division:allDivisions) {
            if (division.getCountryID() == id) {
                divisionsByCountry.add(division);
            }
        }
        return divisionsByCountry;
    }

    /**
     * Grabs all countries from database that application can be used in
     */
    public void importCountries() {
        try {
            // create query and execute it
            PreparedStatement countryStatement = Database.getDBConnection().prepareStatement("SELECT * FROM countries");
            ResultSet countriesResult = countryStatement.executeQuery();
            while (countriesResult.next()) {
                // create country object
                Country country = new Country(
                        countriesResult.getInt("Country_ID"),
                        countriesResult.getString("Country")
                );
                // add country to cache
                allCountries.add(country);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Gets a country object by passed id
     * @param countryID id to find in cache
     * @return country object containing passed id
     */
    public Country getCountryByID(int countryID) {
        for (Country country:allCountries) {
            if (country.getId() == countryID) {
                return country;
            }
        }
        return null;
    }


    // just for easier setting of label
    public final String businessOpen = "8:00 AM";
    public final String businessClose = "10:00 PM";

    /**
     * Converts business open hours to UTC
     * @param localDate for converting local business time to UTC
     * @return UTC time for business open hours
     * @throws ParseException in case a local date can't be parsed successfully
     */
    public ZonedDateTime businessOpenZDT(LocalDate localDate) throws ParseException {
        // iso format for date to be in
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        // set the format to EST timezone
        isoFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        // create date for selected date and opening time EST
        Date openHourDate = isoFormat.parse(localDate + "T08:00:00");
        // return that date as converted zonedatetime to UTC
        return openHourDate.toInstant().atZone(ZoneId.of("UTC"));
    }

    /**
     * Converts business close hours to UTC
     * @param localDate for converting local business time to UTC
     * @return UTC time for business close hours
     * @throws ParseException in case a local date can't be parsed successfully
     */
    public ZonedDateTime businessCloseZDT(LocalDate localDate) throws ParseException {
        // iso format for date to be in
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        // set the format to EST timezone
        isoFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        // create date for selected date and opening time EST
        Date closeHourDate = isoFormat.parse(localDate + "T22:00:00");
        // return that date as converted zonedatetime to UTC
        return closeHourDate.toInstant().atZone(ZoneId.of("UTC"));
    }

    // have this list as local times for easier conversion to dates for saving
    private final ObservableList<LocalTime> appointmentTimes = FXCollections.observableArrayList(
            LocalTime.parse("00:00:00"), LocalTime.parse("00:15:00"), LocalTime.parse("00:30:00"), LocalTime.parse("00:45:00"),
            LocalTime.parse("01:00:00"), LocalTime.parse("01:15:00"), LocalTime.parse("01:30:00"), LocalTime.parse("01:45:00"),
            LocalTime.parse("02:00:00"), LocalTime.parse("02:15:00"), LocalTime.parse("02:30:00"), LocalTime.parse("02:45:00"),
            LocalTime.parse("03:00:00"), LocalTime.parse("03:15:00"), LocalTime.parse("03:30:00"), LocalTime.parse("03:45:00"),
            LocalTime.parse("04:00:00"), LocalTime.parse("04:15:00"), LocalTime.parse("04:30:00"), LocalTime.parse("04:45:00"),
            LocalTime.parse("05:00:00"), LocalTime.parse("05:15:00"), LocalTime.parse("05:30:00"), LocalTime.parse("05:45:00"),
            LocalTime.parse("06:00:00"), LocalTime.parse("06:15:00"), LocalTime.parse("06:30:00"), LocalTime.parse("06:45:00"),
            LocalTime.parse("07:00:00"), LocalTime.parse("07:15:00"), LocalTime.parse("07:30:00"), LocalTime.parse("07:45:00"),
            LocalTime.parse("08:00:00"), LocalTime.parse("08:15:00"), LocalTime.parse("08:30:00"), LocalTime.parse("08:45:00"),
            LocalTime.parse("09:00:00"), LocalTime.parse("09:15:00"), LocalTime.parse("09:30:00"), LocalTime.parse("09:45:00"),
            LocalTime.parse("10:00:00"), LocalTime.parse("10:15:00"), LocalTime.parse("10:30:00"), LocalTime.parse("10:45:00"),
            LocalTime.parse("11:00:00"), LocalTime.parse("11:15:00"), LocalTime.parse("11:30:00"), LocalTime.parse("11:45:00"),
            LocalTime.parse("12:00:00"), LocalTime.parse("12:15:00"), LocalTime.parse("12:30:00"), LocalTime.parse("12:45:00"),
            LocalTime.parse("13:00:00"), LocalTime.parse("13:15:00"), LocalTime.parse("13:30:00"), LocalTime.parse("13:45:00"),
            LocalTime.parse("14:00:00"), LocalTime.parse("14:15:00"), LocalTime.parse("14:30:00"), LocalTime.parse("14:45:00"),
            LocalTime.parse("15:00:00"), LocalTime.parse("15:15:00"), LocalTime.parse("15:30:00"), LocalTime.parse("15:45:00"),
            LocalTime.parse("16:00:00"), LocalTime.parse("16:15:00"), LocalTime.parse("16:30:00"), LocalTime.parse("16:45:00"),
            LocalTime.parse("17:00:00"), LocalTime.parse("17:15:00"), LocalTime.parse("17:30:00"), LocalTime.parse("17:45:00"),
            LocalTime.parse("18:00:00"), LocalTime.parse("18:15:00"), LocalTime.parse("18:30:00"), LocalTime.parse("18:45:00"),
            LocalTime.parse("19:00:00"), LocalTime.parse("19:15:00"), LocalTime.parse("19:30:00"), LocalTime.parse("19:45:00"),
            LocalTime.parse("20:00:00"), LocalTime.parse("20:15:00"), LocalTime.parse("20:30:00"), LocalTime.parse("20:45:00"),
            LocalTime.parse("21:00:00"), LocalTime.parse("21:15:00"), LocalTime.parse("21:30:00"), LocalTime.parse("21:45:00"),
            LocalTime.parse("22:00:00"), LocalTime.parse("22:15:00"), LocalTime.parse("22:30:00"), LocalTime.parse("22:45:00"),
            LocalTime.parse("23:00:00"), LocalTime.parse("23:15:00"), LocalTime.parse("23:30:00"), LocalTime.parse("23:45:00")
    );

    /**
     * Grabs above list of times for scheduling appointments. Allows easier maintenance for future developers if business
     * requirments need to change interval of appt times.
     * @return list of appointment times for user scheduling
     */
    public ObservableList<LocalTime> getAppointmentTimes() {
        return appointmentTimes;
    }

    // list of report types to populate choice box with
    private final ObservableList<String> reports = FXCollections.observableArrayList(
            "Total Appointment Types By Month",
            "Contact Schedules",
            "Customer Schedules"
    );

    /**
     * Grabs all report titles to be generated
     * @return list of reports
     */
    public ObservableList<String> getReports() { return reports; }
}
