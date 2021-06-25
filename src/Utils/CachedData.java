package Utils;

import Models.Appointment;
import Models.Customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CachedData {
    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

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
        } catch (SQLException dbError) {
            dbError.printStackTrace();
        }
        // need to add anything after this? like checks on the list?
    }

    public void importAppointments() {
        try {
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
        } catch (SQLException dbError) {
            dbError.printStackTrace();
        }
        // need to add anything after this? like checks on the list?
    }
}
