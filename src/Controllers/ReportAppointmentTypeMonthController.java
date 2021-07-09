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
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;

public class ReportAppointmentTypeMonthController {
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

    // what formats do we need for this?
    // monthYear and ???

    public static final CachedData cachedData = AppointmentsController.cachedData;
    private final Calendar calendar = Calendar.getInstance();

    private final ObservableMap<String, Integer> apptTypeHashMap = FXCollections.observableHashMap();

    @FXML
    private void initialize() throws SQLException {
        cachedData.importAppointments();
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
    }

    private void retrieveReportData() {
        // clear hash map before grabbing new values
        apptTypeHashMap.clear();

        // get appointments for the month
        ObservableList<Appointment> appointmentsForMonth = cachedData.getAppointmentsByMonth(DateFormatter.formatToSimpleDate(calendar.getTime(), "isoYearMonth"));
        // iterate through appointments
        for (String type:cachedData.getAppointmentTypes()) {
            // get total appointments of that type
            int numberOfAppointments = cachedData.getAppointmentTotalByType(appointmentsForMonth, type);

            if (numberOfAppointments > 0) {
                apptTypeHashMap.put(type, numberOfAppointments);
            }
        }
        // populate table with results
        ObservableList<Map.Entry<String, Integer>> items = FXCollections.observableArrayList(apptTypeHashMap.entrySet());
        reportTableView.setItems(items);
    }

    private void setNavigationButtonEvents() {
        appointmentsNavButton.setOnAction(actionEvent -> {
            try {
                SchedulingApplication.switchScenes(Paths.appointmentsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        customersNavButton.setOnAction(actionEvent -> {
            try {
                SchedulingApplication.switchScenes(Paths.customersPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        logoutButton.setOnAction(actionEvent -> {
            try {
                // want to set user to null and clear cached data as security measure
                SchedulingApplication.setUser(null);
                cachedData.clearCache();
                SchedulingApplication.switchScenes(Paths.mainLoginPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setCalendarNavigateButtonEvents() {
        calendarPreviousButton.setOnAction(actionEvent -> {
            calendar.add(Calendar.MONTH, -1);
            dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
            retrieveReportData();
        });
        calendarNextButton.setOnAction(actionEvent -> {
            calendar.add(Calendar.MONTH, 1);
            dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
            retrieveReportData();
        });
    }

    private void setReportChoiceListener() {
        reportsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                try {
                    if (newValue.equals("Total Appointment Types By Month")) {
                        SchedulingApplication.switchScenes(Paths.reportAppointmentTypeMonthPath);
                    }
                    if (newValue.equals("Contact Schedules")) {
                        SchedulingApplication.switchScenes(Paths.reportContactSchedulePath);
                    }
                    if (newValue.equals("Custom Report TBD")) {
                        SchedulingApplication.switchScenes(Paths.customReportPath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setReportColumns() {
        // lambda for better iteration and setting of values in columns
        appointmentTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        typeTotalColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getValue()).asObject());
    }
}
