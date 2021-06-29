package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Appointment;
import Utils.CachedData;
import Utils.DateFormatter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Set;

public class AppointmentsWeeklyController {
    // view navigation
    @FXML private Button customerNavButton, reportsNavButton, logoutButton;

    @FXML private ToggleButton apptsMonthlyToggle;
    // calendar elements
    @FXML private Label calendarWeekDatesLabel;
    @FXML private Button calendarPreviousButton, calendarNextButton;
    // tableviews for week
    @FXML private TableView<Appointment> sundayTableView, mondayTableView, tuesdayTableView, wednesdayTableView, thursdayTableView, fridayTableView, saturdayTableView;
    @FXML private TableColumn<Appointment, String> sundayTableColumn, mondayTableColumn, tuesdayTableColumn, wednesdayTableColumn, thursdayTableColumn, fridayTableColumn, saturdayTableColumn;
    // appt manipulation buttons
    @FXML private Button addButton, editButton, deleteButton;

    // for linking each days columns with respective table views
    private final LinkedHashMap<TableView<Appointment>, TableColumn<Appointment, String>> calendarWeekHashMap = new LinkedHashMap<>();

    private final Calendar calendar = Calendar.getInstance();
    private final CachedData cachedData = new CachedData();

    @FXML private void initialize() {
        // get first/last day of week for calendar title
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String weekDates = DateFormatter.formatToSimpleDate(calendar.getTime(), "monthDay");
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        weekDates += " - " + DateFormatter.formatToSimpleDate(calendar.getTime(), "monthDay");
        calendarWeekDatesLabel.setText(weekDates);

        populateCalendar();
        // set links and actions for buttons
        setNavigationButtonEvents();
        setMonthlyToggleEvent();
        setCalendarNavigateButtonEvents();
        setAppointmentEditingButtonEvents();
        // set listeners for tableviews
        setDayTableListener(sundayTableColumn, sundayTableView);
        setDayTableListener(mondayTableColumn, mondayTableView);
        setDayTableListener(tuesdayTableColumn, tuesdayTableView);
        setDayTableListener(wednesdayTableColumn, wednesdayTableView);
        setDayTableListener(thursdayTableColumn, thursdayTableView);
        setDayTableListener(fridayTableColumn, fridayTableView);
        setDayTableListener(saturdayTableColumn, saturdayTableView);

        cachedData.importAppointments();
    }

    private void populateCalendar() {
        calendarWeekHashMap.put(sundayTableView, sundayTableColumn);
        calendarWeekHashMap.put(mondayTableView, mondayTableColumn);
        calendarWeekHashMap.put(tuesdayTableView, tuesdayTableColumn);
        calendarWeekHashMap.put(wednesdayTableView, wednesdayTableColumn);
        calendarWeekHashMap.put(thursdayTableView, thursdayTableColumn);
        calendarWeekHashMap.put(fridayTableView, fridayTableColumn);
        calendarWeekHashMap.put(saturdayTableView, saturdayTableColumn);
        Set<TableView<Appointment>> calendarHashMapSet = calendarWeekHashMap.keySet();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String calendarTitleText = DateFormatter.formatToSimpleDate(calendar.getTime(), "monthDay") + " - ";

        // Iterate through the calendarHashMap to populate the tables
        for (TableView<Appointment> tableView:calendarHashMapSet) {

            // Clear any existing data
            tableView.getItems().clear();

            // Change the table header text to the calendar day
            calendarWeekHashMap.get(tableView).setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthDay"));

            // Populate the table if any appointments exist
            ObservableList<Appointment> currentAppointments =
                    cachedData.getAppointmentsByDate(DateFormatter.formatToSimpleDate((calendar.getTime()), "iso"));
            if (!currentAppointments.isEmpty()) {
                tableView.setItems(currentAppointments);
            }

            // need css stylesheet for altering element appearance here too

            // Check if this day is today's date
            if (DateFormatter.formatToSimpleDate(calendar.getTime(), "iso").equals(DateFormatter.formatToIsoDate(LocalDateTime.now()))) {
                if (!calendarWeekHashMap.get(tableView).getStyleClass().contains("cal-blue-text")) {
                    calendarWeekHashMap.get(tableView).getStyleClass().add("cal-blue-text");
                }
            } else {
                calendarWeekHashMap.get(tableView).getStyleClass().remove("cal-blue-text");
            }

            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            }
        }
        calendarTitleText += DateFormatter.formatToSimpleDate(calendar.getTime(), "monthDay");
        calendarWeekDatesLabel.setText(calendarTitleText);
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

    private void setMonthlyToggleEvent() {
        apptsMonthlyToggle.setOnAction(actionEvent -> {
            try {
                clearSelections();
                SchedulingApplication.switchScenes(Paths.appointmentsMonthlyPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setCalendarNavigateButtonEvents() {
        calendarPreviousButton.setOnAction(actionEvent -> {
            clearSelections();
            calendar.add(Calendar.WEEK_OF_MONTH, -1);
            populateCalendar();
        });
        calendarNextButton.setOnAction(actionEvent -> {
            clearSelections();
            calendar.add(Calendar.WEEK_OF_MONTH, 1);
            populateCalendar();
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

    private void setDayTableListener(TableColumn<Appointment, String> dayColumn, TableView<Appointment> dayTable) {
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dayTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (dayTable.getSelectionModel().getSelectedItem() != null) {
                AppointmentsMonthlyController.setSelectedAppointment(newValue);
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }
    
    private void clearSelections() {
        // use lambda for iteration through hash map to clear selections
        calendarWeekHashMap.keySet().forEach(appointmentTableView -> appointmentTableView.getSelectionModel().select(null));
    }
}
