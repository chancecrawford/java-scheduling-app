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
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Set;

public class AppointmentsMonthlyController {
    // view navigation
    @FXML private Button apptsNavButton; // don't think we need this button?
    @FXML private Button customerNavButton;
    @FXML private Button reportsNavButton;
    @FXML private Button logoutButton;

    @FXML private Label apptsLabel;

    @FXML private ToggleGroup apptsToggleGroup;
    @FXML private ToggleButton apptsMonthlyToggle;
    @FXML private ToggleButton apptsWeeklyToggle;
    // calendar elements
    @FXML private Label calendarCurrentDate;
    @FXML private Button calendarPreviousButton;
    @FXML private Button calendarNextButton;
    // cells for calendar
    @FXML private Pane calendarCell_1, calendarCell_2, calendarCell_3, calendarCell_4, calendarCell_5, calendarCell_6,
            calendarCell_7, calendarCell_8, calendarCell_9, calendarCell_10, calendarCell_11, calendarCell_12,
            calendarCell_13, calendarCell_14, calendarCell_15, calendarCell_16, calendarCell_17, calendarCell_18,
            calendarCell_19, calendarCell_20, calendarCell_21, calendarCell_22, calendarCell_23, calendarCell_24,
            calendarCell_25, calendarCell_26, calendarCell_27, calendarCell_28, calendarCell_29, calendarCell_30,
            calendarCell_31, calendarCell_32, calendarCell_33, calendarCell_34, calendarCell_35, calendarCell_36,
            calendarCell_37, calendarCell_38, calendarCell_39, calendarCell_40, calendarCell_41, calendarCell_42;
    @FXML private Text calendarCellNum_1, calendarCellNum_2, calendarCellNum_3, calendarCellNum_4, calendarCellNum_5,
            calendarCellNum_6, calendarCellNum_7, calendarCellNum_8, calendarCellNum_9, calendarCellNum_10,
            calendarCellNum_11, calendarCellNum_12, calendarCellNum_13, calendarCellNum_14, calendarCellNum_15,
            calendarCellNum_16, calendarCellNum_17, calendarCellNum_18, calendarCellNum_19, calendarCellNum_20,
            calendarCellNum_21, calendarCellNum_22, calendarCellNum_23, calendarCellNum_24, calendarCellNum_25,
            calendarCellNum_26, calendarCellNum_27, calendarCellNum_28, calendarCellNum_29, calendarCellNum_30,
            calendarCellNum_31, calendarCellNum_32, calendarCellNum_33, calendarCellNum_34, calendarCellNum_35,
            calendarCellNum_36, calendarCellNum_37, calendarCellNum_38, calendarCellNum_39, calendarCellNum_40,
            calendarCellNum_41, calendarCellNum_42;

    // tableview and list for appointments
    @FXML private TableView<Appointment> appointmentTableView;
    @FXML private TableColumn<Appointment, String> appointmentTableColumn;
    // appointment manipulations
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final ObservableList<Appointment> appointmentTableItems = FXCollections.observableArrayList();

    private final LinkedHashMap<Pane, Text> calendarHashMap = new LinkedHashMap<>();
    private Set<Pane> calendarHashMapSet = null;

    // State Variables
    private Pane currentCalendarCell = null;
    private static Appointment selectedAppointment = null;

    private Calendar calendar = Calendar.getInstance();
    private CachedData cachedData = new CachedData();

    @FXML
    private void initialize() {
        calendarCurrentDate.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
        // populate appointment table view
        appointmentTableColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        appointmentTableColumn.setText("No selection made");
        appointmentTableView.setItems(appointmentTableItems);

        populateCalendar();

        setNavigationButtonEvents();
        setWeeklyToggleEvent();
        setCalendarNavigateButtonEvents();
        setAppointmentEditingButtonEvents();

        setAppointmentTableListener();
    }

