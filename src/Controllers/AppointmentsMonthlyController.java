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
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Set;

public class AppointmentsMonthlyController {
    // view navigation
    @FXML private Button customerNavButton, reportsNavButton, logoutButton;
    // toggle to weekly view
    @FXML private ToggleButton apptsWeeklyToggle;
    // calendar elements
    @FXML private Label calendarMonthYearLabel;
    @FXML private Button calendarPreviousButton, calendarNextButton;
    // cells for calendar
    @FXML private Pane
            calendarCell_1, calendarCell_2, calendarCell_3, calendarCell_4, calendarCell_5, calendarCell_6,
            calendarCell_7, calendarCell_8, calendarCell_9, calendarCell_10, calendarCell_11, calendarCell_12,
            calendarCell_13, calendarCell_14, calendarCell_15, calendarCell_16, calendarCell_17, calendarCell_18,
            calendarCell_19, calendarCell_20, calendarCell_21, calendarCell_22, calendarCell_23, calendarCell_24,
            calendarCell_25, calendarCell_26, calendarCell_27, calendarCell_28, calendarCell_29, calendarCell_30,
            calendarCell_31, calendarCell_32, calendarCell_33, calendarCell_34, calendarCell_35, calendarCell_36,
            calendarCell_37, calendarCell_38, calendarCell_39, calendarCell_40, calendarCell_41, calendarCell_42;
    @FXML private Text
            calendarCellNum_1, calendarCellNum_2, calendarCellNum_3, calendarCellNum_4, calendarCellNum_5,
            calendarCellNum_6, calendarCellNum_7, calendarCellNum_8, calendarCellNum_9, calendarCellNum_10,
            calendarCellNum_11, calendarCellNum_12, calendarCellNum_13, calendarCellNum_14, calendarCellNum_15,
            calendarCellNum_16, calendarCellNum_17, calendarCellNum_18, calendarCellNum_19, calendarCellNum_20,
            calendarCellNum_21, calendarCellNum_22, calendarCellNum_23, calendarCellNum_24, calendarCellNum_25,
            calendarCellNum_26, calendarCellNum_27, calendarCellNum_28, calendarCellNum_29, calendarCellNum_30,
            calendarCellNum_31, calendarCellNum_32, calendarCellNum_33, calendarCellNum_34, calendarCellNum_35,
            calendarCellNum_36, calendarCellNum_37, calendarCellNum_38, calendarCellNum_39, calendarCellNum_40,
            calendarCellNum_41, calendarCellNum_42;

