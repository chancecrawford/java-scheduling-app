package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Contact;
import Models.Customer;
import Utils.CachedData;
import Utils.InputValidation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class AddAppointmentController {
    @FXML private TextField titleTextField, locationTextField;
    @FXML private ChoiceBox<String> typeChoiceBox;
    @FXML private ChoiceBox<Customer> customerChoiceBox;
    @FXML private ChoiceBox<Contact> contactChoiceBox;
    @FXML private TextArea descriptionTextArea;
    @FXML private DatePicker apptDatePicker;
    @FXML private ComboBox<LocalTime> startComboBox, endComboBox;
    @FXML private Label businessHoursLabel;
    @FXML private Button addButton, cancelButton;

    public static final CachedData cachedData = AppointmentsController.cachedData;

    @FXML
    private void initialize() {
        // import needed data
        cachedData.importContacts();
        cachedData.importCustomers();
        // set data and display values
        typeChoiceBox.setItems(cachedData.getAppointmentTypes());
        customerChoiceBox.setItems(cachedData.getAllCustomers());
        contactChoiceBox.setItems(cachedData.getAllContacts());

        apptDatePicker.setValue(LocalDateTime.now().toLocalDate());
        disableDaysBeforeToday();
        // populate times from cached data
        startComboBox.setItems(cachedData.getAppointmentTimes());
        endComboBox.setItems(cachedData.getAppointmentTimes());
        // format because 24hr clock time confuses people
        startComboBox.setButtonCell(timesCellFactory.call(null));
        endComboBox.setButtonCell(timesCellFactory.call(null));
        formatTimes();
        // set label for business hours
        businessHoursLabel.setText(cachedData.businessOpen + " - " + cachedData.businessClose + " EST");

        setButtonActions();
    }

    private void setButtonActions() {
        addButton.setOnAction(actionEvent -> {
            // make sure there is a selection for these dropdowns before passing to validation func
            Integer customerID = !customerChoiceBox.getSelectionModel().isEmpty() ? customerChoiceBox.getSelectionModel().getSelectedItem().getCustID() : null;
            Integer contactID = !contactChoiceBox.getSelectionModel().isEmpty() ? contactChoiceBox.getSelectionModel().getSelectedItem().getContactID() : null;

            try {
                if (InputValidation.areAppointmentInputsValid(
                        titleTextField.getText(),
                        typeChoiceBox.getSelectionModel().getSelectedItem(),
                        customerID,
                        contactID,
                        locationTextField.getText(),
                        apptDatePicker.getValue(),
                        startComboBox.getSelectionModel().getSelectedItem(),
                        endComboBox.getSelectionModel().getSelectedItem()
                )) {
                    System.out.println("Yay!");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction(actionEvent -> {
            try {
                // TODO: add dialog for confirmation before cancelling
                // clear these from cache since we won't need it in main appt screen
                cachedData.clearContacts();
                cachedData.clearCustomers();
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

    // formats LocalTime cells to display as 12hr time format
    private void formatTimes() {
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
    // callback to reformat combobox selection for start/end
    Callback<ListView<LocalTime>, ListCell<LocalTime>> timesCellFactory = new Callback<>() {
        @Override
        public ListCell<LocalTime> call(ListView<LocalTime> l) {
            return new ListCell<>() {
                @Override
                protected void updateItem(LocalTime time, boolean empty) {
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
            };
        }
    };
}
