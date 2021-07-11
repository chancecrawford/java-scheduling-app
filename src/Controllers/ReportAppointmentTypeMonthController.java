package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Appointment;
import Utils.CachedData;
import Utils.DateFormatter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class ReportAppointmentTypeMonthController {
    // javafx instantiation for ui elements
    @FXML
    private Button appointmentsNavButton, customersNavButton, logoutButton;
    @FXML
    private ChoiceBox<String> reportsChoiceBox;
    @FXML
    private Label dateRangeLabel;
    @FXML
    private Button calendarPreviousButton, calendarNextButton;
    @FXML
    private TableView<Map.Entry<String, Integer>> reportTableView;
    @FXML
    private TableColumn<Map.Entry<String, Integer>, String> appointmentTypeColumn;
    @FXML
    private TableColumn<Map.Entry<String, Integer>, Integer> typeTotalColumn;

    // grab cached data from main appointments view
    public static final CachedData cachedData = AppointmentsController.cachedData;
    // create calendar for grabbing date based data
    private final Calendar calendar = Calendar.getInstance();
    // hash map to link relevant report data
    private final ObservableMap<String, Integer> apptTypeHashMap = FXCollections.observableHashMap();

    @FXML
    private void initialize() {
        // set reports available in choicebox
        reportsChoiceBox.setItems(cachedData.getReports());
        reportsChoiceBox.setValue("Total Appointment Types By Month");
        setReportChoiceListener();
        // set date and retrieve report data
        dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
        retrieveReportData();
        setReportColumns();
        // set monthly and app navigation buttons
        setCalendarNavigateButtonEvents();
        setNavigationButtonEvents();
        // for tracking to show upcoming appointment alert
        SchedulingApplication.setLastScene("reports");
    }

    /**
     * Grabs list of appointments for current/selected month, iterates through list and totals appointment types, puts
     * results in the hash map, then checks if any results were retrieved before populating table with results
     */
    private void retrieveReportData() {
        // clear hash map before grabbing new values
        apptTypeHashMap.clear();
        // get appointments for the month
        ObservableList<Appointment> appointmentsForMonth = cachedData.getAppointmentsByMonth(DateFormatter.formatToSimpleDate(calendar.getTime(), "isoYearMonth"));
        // iterate through appointment types
        for (String type:cachedData.getAppointmentTypes()) {
            // get total appointments of that type
            int numberOfAppointments = cachedData.getAppointmentTotalByType(appointmentsForMonth, type);
            // ensure result(s) are there before adding to hash map
            if (numberOfAppointments > 0) {
                apptTypeHashMap.put(type, numberOfAppointments);
            }
        }
        // populate table with results
        ObservableList<Map.Entry<String, Integer>> items = FXCollections.observableArrayList(apptTypeHashMap.entrySet());
        reportTableView.setItems(items);
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
                        SchedulingApplication.switchScenes(Paths.reportAppointmentTypeMonthPath);
                    }
                    if (newValue.equals("Contact Schedules")) {
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
     * Sets columns to relevant data from report
     */
    private void setReportColumns() {
        // lambda for better iteration and setting of values in columns
        appointmentTypeColumn.setCellValueFactory(type -> new SimpleStringProperty(type.getValue().getKey()));
        typeTotalColumn.setCellValueFactory(typeTotal -> new SimpleIntegerProperty(typeTotal.getValue().getValue()).asObject());
    }
}
