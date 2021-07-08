package Utils;

import Main.SchedulingApplication;
import Models.Appointment;
import Models.Contact;
import Models.Customer;

import Models.Division;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class CachedData {
    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static final ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static final ObservableList<Division> allDivisions = FXCollections.observableArrayList();

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

    public ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }

    public ObservableList<Contact> getAllContacts() {
        return allContacts;
    }

    public ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }

    public ObservableList<Division> getAllDivisions() { return allDivisions; }

    // funcs for clearing specific datasets or entire cache
    public void clearAppointments() { allAppointments.clear(); }
    public void clearContacts() {
        allContacts.clear();
    }
    public void clearCustomers() {
        allCustomers.clear();
    }
    public void clearDivisions() { allDivisions.clear(); }
    public void clearCache() { allAppointments.clear(); allContacts.clear(); allCustomers.clear(); allDivisions.clear(); }

    public ObservableList<String> getCountries() { return countries; }

    public void importAppointments() throws SQLException {
        PreparedStatement appointmentsStatement = Database.getDBConnection().prepareStatement("SELECT * FROM appointments");
        ResultSet appointmentResult = appointmentsStatement.executeQuery();
        while (appointmentResult.next()) {
            // convert to ldt (still UTC hours here)
            LocalDateTime startUTC = LocalDateTime.parse(appointmentResult.getString("Start"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endUTC = LocalDateTime.parse(appointmentResult.getString("End"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // offset from UTC to local timezone
            LocalDateTime startLocalTime = startUTC.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime endLocalTime = endUTC.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

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
            addAppointment(appt);
        }
        // need to add anything after this? like checks on the list?
    }

    public void importContacts() {
        try {
            PreparedStatement contactsStatement = Database.getDBConnection().prepareStatement("SELECT * FROM contacts");
            ResultSet contactsResult = contactsStatement.executeQuery();
            while (contactsResult.next()) {
                Contact contact = new Contact(
                        contactsResult.getInt("Contact_ID"),
                        contactsResult.getString("Contact_Name"),
                        contactsResult.getString("Email")
                );
                addContact(contact);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // need to add anything after this? like checks on the list?
    }

    public void importCustomers() {
        try {
            PreparedStatement customersStatement = Database.getDBConnection().prepareStatement("SELECT * FROM customers");
            ResultSet customerResult = customersStatement.executeQuery();
            while (customerResult.next()) {
                Customer customer = new Customer(
                        customerResult.getInt("Customer_ID"),
                        customerResult.getInt("Division_ID"),
                        customerResult.getString("Customer_Name"),
                        customerResult.getString("Address"),
                        customerResult.getString("Postal_Code"),
                        customerResult.getString("Phone")
                );
                addCustomer(customer);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // need to add anything after this? like checks on the list?
    }

    public void importDivisions() {
        try {
            PreparedStatement divisionStatement = Database.getDBConnection().prepareStatement("SELECT * FROM first_level_divisions");
            ResultSet divisions = divisionStatement.executeQuery();
            while (divisions.next()) {
                Division division = new Division(
                        divisions.getInt("Division_ID"),
                        divisions.getString("Division"),
                        divisions.getInt("Country_ID")
                );
                addDivision(division);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ObservableList<Appointment> getAppointmentsByDate(String date) {
        // temp list to hold date matches
        ObservableList<Appointment> datesMatched = FXCollections.observableArrayList();
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getUserID() == SchedulingApplication.getUser().getId()
                    && DateFormatter.formatToIsoDate(appointment.getStart()).equals(date)) {
                datesMatched.add(appointment);
            }
        }
        return datesMatched;
    }

    public ObservableList<Appointment> getAppointmentsByCustomerID(int customerID) {
        ObservableList<Appointment> customerAppointments = FXCollections.observableArrayList();
        for (Appointment appointment: getAllAppointments()) {
            if (appointment.getUserID() == SchedulingApplication.getUser().getId()
                && customerID == appointment.getCustomerID()) {
                customerAppointments.add(appointment);
            }
        }
        return customerAppointments;
    }

    public ObservableList<Appointment> getCustomerAppointmentsByDate(String date, int customerID) {
        // temp list to hold date matches
        ObservableList<Appointment> datesMatched = FXCollections.observableArrayList();
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getUserID() == SchedulingApplication.getUser().getId()
                    && appointment.getCustomerID() == customerID
                    && DateFormatter.formatToIsoDate(appointment.getStart()).equals(date)) {
                datesMatched.add(appointment);
            }
        }
        return datesMatched;
    }

    public ObservableList<Appointment> getAppointmentsByMonth(String date) {

        // Create an array of results that match the date
        ObservableList<Appointment> monthMatches = FXCollections.observableArrayList();
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getUserID() == SchedulingApplication.getUser().getId()
                    && appointment.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM")).equals(date)) {
                monthMatches.add(appointment);
            }
        }
        return monthMatches;
    }

    // want a fresh pull from db in case new appt types have been added
    public ObservableList<String> getAppointmentTypes() {
        ObservableList<String> appointmentTypes = FXCollections.observableArrayList();
        try {
            PreparedStatement appointmentTypesStatement = Database.getDBConnection().prepareStatement("SELECT DISTINCT Type FROM appointments");
            ResultSet appointmentTypesResult = appointmentTypesStatement.executeQuery();
            while (appointmentTypesResult.next()) {
                appointmentTypes.add(appointmentTypesResult.getString("Type"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return appointmentTypes;
    }

    public Contact getContactByID(int id) {
        for (Contact contact:allContacts) {
            if (contact.getContactID() == id) {
                return contact;
            }
        }
        return null;
    }

    public Customer getCustomerByID(int id) {
        for (Customer customer:allCustomers) {
            if (customer.getCustID() == id) {
                return customer;
            }
        }
        return null;
    }

    public ObservableList<Division> getDivisionsByCountryID(int id) {
        ObservableList<Division> divisionsByCountry = FXCollections.observableArrayList();
        for (Division division:allDivisions) {
            if (division.getCountryID() == id) {
                divisionsByCountry.add(division);
            }
        }
        return divisionsByCountry;
    }

    private final ObservableList<String> countries = FXCollections.observableArrayList(
            "United States",
            "United Kingdom",
            "Canada"
    );

    public final Integer getCountryID(String country) {
        if (country.equals("United States")) {
            return 1;
        }
        if (country.equals("United Kingdom")) {
            return 2;
        }
        if (country.equals("Canada")) {
            return 3;
        }
        return null;
    }


    // just for easier setting of label
    public final String businessOpen = "8:00 AM";
    public final String businessClose = "10:00 PM";

    // need to declare est time for start/end here
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

    // have this list as local times for easier conversion to dates for saving (?)
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

    public ObservableList<LocalTime> getAppointmentTimes() {
        return appointmentTimes;
    }
}