    private void populateCalendar() {
        // this is so janky but don't know a better way for linking these in grid pane?
        calendarHashMap.put(calendarCell_1, calendarCellNum_1);
        calendarHashMap.put(calendarCell_2, calendarCellNum_2);
        calendarHashMap.put(calendarCell_3, calendarCellNum_3);
        calendarHashMap.put(calendarCell_4, calendarCellNum_4);
        calendarHashMap.put(calendarCell_5, calendarCellNum_5);
        calendarHashMap.put(calendarCell_6, calendarCellNum_6);
        calendarHashMap.put(calendarCell_7, calendarCellNum_7);
        calendarHashMap.put(calendarCell_8, calendarCellNum_8);
        calendarHashMap.put(calendarCell_9, calendarCellNum_9);
        calendarHashMap.put(calendarCell_10, calendarCellNum_10);
        calendarHashMap.put(calendarCell_11, calendarCellNum_11);
        calendarHashMap.put(calendarCell_12, calendarCellNum_12);
        calendarHashMap.put(calendarCell_13, calendarCellNum_13);
        calendarHashMap.put(calendarCell_14, calendarCellNum_14);
        calendarHashMap.put(calendarCell_15, calendarCellNum_15);
        calendarHashMap.put(calendarCell_16, calendarCellNum_16);
        calendarHashMap.put(calendarCell_17, calendarCellNum_17);
        calendarHashMap.put(calendarCell_18, calendarCellNum_18);
        calendarHashMap.put(calendarCell_19, calendarCellNum_19);
        calendarHashMap.put(calendarCell_20, calendarCellNum_20);
        calendarHashMap.put(calendarCell_21, calendarCellNum_21);
        calendarHashMap.put(calendarCell_22, calendarCellNum_22);
        calendarHashMap.put(calendarCell_23, calendarCellNum_23);
        calendarHashMap.put(calendarCell_24, calendarCellNum_24);
        calendarHashMap.put(calendarCell_25, calendarCellNum_25);
        calendarHashMap.put(calendarCell_26, calendarCellNum_26);
        calendarHashMap.put(calendarCell_27, calendarCellNum_27);
        calendarHashMap.put(calendarCell_28, calendarCellNum_28);
        calendarHashMap.put(calendarCell_29, calendarCellNum_29);
        calendarHashMap.put(calendarCell_30, calendarCellNum_30);
        calendarHashMap.put(calendarCell_31, calendarCellNum_31);
        calendarHashMap.put(calendarCell_32, calendarCellNum_32);
        calendarHashMap.put(calendarCell_33, calendarCellNum_33);
        calendarHashMap.put(calendarCell_34, calendarCellNum_34);
        calendarHashMap.put(calendarCell_35, calendarCellNum_35);
        calendarHashMap.put(calendarCell_36, calendarCellNum_36);
        calendarHashMap.put(calendarCell_37, calendarCellNum_37);
        calendarHashMap.put(calendarCell_38, calendarCellNum_38);
        calendarHashMap.put(calendarCell_39, calendarCellNum_39);
        calendarHashMap.put(calendarCell_40, calendarCellNum_40);
        calendarHashMap.put(calendarCell_41, calendarCellNum_41);
        calendarHashMap.put(calendarCell_42, calendarCellNum_42);
        calendarHashMapSet = calendarHashMap.keySet();

        // need to loop through panes and set dates

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
                SchedulingApplication.switchScenes(Paths.mainLoginPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setWeeklyToggleEvent() {
        apptsWeeklyToggle.setOnAction(actionEvent -> {

        });
    }

    private void setCalendarNavigateButtonEvents() {
        calendarPreviousButton.setOnAction(actionEvent -> {

        });
        calendarNextButton.setOnAction(actionEvent -> {

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

    private void setAppointmentTableListener() {
        // lambda instead of change listener?
        appointmentTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (appointmentTableView.getSelectionModel().getSelectedItem() != null) {
                selectedAppointment = newValue;
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }
}
