package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Appointment;
import Models.Contact;
import Models.Customer;
import Utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
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
        convertTimeSelectionCells();
        // set label for business hours
        businessHoursLabel.setText(cachedData.businessOpen + " - " + cachedData.businessClose + " EST");

        setButtonActions();
    }

    private void setButtonActions() {
        addButton.setOnAction(actionEvent -> {
            // generate random appt id
            int tempApptID = InputValidation.generateAppointmentID();
            // make sure there is a selection for these dropdowns before passing to validation func
            Integer customerID = !customerChoiceBox.getSelectionModel().isEmpty() ? customerChoiceBox.getSelectionModel().getSelectedItem().getCustID() : null;
            Integer contactID = !contactChoiceBox.getSelectionModel().isEmpty() ? contactChoiceBox.getSelectionModel().getSelectedItem().getContactID() : null;

            try {
                if (InputValidation.areAppointmentInputsValid(
                        tempApptID,
                        titleTextField.getText(),
                        typeChoiceBox.getSelectionModel().getSelectedItem(),
                        customerID,
                        contactID,
                        locationTextField.getText(),
                        apptDatePicker.getValue(),
                        startComboBox.getSelectionModel().getSelectedItem(),
                        endComboBox.getSelectionModel().getSelectedItem()
                )) {
                    // get local date time
                    LocalDateTime startLDT = LocalDateTime.of(apptDatePicker.getValue(), startComboBox.getValue());
                    LocalDateTime endLDT = LocalDateTime.of(apptDatePicker.getValue(), endComboBox.getValue());
                    // convert to UTC
                    LocalDateTime startUTC = (startLDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
                    LocalDateTime endUTC = (endLDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());

                    PreparedStatement saveApptStatement = Database.getDBConnection().prepareStatement("INSERT INTO appointments " +
                            "(Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    saveApptStatement.setInt(1, tempApptID);
                    saveApptStatement.setString(2, titleTextField.getText().trim());
                    saveApptStatement.setString(3, descriptionTextArea.getText());
                    saveApptStatement.setString(4, locationTextField.getText().trim());
                    saveApptStatement.setString(5, typeChoiceBox.getValue().trim());
                    saveApptStatement.setTimestamp(6, Timestamp.valueOf(startUTC));
                    saveApptStatement.setTimestamp(7, Timestamp.valueOf(endUTC));
                    saveApptStatement.setInt(8, customerID);
                    saveApptStatement.setInt(9, SchedulingApplication.getUser().getId());
                    saveApptStatement.setInt(10, contactID);

                    //check if appt saved or not
                    int saveResult = saveApptStatement.executeUpdate();
                    if (saveResult == 1) {
                        // add new appointment to cached data since save confirmed
                        cachedData.addAppointment(new Appointment(
                            tempApptID,
                                titleTextField.getText().trim(),
                                descriptionTextArea.getText(),
                                locationTextField.getText().trim(),
                                typeChoiceBox.getValue().trim(),
                                startLDT,
                                endLDT,
                                customerID,
                                SchedulingApplication.getUser().getId(),
                                contactID
                        ));
                        // TODO: generate alert then close back to application scene after user closes alert
                        System.out.println("----- Appointment Saved! -----");
                    }
                }
            } catch (ParseException | SQLException e) {
                e.printStackTrace();
                Alerts.GenerateAlert("ERROR", "Appointment Save Error", "Appointment Save Error", "Appointment could not be saved!", "ShowAndWait");
            }
            cachedData.clearAppointments();
            cachedData.clearContacts();
            cachedData.clearCustomers();
            try {
                SchedulingApplication.switchScenes(Paths.appointmentsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction(actionEvent -> {
                // clear these from cache since we won't need it in main appt screen
                cachedData.clearAppointments();
                cachedData.clearContacts();
                cachedData.clearCustomers();
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

    // formats LocalTime cells to display as 12hr time format
    private void convertTimeSelectionCells() {
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
