package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Appointment;
import Utils.Alerts;
import Utils.CachedData;
import Utils.Database;
import Utils.DateFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

/**
 * Main appointments view that contains navigation buttons to other important views, a list of appointments to be viewed
 * by week or month depending on user toggle choice, and which can be selected by a user to by modified or deleted. Users
 * can also navigate to add appointments from here.
 */
public class AppointmentsController {
    // javafx instantiation for ui elements
    @FXML
    private Button customerNavButton, reportsNavButton, logoutButton;
    @FXML
    private ToggleButton apptsWeeklyToggle, apptsMonthlyToggle;
    @FXML
    private Label dateRangeLabel;
    @FXML
    private Button calendarPreviousButton, calendarNextButton;
    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, String>
            appointmentIDColumn,
            appointmentTitleColumn,
            appointmentTypeColumn,
            appointmentLocationColumn,
            appointmentStartColumn,
            appointmentEndColumn,
            appointmentCustIDColumn,
            appointmentContactColumn,
            appointmentDescColumn;
    @FXML
    private Button addButton, editButton, deleteButton;

    // stores appointments for table view population
    private final ObservableList<Appointment> appointmentTableItems = FXCollections.observableArrayList();
    // for keeping track of selections
    private static Appointment selectedAppointment = null;
    // for passing/retrieving selected appointment between scenes
    public static Appointment getSelectedAppointment() {
        return selectedAppointment;
    }
    public static void setSelectedAppointment(Appointment appointment) {
        selectedAppointment = appointment;
    }
    // for getting, setting, converting, and displaying dates and times
    private final Calendar calendar = Calendar.getInstance();
    // for referencing in add/edit views
    public static final CachedData cachedData = new CachedData();

    /**
     * Initializes and populates appointment list, sets actions for all buttons and toggles.
     * @throws SQLException if a query to the database can't be completed
     */
    @FXML
    private void initialize() throws SQLException {
        // populate appointment table view
        setAppointmentColumns();
        appointmentTableView.setItems(appointmentTableItems);
        // grab fresh data in case changes occured
        cachedData.importAppointments();
        // check if user has upcoming appointments
        checkUpcomingAppointments();
        // populate appointments for default date range
        populateAppointmentsTable();
        // set paths and actions for buttons
        setNavigationButtonEvents();
        setMonthWeekToggleEvent();
        setCalendarNavigateButtonEvents();
        setAppointmentEditingButtonEvents();
        // set listener to update appt table with appts for selected day
        setAppointmentTableListener();
        // for tracking to show upcoming appointment alert
        SchedulingApplication.setLastScene("appointments");
    }

