package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Appointment;
import Models.Contact;
import Models.Customer;
import Utils.Alerts;
import Utils.CachedData;
import Utils.Database;
import Utils.InputValidation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class EditAppointmentController {
    @FXML
    private TextField titleTextField, locationTextField;
    @FXML private ChoiceBox<String> typeChoiceBox;
    @FXML private ChoiceBox<Customer> customerChoiceBox;
    @FXML private ChoiceBox<Contact> contactChoiceBox;
    @FXML private TextArea descriptionTextArea;
    @FXML private DatePicker apptDatePicker;
    @FXML private ComboBox<LocalTime> startComboBox, endComboBox;
    @FXML private Label apptIDLabel, businessHoursLabel;
    @FXML private Button saveButton, cancelButton;

    public static final CachedData cachedData = AppointmentsController.cachedData;
    public static final Appointment selectedAppointment = AppointmentsController.getSelectedAppointment();

    @FXML
    private void initialize() {
        // import needed data
        cachedData.importContacts();
        cachedData.importCustomers();
        // set data and display values
        apptIDLabel.setText(String.valueOf(selectedAppointment.getApptID()));
        // appt title
        titleTextField.setText(selectedAppointment.getTitle());
        // appt type
        typeChoiceBox.setItems(cachedData.getAppointmentTypes());
        typeChoiceBox.setValue(selectedAppointment.getType());
        // appt description
        descriptionTextArea.setText(selectedAppointment.getDescription());
        // customer selection
        customerChoiceBox.setItems(cachedData.getAllCustomers());
        customerChoiceBox.setValue(cachedData.getCustomerByID(selectedAppointment.getCustomerID()));
        // contact selection
        contactChoiceBox.setItems(cachedData.getAllContacts());
        contactChoiceBox.setValue(cachedData.getContactByID(selectedAppointment.getContactID()));
        // appt location
        locationTextField.setText(selectedAppointment.getLocation());
        // set date from selected appt
        apptDatePicker.setValue(selectedAppointment.getStart().toLocalDate());
        disableDaysBeforeToday();
        // populate times from cached data and set from selected appt
        startComboBox.setItems(cachedData.getAppointmentTimes());
        startComboBox.setValue(selectedAppointment.getStart().toLocalTime());
        endComboBox.setItems(cachedData.getAppointmentTimes());
        endComboBox.setValue(selectedAppointment.getEnd().toLocalTime());
        // format because 24hr clock time confuses people
        startComboBox.setButtonCell(timesCellFactory.call(null));
        endComboBox.setButtonCell(timesCellFactory.call(null));
        formatTimes();
        // set label for business hours
        businessHoursLabel.setText(cachedData.businessOpen + " - " + cachedData.businessClose + " EST");

        setButtonActions();
    }

    private void setButtonActions() {
        saveButton.setOnAction(actionEvent -> {
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
                    // get local date time
                    LocalDateTime startLDT = LocalDateTime.of(apptDatePicker.getValue(), startComboBox.getValue());
                    LocalDateTime endLDT = LocalDateTime.of(apptDatePicker.getValue(), endComboBox.getValue());
                    // convert to UTC
                    LocalDateTime startUTC = (startLDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
                    LocalDateTime endUTC = (endLDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());

                    PreparedStatement saveApptStatement = Database.getDBConnection().prepareStatement("UPDATE appointments " +
                            "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                            "WHERE Appointment_ID = ?");
                    saveApptStatement.setString(1, titleTextField.getText().trim());
                    saveApptStatement.setString(2, descriptionTextArea.getText());
                    saveApptStatement.setString(3, locationTextField.getText().trim());
                    saveApptStatement.setString(4, typeChoiceBox.getValue().trim());
                    saveApptStatement.setTimestamp(5, Timestamp.valueOf(startUTC));
                    saveApptStatement.setTimestamp(6, Timestamp.valueOf(endUTC));
                    saveApptStatement.setInt(7, customerID);
                    saveApptStatement.setInt(8, SchedulingApplication.getUser().getId());
                    saveApptStatement.setInt(9, contactID);
                    saveApptStatement.setInt(10, selectedAppointment.getApptID());

                    //check if appt saved or not
                    int updateResult = saveApptStatement.executeUpdate();
                    if (updateResult == 1) {
                        // update appointment in cached data since db update confirmed
                        selectedAppointment.setTitle(titleTextField.getText().trim());
                        selectedAppointment.setDescription(descriptionTextArea.getText());
                        selectedAppointment.setLocation(locationTextField.getText().trim());
                        selectedAppointment.setType(typeChoiceBox.getValue().trim());
                        selectedAppointment.setStart(startLDT);
                        selectedAppointment.setEnd(endLDT);
                        selectedAppointment.setCustomerID(customerID);
                        selectedAppointment.setUserID(SchedulingApplication.getUser().getId());
                        selectedAppointment.setContactID(contactID);
                        // TODO: generate alert then close back to application scene after user closes alert
                        System.out.println("----- Appointment Updated! -----");
                    }
                }
            } catch (ParseException | SQLException e) {
                e.printStackTrace();
                Alerts.GenerateAlert("ERROR", "Appointment Update Error", "Appointment Update Error", "Appointment could not be updated!", "ShowAndWait");
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
            AppointmentsController.setSelectedAppointment(null);
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
