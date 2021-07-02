package Utils;

import Main.SchedulingApplication;
import Models.Appointment;
import Models.Customer;

import Models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CachedData {
    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    public void addCustomer(Customer newCustomer) { allCustomers.add(newCustomer); }
    public void addAppointment(Appointment newAppointment) { allAppointments.add(newAppointment); }

    public ObservableList<Customer> getAllCustomers() { return allCustomers; }
    public ObservableList<Appointment> getAllAppointments() { return allAppointments; }

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

    public void importAppointments() throws SQLException {
            PreparedStatement appointmentsStatement = Database.getDBConnection().prepareStatement("SELECT * FROM appointments");
            ResultSet appointmentResult = appointmentsStatement.executeQuery();
            while (appointmentResult.next()) {
                // retrieve and convert start/end timestamps to local time
                LocalDateTime startLocalTime = appointmentResult.getTimestamp("Start").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime endLocalTime = appointmentResult.getTimestamp("End").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                Appointment appt = new Appointment(
                    appointmentResult.getInt("Appointment_ID"),
                    appointmentResult.getInt("User_ID"),
                    appointmentResult.getInt("Customer_ID"),
                    appointmentResult.getInt("Contact_ID"),
                    appointmentResult.getString("Title"),
                    appointmentResult.getString("Description"),
                    appointmentResult.getString("Location"),
                    appointmentResult.getString("Type"),
                    startLocalTime,
                    endLocalTime
                );
                addAppointment(appt);
            }
        // need to add anything after this? like checks on the list?
        }

    public ObservableList<Appointment> getAppointmentsByDate(String date) {
        // temp list to hold date matches
        ObservableList<Appointment> datesMatched = FXCollections.observableArrayList();
        for (Appointment appointment:getAllAppointments()) {
            if (appointment.getUserID() == SchedulingApplication.getUser().getId()
                && DateFormatter.formatToIsoDate(appointment.getStart()).equals(date)) {
                datesMatched.add(appointment);
            }
        }
        return datesMatched;
    }

    public ObservableList<Appointment> getAppointmentsByMonth(String date) {

        // Create an array of results that match the date
        ObservableList<Appointment> monthMatches = FXCollections.observableArrayList();
        for (Appointment appointment:getAllAppointments()) {
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

    public ObservableList<String> getContactsByName() {
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        try {
            PreparedStatement contactNamesStatement = Database.getDBConnection().prepareStatement("SELECT DISTINCT Contact_Name FROM contacts");
            ResultSet contactNamesResult = contactNamesStatement.executeQuery();
            while (contactNamesResult.next()) {
                contactNames.add(contactNamesResult.getString("Contact_Name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return contactNames;
    }

    public ObservableList<String> getCustomersByName() {
        ObservableList<String> customerNames = FXCollections.observableArrayList();
        try {
            PreparedStatement customerNamesStatement = Database.getDBConnection().prepareStatement("SELECT DISTINCT Customer_Name FROM customers");
            ResultSet customerNamesResult = customerNamesStatement.executeQuery();
            while (customerNamesResult.next()) {
                customerNames.add(customerNamesResult.getString("Customer_Name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return customerNames;
    }

//    public User getUserByUsername(String username) {
//        User user = null;
//        try {
//            PreparedStatement userStatement = Database.getDBConnection().prepareStatement("SELECT * FROM users WHERE User_Name =?");
//            ResultSet userResult = userStatement.executeQuery();
//            user = new User(
//                    userResult.getInt("User_ID"),
//                    userResult.getString("User_Name"),
//                    userResult.getString("Password")
//            );
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return user;
//    }

    // TODO: create list of dates for start/end time choices in add/modify appointment
}