    /**
     * Iterates through every appointment and generates an alert with appointment details of ones within 15min of current time.
     */
    private void checkUpcomingAppointments() {
        if (SchedulingApplication.getLastScene().equals("login")) {
            // to hold appointments for alert if multiple
            StringBuilder appointmentAlert = new StringBuilder();
            for (Appointment appointment : cachedData.getAppointmentsByDate(DateFormatter.formatToSimpleDate(calendar.getTime(), "iso"))) {
                // get minutes between this appointment and current time
                long minutesBetween = ChronoUnit.MINUTES.between(LocalDateTime.now(), appointment.getStart());
                // check if appointment is within 15 minutes and add appt info to alert if true
                if (minutesBetween <= 15 && minutesBetween > 0) {
                    appointmentAlert.append("ID: ")
                            .append(appointment.getApptID())
                            .append("\n")
                            .append("Date: ")
                            .append(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthDay"))
                            .append("\n")
                            .append("Time: ")
                            .append(DateFormatter.formatLocalDateTime(appointment.getStart(), "simpleTime"))
                            .append("\n").append("\n");
                }
            }
            // generates alert and populates it with appointment messages if string builder isn't empty
            if (appointmentAlert.length() > 0 || !appointmentAlert.toString().equals("")) {
                Alerts.GenerateAlert("INFORMATION", "Upcoming Appointments", "You have upcoming appointments!", appointmentAlert.toString(), "ShowAndWait");
            }
            // generate alert if no upcoming appointments exist
            if (appointmentAlert.length() == 0 || appointmentAlert.toString().equals("")) {
                Alerts.GenerateAlert("INFORMATION", "Upcoming Appointments", "No upcoming appointments", "You have no upcoming appointments", "ShowAndWait");
            }
        }
    }

    /**
     * Populates table view with appointments by month or week depending on selected toggle.
     */
    private void populateAppointmentsTable() {
        // clear items before grabbing appts for month
        appointmentTableItems.clear();
        appointmentTableView.setItems(appointmentTableItems);

        if (apptsMonthlyToggle.isSelected()) {
            // set month/year for label
            dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
            // search and grab appts
            ObservableList<Appointment> monthlyAppointmentsResult = cachedData.getAppointmentsByMonth(DateFormatter.formatToSimpleDate(calendar.getTime(), "isoYearMonth"));
            // check if results exist before attempting to add appts to table view
            if (!monthlyAppointmentsResult.isEmpty()) {
                appointmentTableItems.addAll(monthlyAppointmentsResult);
                appointmentTableView.setItems(appointmentTableItems);
            }
        }
        if (apptsWeeklyToggle.isSelected()) {
            // get first/last day of week for label
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            String weekDates = DateFormatter.formatToSimpleDate(calendar.getTime(), "monthDay");
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            weekDates += " - " + DateFormatter.formatToSimpleDate(calendar.getTime(), "monthDay");
            dateRangeLabel.setText(weekDates);

            // iterate through each day of week for appts
            for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
                calendar.set(Calendar.DAY_OF_WEEK, i);
                ObservableList<Appointment> weeklyAppointmentsResult = cachedData.getAppointmentsByDate(DateFormatter.formatToSimpleDate((calendar.getTime()), "iso"));
                // check if results exist before attempting to add appts to table view
                if (!weeklyAppointmentsResult.isEmpty()) {
                    appointmentTableItems.addAll(weeklyAppointmentsResult);
                    appointmentTableView.setItems(appointmentTableItems);
                }
            }
        }
    }

