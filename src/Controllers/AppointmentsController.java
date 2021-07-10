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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AppointmentsController {
    // view navigation
    @FXML
    private Button customerNavButton, reportsNavButton, logoutButton;
    // toggles for month/week
    @FXML
    private ToggleButton apptsWeeklyToggle, apptsMonthlyToggle;
    // calendar elements
    @FXML
    private Label dateRangeLabel;
    @FXML
    private Button calendarPreviousButton, calendarNextButton;
    // tableview and columns for appointments
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
    // appointment manipulation buttons
    @FXML
    private Button addButton, editButton, deleteButton;

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

    private final Calendar calendar = Calendar.getInstance();
    // want this public for referencing in add/edit views
    // TODO: should do this in Main and just not pull data until after login?
    public static final CachedData cachedData = new CachedData();

    @FXML
    private void initialize() throws SQLException {
        // populate appointment table view
        setAppointmentColumns();
        appointmentTableView.setItems(appointmentTableItems);
        cachedData.importAppointments();

        // check if user has upcoming appointments
        checkUpcomingAppointments();
        // populate appointments for default date range
        populateAppointmentsTable();
        // set links and actions for buttons
        setNavigationButtonEvents();
        setMonthWeekToggleEvent();
        setCalendarNavigateButtonEvents();
        setAppointmentEditingButtonEvents();
        // set listener to update appt table with appts for selected day
        setAppointmentTableListener();
    }

    private void checkUpcomingAppointments() {
        // TODO: add checking for last scene so it'll only generate from login

        // to hold appointments for alert if multiple
        StringBuilder appointmentAlert = new StringBuilder();

        for (Appointment appointment : cachedData.getAppointmentsByDate(DateFormatter.formatToSimpleDate(calendar.getTime(), "iso"))) {
            // get minutes between this appointment and current time
            long minutesBetween = ChronoUnit.MINUTES.between(LocalDateTime.now(), appointment.getStart());
            // check if appointment is within 15 minutes
            if (minutesBetween <= 15) {
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

    private void populateAppointmentsTable() {
        // clear items before grabbing appts for month
        appointmentTableItems.clear();
        appointmentTableView.setItems(appointmentTableItems);

        if (apptsMonthlyToggle.isSelected()) {
            // set month/year for label
            dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
            // search and grab appts
            ObservableList<Appointment> monthlyAppointmentsResult = cachedData.getAppointmentsByMonth(DateFormatter.formatToSimpleDate(calendar.getTime(), "isoYearMonth"));
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
                if (!weeklyAppointmentsResult.isEmpty()) {
                    appointmentTableItems.addAll(weeklyAppointmentsResult);
                    appointmentTableView.setItems(appointmentTableItems);
                }
            }
        }
    }

    private void setNavigationButtonEvents() {
        customerNavButton.setOnAction(actionEvent -> {
            try {
                cachedData.clearAppointments();
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
            try {
                // want to set user to null as security measure
                SchedulingApplication.setUser(null);
                cachedData.clearCache();
                SchedulingApplication.switchScenes(Paths.mainLoginPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setMonthWeekToggleEvent() {
        apptsWeeklyToggle.setOnAction(actionEvent -> {
            appointmentTableView.getSelectionModel().select(null);
            populateAppointmentsTable();
        });
        apptsMonthlyToggle.setOnAction(actionEvent -> {
            appointmentTableView.getSelectionModel().select(null);
            populateAppointmentsTable();
        });
    }

    private void setCalendarNavigateButtonEvents() {
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
            if (selectedAppointment != null) {
                // can't use Alerts class here due to needing to verify against user response from alert
                Alert deleteAppointmentWarning = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedAppointment.getTitle() + "?", ButtonType.OK, ButtonType.CANCEL);
                deleteAppointmentWarning.showAndWait();
                // after user confirms, delete part
                if (deleteAppointmentWarning.getResult() == ButtonType.OK) {
                    try {
                        PreparedStatement deleteApptStatement = Database.getDBConnection().prepareStatement("DELETE FROM appointments WHERE Appointment_ID = ?");
                        deleteApptStatement.setInt(1, selectedAppointment.getApptID());
                        int deleteResult = deleteApptStatement.executeUpdate();
                        if (deleteResult == 1) {
                            cachedData.deleteAppointment(selectedAppointment);
                            appointmentTableView.getSelectionModel().select(null);
                            refreshCache();
                            populateAppointmentsTable();
                            // TODO: add succcess alert here
                            System.out.println("----- Appointment Deleted! -----");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    private void refreshCache() throws SQLException {
        // clear all lists to repopulate
        cachedData.clearCache();
        // pull down fresh data
        cachedData.importAppointments();
    }

    private void setAppointmentTableListener() {
        // used lambda for setting listeners on table view
        appointmentTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (appointmentTableView.getSelectionModel().getSelectedItem() != null) {
                selectedAppointment = newValue;
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

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
