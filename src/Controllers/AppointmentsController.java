package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Appointment;
import Utils.CachedData;
import Utils.DateFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Calendar;

public class AppointmentsController {
    // view navigation
    @FXML private Button customerNavButton, reportsNavButton, logoutButton;
    // toggles for month/week
    @FXML private ToggleButton apptsWeeklyToggle, apptsMonthlyToggle;
    // calendar elements
    @FXML
    private Label dateRangeLabel;
    @FXML
    private Button calendarPreviousButton, calendarNextButton;

    // tableview and columns for appointments
    @FXML private TableView<Appointment> appointmentTableView;
    @FXML private TableColumn<Appointment, String>
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
    @FXML private Button addButton, editButton, deleteButton;

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
    private final CachedData cachedData = new CachedData();

    @FXML
    private void initialize() {
        // populate appointment table view
        setAppointmentColumns();
        appointmentTableView.setItems(appointmentTableItems);
        populateAppointmentsTable();
        // set links and actions for buttons
        setNavigationButtonEvents();
        setMonthWeekToggleEvent();
        setCalendarNavigateButtonEvents();
        setAppointmentEditingButtonEvents();
        // set listener to update appt table with appts for selected day
        setAppointmentMonthlyTableListener();
        cachedData.importAppointments();
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
            // delete selected appointment in table view in db
            // after confirm, delete from cached data
            // refresh data in view
        });
    }

    private void setAppointmentMonthlyTableListener() {
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