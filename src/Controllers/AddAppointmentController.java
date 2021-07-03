package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Contact;
import Utils.CachedData;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class AddAppointmentController {
    @FXML
    private TextField titleTextField, locationTextField;
    @FXML
    private ChoiceBox<String> typeChoiceBox, customerChoiceBox, contactChoiceBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private DatePicker apptDatePicker;
    @FXML
    private ComboBox<LocalTime> startComboBox, endComboBox;
    @FXML
    private Label businessHoursLabel;
    @FXML
    private Button addButton, cancelButton;

    public static final CachedData cachedData = AppointmentsController.cachedData;

    private Contact selectedContact;

    @FXML
    private void initialize() {
        typeChoiceBox.setItems(cachedData.getAppointmentTypes());
        customerChoiceBox.setItems(cachedData.getCustomersByName());
        contactChoiceBox.setItems(cachedData.getContactsByName());

        apptDatePicker.setValue(LocalDateTime.now().toLocalDate());
        disableDaysBeforeToday();
        // populate times from cached data
        startComboBox.setItems(cachedData.getAppointmentTimes());
        endComboBox.setItems(cachedData.getAppointmentTimes());
        // format because 24hr clock time confuses people
        formatTimes();

        setButtonActions();
    }

    private void setButtonActions() {
        addButton.setOnAction(actionEvent -> {

        });
        cancelButton.setOnAction(actionEvent -> {
            try {
                // TODO: add dialog for confirmation before cancelling
                SchedulingApplication.switchScenes(Paths.appointmentsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void disableDaysBeforeToday() {
        apptDatePicker.setDayCellFactory(datePicker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
    }

    private void formatTimes() {
        // oh my god, this doesn't format the button cell
        // saving this shit for tomorrow
        // this is why Julius Caeser was stabbed to death
        startComboBox.setCellFactory(localTimeListView -> new ListCell<>() {
            public void updateItem(LocalTime time, boolean empty) {
                super.updateItem(time, empty);
                if (time != null) {
                    try {
                        // clean this up and probs add it to DateFormatter
                        SimpleDateFormat _24Format = new SimpleDateFormat("HH:mm");
                        SimpleDateFormat _12Format = new SimpleDateFormat("hh:mm a");
                        Date _24Date = _24Format.parse(time.toString());
                        setText(_12Format.format(_24Date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        endComboBox.setCellFactory(localTimeListView -> new ListCell<>() {
            public void updateItem(LocalTime time, boolean empty) {
                super.updateItem(time, empty);
                if (time != null) {
                    try {
                        // clean this up and probs add it to DateFormatter
                        SimpleDateFormat _24Format = new SimpleDateFormat("HH:mm");
                        SimpleDateFormat _12Format = new SimpleDateFormat("hh:mm a");
                        Date _24Date = _24Format.parse(time.toString());
                        setText(_12Format.format(_24Date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
