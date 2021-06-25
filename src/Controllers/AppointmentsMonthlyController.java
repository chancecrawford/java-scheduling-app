package Controllers;

import Models.Appointment;
import Utils.CachedData;
import Utils.DateFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Calendar;
import java.util.LinkedHashMap;

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

    private CachedData cachedData = new CachedData();

    private Calendar calendar = Calendar.getInstance();

    @FXML
    private void initialize() {
        calendarCurrentDate.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));

        setNavigationButtonEvents();
    }

    private void setNavigationButtonEvents() {
        customerNavButton.setOnAction(actionEvent -> {

        });
        reportsNavButton.setOnAction(actionEvent -> {

        });
        logoutButton.setOnAction(actionEvent -> {

        });
    }

    private void weeklyToggleEvent() {
        apptsWeeklyToggle.setOnAction(actionEvent -> {

        });
    }

    private void calendarNavigateButtonEvents() {
        calendarPreviousButton.setOnAction(actionEvent -> {

        });
        calendarNextButton.setOnAction(actionEvent -> {

        });
    }

    private void appointmentEditingButtonEvents() {
        addButton.setOnAction(actionEvent -> {

        });
        editButton.setOnAction(actionEvent -> {

        });
        deleteButton.setOnAction(actionEvent -> {

        });
    }
}
