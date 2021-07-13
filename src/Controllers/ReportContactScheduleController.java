package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Appointment;
import Models.Contact;
import Utils.CachedData;
import Utils.DateFormatter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * Report view that generates the report for contact schedules and handles selection/navigation to other types of reports.
 */
public class ReportContactScheduleController {
    // javafx instantiation for ui elements
    @FXML
    private Button appointmentsNavButton, customersNavButton, logoutButton;
    @FXML
    private ChoiceBox<String> reportsChoiceBox;
    @FXML
    private ChoiceBox<Contact> contactsChoiceBox;
    @FXML
    private Label dateRangeLabel;
    @FXML
    private Button calendarPreviousButton, calendarNextButton;
    @FXML
    private TableView<Appointment> reportTableView;
    @FXML
    private TableColumn<Appointment, String>
            appointmentIDColumn,
            appointmentTitleColumn,
            appointmentTypeColumn,
            appointmentDateColumn,
            appointmentStartEndColumn,
            appointmentCustomerIDColumn,
            appointmentDescriptionColumn;

    // grab cached data from main appointments view
    public static final CachedData cachedData = AppointmentsController.cachedData;
    // create calendar for grabbing date based data
    private final Calendar calendar = Calendar.getInstance();
    // list to hold retrieved data for report
    private final ObservableList<Appointment> reportTableItems = FXCollections.observableArrayList();

    /**
     * Initializes all ui elements, retrieves the needed data for the report, and populates the table view with the results.
     */
    @FXML
    private void initialize() {
        cachedData.importContacts();
        // set reports available in choicebox
        reportsChoiceBox.setItems(cachedData.getReports());
        reportsChoiceBox.setValue("Contact Schedules");
        // set contacts choicebox
        contactsChoiceBox.setItems(cachedData.getAllContacts());
        // set contact to first in list
        contactsChoiceBox.setValue(contactsChoiceBox.getItems().get(0));
        // set label to current month and year
        dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
        // get data for report and set in table
        retrieveReportData();
        setReportColumns();

        // set listeners and events
        setReportChoiceListener();
        setContactsChoiceListener();
        setNavigationButtonEvents();
        setCalendarNavigateButtonEvents();
    }

    /**
     * Grabs list of contacts for current/selected month for selected contact, iterates through list and puts
     * results in report table
     */
    private void retrieveReportData() {
        // clear table before populating with new data
        reportTableItems.clear();
        reportTableView.setItems(reportTableItems);
        // populate table with selected contact data
        reportTableItems.addAll(cachedData.getAppointmentsByContactForMonth(
                DateFormatter.formatToSimpleDate(calendar.getTime(),
                        "isoYearMonth"), contactsChoiceBox.getValue().getContactID())
        );
        reportTableView.setItems(reportTableItems);
    }

    /**
     * Sets actions to fire for nagivating between different pages (Appointments, Customers, and logout).
     */
    private void setNavigationButtonEvents() {
        appointmentsNavButton.setOnAction(actionEvent -> {
            try {
                cachedData.clearAppointments();
                SchedulingApplication.switchScenes(Paths.appointmentsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        customersNavButton.setOnAction(actionEvent -> {
            try {
                cachedData.clearCustomers();
                SchedulingApplication.switchScenes(Paths.customersPath);
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
                            // want to set user to null and clear cached data as security measure
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
     * Sets actions for calendar navigation buttons. Previous button goes back one month or week depending on toggle
     * selected. Next does the same but for one month or week forward.
     */
    private void setCalendarNavigateButtonEvents() {
        // moves calendar one week or month back then repopulates data
        calendarPreviousButton.setOnAction(actionEvent -> {
            calendar.add(Calendar.MONTH, -1);
            dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
            retrieveReportData();
        });
        // moves calendar one week or month forward then repopulates data
        calendarNextButton.setOnAction(actionEvent -> {
            calendar.add(Calendar.MONTH, 1);
            dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
            retrieveReportData();
        });
    }

    /**
     * Changes view to selected report view after selection confirmed not to be null, based on user input. Uses lambda
     * for better iteration through choice box elements to perform view switching.
     */
    private void setReportChoiceListener() {
        reportsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                try {
                    if (newValue.equals("Total Appointment Types By Month")) {
                        cachedData.clearContacts();
                        SchedulingApplication.switchScenes(Paths.reportAppointmentTypeMonthPath);
                    }
                    if (newValue.equals("Contact Schedules")) {
                        SchedulingApplication.switchScenes(Paths.reportContactSchedulePath);
                    }
                    if (newValue.equals("Customer Schedules")) {
                        cachedData.clearContacts();
                        SchedulingApplication.switchScenes(Paths.reportCustomerSchedulePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Ensures selection is made before repopulating report table with data based on selected contact. Uses lambda for
     * better iteration through contacts objects to perform report generation function call with.
     */
    private void setContactsChoiceListener() {
        // lambda for better iteration through contact objects for listener
        contactsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (contactsChoiceBox.getSelectionModel().getSelectedItem() != null && !newValue.equals(oldValue)) {
                retrieveReportData();
            }
        });
    }

    /**
     * Sets columns to relevant data from report. Uses lambda for date and start/end columns to more efficiently set
     * values from generated report.
     */
    private void setReportColumns() {
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        // lambdas for iterating through items and using formatters to properly display values
        appointmentDateColumn.setCellValueFactory(date -> new SimpleStringProperty(date.getValue().getStart().format(DateTimeFormatter.ofPattern("MMM d"))));
        appointmentStartEndColumn.setCellValueFactory(times -> Bindings.concat(times.getValue().getStart().format(DateTimeFormatter.ofPattern("hh:mm a")) + " - " +
                times.getValue().getEnd().format(DateTimeFormatter.ofPattern("hh:mm a"))));
        appointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }
}
