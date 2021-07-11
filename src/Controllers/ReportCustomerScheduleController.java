package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Appointment;
import Models.Customer;
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

public class ReportCustomerScheduleController {
    // javafx instantiation for ui elements
    @FXML
    private Button appointmentsNavButton, customersNavButton, logoutButton;
    @FXML
    private ChoiceBox<String> reportsChoiceBox;
    @FXML
    private ChoiceBox<Customer> customersChoiceBox;
    @FXML
    private Label dateRangeLabel;
    @FXML
    private Button calendarPreviousButton, calendarNextButton;
    @FXML
    private TableView<Appointment> reportTableView;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn, appointmentDateColumn, appointmentStartEndColumn;

    // grab cached data from main appointments view
    public static final CachedData cachedData = AppointmentsController.cachedData;
    // create calendar for grabbing date based data
    private final Calendar calendar = Calendar.getInstance();
    // list to hold retrieved data for report
    private final ObservableList<Appointment> reportTableItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // get needed data for report
        cachedData.importCustomers();
        // set reports available in choicebox
        reportsChoiceBox.setItems(cachedData.getReports());
        reportsChoiceBox.setValue("Customer Schedules");
        // set contacts choicebox
        customersChoiceBox.setItems(cachedData.getAllCustomers());
        // set contact to first in list
        customersChoiceBox.setValue(customersChoiceBox.getItems().get(0));
        // set label to current month and year
        dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
        // get data for report and set in table
        retrieveReportData();
        setReportColumns();

        // set listeners and events
        setReportChoiceListener();
        setCustomersChoiceListener();
        setNavigationButtonEvents();
        setCalendarNavigateButtonEvents();
    }

    /**
     * Grabs list of customers for current/selected month for selected customer, iterates through list and puts
     * results in report table
     */
    private void retrieveReportData() {
        // clear table before populating with new data
        reportTableItems.clear();
        reportTableView.setItems(reportTableItems);
        // populate table with selected contact data
        reportTableItems.addAll(cachedData.getAppointmentsByCustomerForMonth(
                DateFormatter.formatToSimpleDate(calendar.getTime(),
                        "isoYearMonth"), customersChoiceBox.getValue().getCustID())
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
     * Changes view to selected report view after selection confirmed not to be null, based on user input
     */
    private void setReportChoiceListener() {
        // lambda used for better iteration for listener through objects
        reportsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                try {
                    if (newValue.equals("Total Appointment Types By Month")) {
                        cachedData.clearCustomers();
                        SchedulingApplication.switchScenes(Paths.reportAppointmentTypeMonthPath);
                    }
                    if (newValue.equals("Contact Schedules")) {
                        cachedData.clearCustomers();
                        SchedulingApplication.switchScenes(Paths.reportContactSchedulePath);
                    }
                    if (newValue.equals("Customer Schedules")) {
                        SchedulingApplication.switchScenes(Paths.reportCustomerSchedulePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Ensures selection is made before repopulating report table with data based on selected customer
     */
    private void setCustomersChoiceListener() {
        // lambda for better iteration through customer objects for listener
        customersChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (customersChoiceBox.getSelectionModel().getSelectedItem() != null && !newValue.equals(oldValue)) {
                retrieveReportData();
            }
        });
    }

    /**
     * Sets columns to relevant data from report
     */
    private void setReportColumns() {
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        // lambdas for iterating through items and using formatters to properly display values
        appointmentDateColumn.setCellValueFactory(date -> new SimpleStringProperty(date.getValue().getStart().format(DateTimeFormatter.ofPattern("MMM d"))));
        appointmentStartEndColumn.setCellValueFactory(times -> Bindings.concat(times.getValue().getStart().format(DateTimeFormatter.ofPattern("hh:mm a")) + " - " +
                times.getValue().getEnd().format(DateTimeFormatter.ofPattern("hh:mm a"))));
    }
}