    /**
     * Sets actions to fire for nagivating between different pages (Customers, Reports, and logout).
     */
    private void setNavigationButtonEvents() {
        customerNavButton.setOnAction(actionEvent -> {
            try {
                SchedulingApplication.switchScenes(Paths.customersPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        reportsNavButton.setOnAction(actionEvent -> {
            try {
                SchedulingApplication.switchScenes(Paths.reportAppointmentTypeMonthPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        logoutButton.setOnAction(actionEvent -> {
                    // can't use Alerts class here due to needing to verify against user response from alert
                    Alert logoutAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.OK, ButtonType.CANCEL);
                    logoutAlert.showAndWait();
                    // if user clicks ok, continue with navigation back to login view
                    if (logoutAlert.getResult() == ButtonType.OK) {
                        try {
                            // want to set user to null as security measure
                            SchedulingApplication.setUser(null);
                            cachedData.clearCache();
                            SchedulingApplication.switchScenes(Paths.mainLoginPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
        });
    }

    /**
     * Sets actions for when month/week toggles are selected, which change how and what appointments are displayed.
     */
    private void setMonthWeekToggleEvent() {
        // clear selection in table and refresh data after each toggle is selected
        apptsWeeklyToggle.setOnAction(actionEvent -> {
            appointmentTableView.getSelectionModel().select(null);
            populateAppointmentsTable();
        });
        apptsMonthlyToggle.setOnAction(actionEvent -> {
            appointmentTableView.getSelectionModel().select(null);
            populateAppointmentsTable();
        });
    }

    /**
     * Sets actions for calendar navigation buttons. Previous button goes back one month or week depending on toggle
     * selected. Next does the same but for one month or week forward.
     */
    private void setCalendarNavigateButtonEvents() {
        // moves calendar one week or month back then repopulates data
        calendarPreviousButton.setOnAction(actionEvent -> {
            appointmentTableView.getSelectionModel().select(null);
            if (apptsMonthlyToggle.isSelected()) {
                calendar.add(Calendar.MONTH, -1);
            }
            if (apptsWeeklyToggle.isSelected()) {
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
            }
            populateAppointmentsTable();
        });
        // moves calendar one week or month forward then repopulates data
        calendarNextButton.setOnAction(actionEvent -> {
            appointmentTableView.getSelectionModel().select(null);
            if (apptsMonthlyToggle.isSelected()) {
                calendar.add(Calendar.MONTH, 1);
            }
            if (apptsWeeklyToggle.isSelected()) {
                calendar.add(Calendar.WEEK_OF_MONTH, 1);
            }
            populateAppointmentsTable();
        });
    }

    /**
     * Sets actions for appointment add, edit, and delete buttons. Add/edit trigger navigation to their pages and delete
     * asks for user confirmation before doing so.
     */
    private void setAppointmentEditingButtonEvents() {
        addButton.setOnAction(actionEvent -> {
            try {
                SchedulingApplication.switchScenes(Paths.addAppointmentPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        editButton.setOnAction(actionEvent -> {
            try {
                SchedulingApplication.switchScenes(Paths.editAppointmentsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        deleteButton.setOnAction(actionEvent -> {
            // make sure a selection exists first
            if (selectedAppointment != null) {
                // can't use Alerts class here due to needing to verify against user response from alert
                Alert deleteAppointmentWarning = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Delete Appointment " + selectedAppointment.getApptID() + " of type: " + selectedAppointment.getType() + "?",
                        ButtonType.OK,
                        ButtonType.CANCEL
                );
                deleteAppointmentWarning.showAndWait();
                // after user confirms, delete appointment
                if (deleteAppointmentWarning.getResult() == ButtonType.OK) {
                    try {
                        // create and execute query to delete appointment
                        PreparedStatement deleteApptStatement = Database.getDBConnection().prepareStatement("DELETE FROM appointments WHERE Appointment_ID = ?");
                        deleteApptStatement.setInt(1, selectedAppointment.getApptID());
                        int deleteResult = deleteApptStatement.executeUpdate();
                        // confirm result from deletion
                        if (deleteResult == 1) {
                            // delete appointment from local cache
                            cachedData.deleteAppointment(selectedAppointment);
                            // refresh data then repopulate table with updated data
                            refreshCache();
                            populateAppointmentsTable();
                            // throw success alert for user
                            Alerts.GenerateAlert(
                                    "INFORMATION",
                                    "Appointment Cancellation",
                                    "Appointment Cancelled",
                                    "Appointment " + selectedAppointment.getApptID() + " of type, " + selectedAppointment.getType() + ", has been successfully cancelled.",
                                    "ShowAndWait"
                            );
                            // set selected appt null AFTER custom message
                            appointmentTableView.getSelectionModel().select(null);
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Clears local cache then repopulates with data from database
     * @throws SQLException in case data could not be retrieved or database connection couldn't be established
     */
    private void refreshCache() throws SQLException {
        // clear all lists to repopulate data
        cachedData.clearCache();
        // pull down fresh data
        cachedData.importAppointments();
    }

    /**
     * Sets listener for appointments table. Adds validation prefix to ensure edit/delete functions aren't executed with
     * null selections and sets selected appointment to pass down to edit and delete pages. Lambda used for better
     * iteration through appointments in list to perform needed actions in response to a selection.
     */
    private void setAppointmentTableListener() {
        // used lambda for setting listener on table view selections
        appointmentTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (appointmentTableView.getSelectionModel().getSelectedItem() != null) {
                setSelectedAppointment(newValue);
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    /**
     * Sets columns in appointment table to relevant data from {@link Appointment} class
     */
    private void setAppointmentColumns() {
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        appointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        appointmentDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }
}