    // Calendar cell click events
    @FXML private void calendarDayClicked_1() { selectDay(calendarCell_1, calendarCellNum_1); }
    @FXML private void calendarDayClicked_2() { selectDay(calendarCell_2, calendarCellNum_2); }
    @FXML private void calendarDayClicked_3() { selectDay(calendarCell_3, calendarCellNum_3); }
    @FXML private void calendarDayClicked_4() { selectDay(calendarCell_4, calendarCellNum_4); }
    @FXML private void calendarDayClicked_5() { selectDay(calendarCell_5, calendarCellNum_5); }
    @FXML private void calendarDayClicked_6() { selectDay(calendarCell_6, calendarCellNum_6); }
    @FXML private void calendarDayClicked_7() { selectDay(calendarCell_7, calendarCellNum_7); }
    @FXML private void calendarDayClicked_8() { selectDay(calendarCell_8, calendarCellNum_8); }
    @FXML private void calendarDayClicked_9() { selectDay(calendarCell_9, calendarCellNum_9); }
    @FXML private void calendarDayClicked_10() { selectDay(calendarCell_10, calendarCellNum_10); }
    @FXML private void calendarDayClicked_11() { selectDay(calendarCell_11, calendarCellNum_11); }
    @FXML private void calendarDayClicked_12() { selectDay(calendarCell_12, calendarCellNum_12); }
    @FXML private void calendarDayClicked_13() { selectDay(calendarCell_13, calendarCellNum_13); }
    @FXML private void calendarDayClicked_14() { selectDay(calendarCell_14, calendarCellNum_14); }
    @FXML private void calendarDayClicked_15() { selectDay(calendarCell_15, calendarCellNum_15); }
    @FXML private void calendarDayClicked_16() { selectDay(calendarCell_16, calendarCellNum_16); }
    @FXML private void calendarDayClicked_17() { selectDay(calendarCell_17, calendarCellNum_17); }
    @FXML private void calendarDayClicked_18() { selectDay(calendarCell_18, calendarCellNum_18); }
    @FXML private void calendarDayClicked_19() { selectDay(calendarCell_19, calendarCellNum_19); }
    @FXML private void calendarDayClicked_20() { selectDay(calendarCell_20, calendarCellNum_20); }
    @FXML private void calendarDayClicked_21() { selectDay(calendarCell_21, calendarCellNum_21); }
    @FXML private void calendarDayClicked_22() { selectDay(calendarCell_22, calendarCellNum_22); }
    @FXML private void calendarDayClicked_23() { selectDay(calendarCell_23, calendarCellNum_23); }
    @FXML private void calendarDayClicked_24() { selectDay(calendarCell_24, calendarCellNum_24); }
    @FXML private void calendarDayClicked_25() { selectDay(calendarCell_25, calendarCellNum_25); }
    @FXML private void calendarDayClicked_26() { selectDay(calendarCell_26, calendarCellNum_26); }
    @FXML private void calendarDayClicked_27() { selectDay(calendarCell_27, calendarCellNum_27); }
    @FXML private void calendarDayClicked_28() { selectDay(calendarCell_28, calendarCellNum_28); }
    @FXML private void calendarDayClicked_29() { selectDay(calendarCell_29, calendarCellNum_29); }
    @FXML private void calendarDayClicked_30() { selectDay(calendarCell_30, calendarCellNum_30); }
    @FXML private void calendarDayClicked_31() { selectDay(calendarCell_31, calendarCellNum_31); }
    @FXML private void calendarDayClicked_32() { selectDay(calendarCell_32, calendarCellNum_32); }
    @FXML private void calendarDayClicked_33() { selectDay(calendarCell_33, calendarCellNum_33); }
    @FXML private void calendarDayClicked_34() { selectDay(calendarCell_34, calendarCellNum_34); }
    @FXML private void calendarDayClicked_35() { selectDay(calendarCell_35, calendarCellNum_35); }
    @FXML private void calendarDayClicked_36() { selectDay(calendarCell_36, calendarCellNum_36); }
    @FXML private void calendarDayClicked_37() { selectDay(calendarCell_37, calendarCellNum_37); }
    @FXML private void calendarDayClicked_38() { selectDay(calendarCell_38, calendarCellNum_38); }
    @FXML private void calendarDayClicked_39() { selectDay(calendarCell_39, calendarCellNum_39); }
    @FXML private void calendarDayClicked_40() { selectDay(calendarCell_40, calendarCellNum_40); }
    @FXML private void calendarDayClicked_41() { selectDay(calendarCell_41, calendarCellNum_41); }
    @FXML private void calendarDayClicked_42() { selectDay(calendarCell_42, calendarCellNum_42); }

    // tableview and list for appointments
    @FXML private TableView<Appointment> appointmentTableView;
    @FXML private TableColumn<Appointment, String> appointmentTableColumn;
    // appointment manipulations
    @FXML private Button addButton, editButton, deleteButton;

    private final ObservableList<Appointment> appointmentTableItems = FXCollections.observableArrayList();
    // used to link elements in cells for days in each month
    private final LinkedHashMap<Pane, Text> calendarMonthHashMap = new LinkedHashMap<>();

    // for keeping track of selections
    private Pane selectedDay = null;
    private static Appointment selectedAppointment = null;

    // for passing/retrieving selected appointment between scenes
    public static Appointment getSelectedAppointment() { return selectedAppointment; }
    public static void setSelectedAppointment(Appointment appointment) { selectedAppointment = appointment; }

    private final Calendar calendar = Calendar.getInstance();
    private final CachedData cachedData = new CachedData();

