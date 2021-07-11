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
    // javafx instantiation for ui elements
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

    // grab cache from main appointments view
    public static final CachedData cachedData = AppointmentsController.cachedData;
    // get selected user to edit from main appointments view
    private final Appointment selectedAppointment = AppointmentsController.getSelectedAppointment();

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

    /**
     * Grabs user inputs, validates inputs with {@link InputValidation#areAppointmentInputsValid}, which also checks if
     * appointment date and times are within business hours and do not conflict with other customer appointments, before
     * saving the new appointment to the database and local cache, and finally navigating the user back to the main
     * appointments view with a success alert.
     */
    private void setButtonActions() {
        saveButton.setOnAction(actionEvent -> {
            // make sure there is a selection for these dropdowns before passing to validation func
            Integer customerID = !customerChoiceBox.getSelectionModel().isEmpty() ? customerChoiceBox.getSelectionModel().getSelectedItem().getCustID() : null;
            Integer contactID = !contactChoiceBox.getSelectionModel().isEmpty() ? contactChoiceBox.getSelectionModel().getSelectedItem().getContactID() : null;

            // validate user inputs and appointment checks before moving forward;
            // generate error message if any fields are invalid or if conflicts exist
            try {
                if (InputValidation.areAppointmentInputsValid(
                        selectedAppointment.getApptID(),
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
                    // create save appointment query
                    PreparedStatement saveApptStatement = Database.getDBConnection().prepareStatement("UPDATE appointments " +
                            "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                            "WHERE Appointment_ID = ?");
                    // set values from user inputs in query
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
                        // generate success alert for user
                        Alerts.GenerateAlert(
                                "INFORMATION",
                                "Appointment Updated",
                                "Appointment Updated",
                                "Appointment has been successfully updated.",
                                "ShowAndWait"
                        );
                    }
                }
            } catch (ParseException | SQLException e) {
                e.printStackTrace();
                Alerts.GenerateAlert(
                        "ERROR",
                        "Appointment Update Error",
                        "Appointment Update Error",
                        "Appointment could not be updated!",
                        "ShowAndWait"
                );
            }
            // clear data for main appt table to repopulate with fresh data
            cachedData.clearAppointments();
            cachedData.clearContacts();
            cachedData.clearCustomers();
            // reset appointment selected
            AppointmentsController.setSelectedAppointment(null);
            // navigate back to main appointments view
            try {
                SchedulingApplication.switchScenes(Paths.appointmentsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction(actionEvent -> {
            // can't use Alerts class here due to needing to verify against user response from alert
            Alert cancelConfirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel?", ButtonType.OK, ButtonType.CANCEL);
            cancelConfirmAlert.showAndWait();
            // if user clicks ok, continue with navigation back to main appointments view
            if (cancelConfirmAlert.getResult() == ButtonType.OK) {
                // clear these from cache since we won't need it in main appt screen
                cachedData.clearAppointments();
                cachedData.clearContacts();
                cachedData.clearCustomers();
                AppointmentsController.setSelectedAppointment(null);
                try {
                    SchedulingApplication.switchScenes(Paths.appointmentsPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Disables days in date picker before current date as an extra layer of validation for appointments.
     */
    private void disableDaysBeforeToday() {
        apptDatePicker.setDayCellFactory(datePicker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
    }

    /**
     * Formats LocalTime cells to display as 12hr time format. Lambda is used for iterating through list cells to perform
     * updateItem function more efficiently.
     */
    private void formatTimes() {
        startComboBox.setCellFactory(localTimeListView -> new ListCell<>() {
            public void updateItem(LocalTime time, boolean empty) {
                super.updateItem(time, empty);
                if (time != null) {
                    try {
                        // convert time to am/pm format
                        setText(convertTimeToAMPM(time));
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
                        // convert time to am/pm format
                        setText(convertTimeToAMPM(time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    /**
     * Callback to reformat combobox selection for start/end
     */
    Callback<ListView<LocalTime>, ListCell<LocalTime>> timesCellFactory = new Callback<>() {
        @Override
        public ListCell<LocalTime> call(ListView<LocalTime> l) {
            return new ListCell<>() {
                @Override
                protected void updateItem(LocalTime time, boolean empty) {
                    super.updateItem(time, empty);
                    if (time != null) {
                        try {
                            // convert time to am/pm format
                            setText(convertTimeToAMPM(time));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
    };

    /**
     * This converts the LocalTime object selected in the start/end combo boxes to 12hr am/pm formats for better
     * readability for the user.
     * @param time time selection from start/end combo boxes
     * @return time formatted to 12hr am/pm format
     * @throws ParseException in case LocalTime object can't be converted to new format
     */
    public static String convertTimeToAMPM(LocalTime time) throws ParseException {
        Date temp24hrDate = new SimpleDateFormat("HH:mm").parse(time.toString());
        return new SimpleDateFormat("hh:mm a").format(temp24hrDate);
    }
}
