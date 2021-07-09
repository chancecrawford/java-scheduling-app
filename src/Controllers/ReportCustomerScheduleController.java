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

    public static final CachedData cachedData = AppointmentsController.cachedData;
    private final Calendar calendar = Calendar.getInstance();

    private final ObservableList<Appointment> reportTableItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cachedData.importCustomers();
        // set reports available in choicebox
        reportsChoiceBox.setItems(cachedData.getReports());
        reportsChoiceBox.setValue("Customer Schedules");
        // set contacts choicebox
        customersChoiceBox.setItems(cachedData.getAllCustomers());
        // set contact to first in list
        customersChoiceBox.setValue(customersChoiceBox.getItems().get(0));

        dateRangeLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));

        retrieveReportData();
        setReportColumns();

        // set listeners and events
        setReportChoiceListener();
        setCustomersChoiceListener();
        setNavigationButtonEvents();
        setCalendarNavigateButtonEvents();
    }

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

    private void setCustomersChoiceListener() {
        customersChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                retrieveReportData();
            }
        });
    }

    private void setReportColumns() {
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStart().format(DateTimeFormatter.ofPattern("MMM d"))));
        appointmentStartEndColumn.setCellValueFactory(param -> Bindings.concat(param.getValue().getStart().format(DateTimeFormatter.ofPattern("hh:mm a")) + " - " +
                param.getValue().getEnd().format(DateTimeFormatter.ofPattern("hh:mm a"))));
    }
}