    @FXML
    private void initialize() {
        // populate appointment table view
        appointmentTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentTableColumn.setText("No selection made");
        appointmentTableView.setItems(appointmentTableItems);

        populateCalendar();
        // set links and actions for buttons
        setNavigationButtonEvents();
        setWeeklyToggleEvent();
        setCalendarNavigateButtonEvents();
        setAppointmentEditingButtonEvents();
        // set listener to update appt table with appts for selected day
        setAppointmentMonthlyTableListener();
        cachedData.importAppointments();

//        for (Appointment appt: cachedData.getAllAppointments()) {
//            System.out.println(appt.getApptID());
//            System.out.println(appt.getTitle());
//            System.out.println(appt.getType());
//            System.out.println(appt.getDescription());
//        }
    }

    private void populateCalendar() {
        calendarMonthYearLabel.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "monthYear"));
        // this is so janky but don't know a better way for linking these in grid pane?
        calendarMonthHashMap.put(calendarCell_1, calendarCellNum_1);
        calendarMonthHashMap.put(calendarCell_2, calendarCellNum_2);
        calendarMonthHashMap.put(calendarCell_3, calendarCellNum_3);
        calendarMonthHashMap.put(calendarCell_4, calendarCellNum_4);
        calendarMonthHashMap.put(calendarCell_5, calendarCellNum_5);
        calendarMonthHashMap.put(calendarCell_6, calendarCellNum_6);
        calendarMonthHashMap.put(calendarCell_7, calendarCellNum_7);
        calendarMonthHashMap.put(calendarCell_8, calendarCellNum_8);
        calendarMonthHashMap.put(calendarCell_9, calendarCellNum_9);
        calendarMonthHashMap.put(calendarCell_10, calendarCellNum_10);
        calendarMonthHashMap.put(calendarCell_11, calendarCellNum_11);
        calendarMonthHashMap.put(calendarCell_12, calendarCellNum_12);
        calendarMonthHashMap.put(calendarCell_13, calendarCellNum_13);
        calendarMonthHashMap.put(calendarCell_14, calendarCellNum_14);
        calendarMonthHashMap.put(calendarCell_15, calendarCellNum_15);
        calendarMonthHashMap.put(calendarCell_16, calendarCellNum_16);
        calendarMonthHashMap.put(calendarCell_17, calendarCellNum_17);
        calendarMonthHashMap.put(calendarCell_18, calendarCellNum_18);
        calendarMonthHashMap.put(calendarCell_19, calendarCellNum_19);
        calendarMonthHashMap.put(calendarCell_20, calendarCellNum_20);
        calendarMonthHashMap.put(calendarCell_21, calendarCellNum_21);
        calendarMonthHashMap.put(calendarCell_22, calendarCellNum_22);
        calendarMonthHashMap.put(calendarCell_23, calendarCellNum_23);
        calendarMonthHashMap.put(calendarCell_24, calendarCellNum_24);
        calendarMonthHashMap.put(calendarCell_25, calendarCellNum_25);
        calendarMonthHashMap.put(calendarCell_26, calendarCellNum_26);
        calendarMonthHashMap.put(calendarCell_27, calendarCellNum_27);
        calendarMonthHashMap.put(calendarCell_28, calendarCellNum_28);
        calendarMonthHashMap.put(calendarCell_29, calendarCellNum_29);
        calendarMonthHashMap.put(calendarCell_30, calendarCellNum_30);
        calendarMonthHashMap.put(calendarCell_31, calendarCellNum_31);
        calendarMonthHashMap.put(calendarCell_32, calendarCellNum_32);
        calendarMonthHashMap.put(calendarCell_33, calendarCellNum_33);
        calendarMonthHashMap.put(calendarCell_34, calendarCellNum_34);
        calendarMonthHashMap.put(calendarCell_35, calendarCellNum_35);
        calendarMonthHashMap.put(calendarCell_36, calendarCellNum_36);
        calendarMonthHashMap.put(calendarCell_37, calendarCellNum_37);
        calendarMonthHashMap.put(calendarCell_38, calendarCellNum_38);
        calendarMonthHashMap.put(calendarCell_39, calendarCellNum_39);
        calendarMonthHashMap.put(calendarCell_40, calendarCellNum_40);
        calendarMonthHashMap.put(calendarCell_41, calendarCellNum_41);
        calendarMonthHashMap.put(calendarCell_42, calendarCellNum_42);
        Set<Pane> calendarHashMapSet = calendarMonthHashMap.keySet();

        // Set calendar initial values
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        int calendarMapIndex = 1;
        boolean endOfMonth = false;

        // Iterate through the cells and populate them with correct day number
        for (Pane pane: calendarHashMapSet) {

            // need css stylesheet for altering element appearance here as well

            // If the calendar index is greater-than or equal to the first weekday of the month, start the iteration
            if (calendarMapIndex >= calendar.get(Calendar.DAY_OF_WEEK) && !endOfMonth) {
                calendarMonthHashMap.get(pane).setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
                pane.getStyleClass().remove("cal-empty-cell");

                // check for any appointments this day
                if (!cachedData.getAppointmentsByDate(DateFormatter.formatToSimpleDate((calendar.getTime()), "iso")).isEmpty()) {
                    if (!calendarMonthHashMap.get(pane).getStyleClass().contains("cal-bold-text")) {
                        calendarMonthHashMap.get(pane).getStyleClass().add("cal-bold-text");
                    }
                } else {
                    calendarMonthHashMap.get(pane).getStyleClass().remove("cal-bold-text");
                }

                // Check if this day is today's date
                if (DateFormatter.formatToSimpleDate(calendar.getTime(), "iso").equals(DateFormatter.formatToIsoDate(LocalDateTime.now()))) {
                    if (!calendarMonthHashMap.get(pane).getStyleClass().contains("cal-blue-text")) {
                        calendarMonthHashMap.get(pane).getStyleClass().add("cal-blue-text");
                        selectDay(pane, calendarMonthHashMap.get(pane));
                    }
                } else {
                    calendarMonthHashMap.get(pane).getStyleClass().remove("cal-blue-text");
                }

                if (calendar.get(Calendar.DAY_OF_MONTH) != calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                } else {
                    endOfMonth = true;
                }

                // Otherwise remove the text and color the pane gray
            } else {
                calendarMonthHashMap.get(pane).setText("");
                if (!pane.getStyleClass().contains("cal-empty-cell")) {
                    pane.getStyleClass().add("cal-empty-cell");
                }
            }
            calendarMapIndex++;
        }
    }

    private void selectDay(Pane dayCell, Text dayText) {
        // TODO: create css file for styles
        // or can we bold/outline the panes directly here?
//        if (selectedDay != null) {
//            selectedDay.setStyle(null);
//        }

        selectedDay = dayCell;
        // add indicator of selection here
//        selectedDay.setStyle("-fx-background-color: #C1C1C1");
//        selectedDay.setStyle("-fx-font-weight: bold");

        // update appointment table header
        appointmentTableColumn.setText(DateFormatter.formatToSimpleDate(calendar.getTime(), "month") + " " + dayText.getText());

        // clear items before date match check
        appointmentTableItems.clear();
        appointmentTableView.setItems(appointmentTableItems);

        // check for appointments for that user on that day
        String selectedDayToDate = DateFormatter.formatToSimpleDate(calendar.getTime(), "isoYearMonth") + "-" + dayText.getText();
        ObservableList<Appointment> currentAppointments =
                cachedData.getAppointmentsByDate(selectedDayToDate);

        // populate table with appointments if any exist
        if (!currentAppointments.isEmpty()) {
            appointmentTableItems.addAll(currentAppointments);
            appointmentTableView.setItems(appointmentTableItems);
        }

        // disable appointment edit/delete buttons
        editButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void unselectDay() {
        if (selectedDay != null) {
            // remove styles here
        }
        selectedDay = null;
        // clear and reset appointment table
        appointmentTableColumn.setText("No selection made");
        appointmentTableItems.clear();
        appointmentTableView.setItems(appointmentTableItems);
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

    private void setWeeklyToggleEvent() {
        apptsWeeklyToggle.setOnAction(actionEvent -> {
            try {
                unselectDay();
                SchedulingApplication.switchScenes(Paths.appointmentsWeeklyPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setCalendarNavigateButtonEvents() {
        calendarPreviousButton.setOnAction(actionEvent -> {
            unselectDay();
            calendar.add(Calendar.MONTH, -1);
            populateCalendar();
        });
        calendarNextButton.setOnAction(actionEvent -> {
            unselectDay();
            calendar.add(Calendar.MONTH, 1);
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

    private void setAppointmentMonthlyTableListener() {
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
