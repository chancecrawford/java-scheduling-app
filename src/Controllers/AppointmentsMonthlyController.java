package Controllers;

import Models.Appointment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class AppointmentsMonthlyController {
    @FXML private Label calendarTitleLabel;
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
    @FXML private Button deleteButton;
    @FXML private Button editButton;

    @FXML private TableView<Appointment> appointmentTableView;
    @FXML private TableColumn<Appointment, String> appointmentTableColumn;

    @FXML
    private void initialize() {

    }
}
