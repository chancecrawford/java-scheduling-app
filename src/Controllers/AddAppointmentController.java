package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Appointment;
import Models.Contact;
import Models.Customer;
import Models.User;
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

/**
 * Controller dedicated to generating the add appointment scene, allowing input from user for new appointment, running
 * user inputs through input validation, and saving the new appointment to the database and local cache before navigating
 * the user back to the main apppointments view.
 */
public class AddAppointmentController {
    // javafx instantiation for ui elements
    @FXML private TextField titleTextField, locationTextField;
    @FXML private ChoiceBox<String> typeChoiceBox;
    @FXML private ChoiceBox<Customer> customerChoiceBox;
    @FXML private ChoiceBox<Contact> contactChoiceBox;
    @FXML private ChoiceBox<User> userChoiceBox;
    @FXML private TextArea descriptionTextArea;
    @FXML private DatePicker apptDatePicker;
    @FXML private ComboBox<LocalTime> startComboBox, endComboBox;
    @FXML private Label businessHoursLabel;
    @FXML private Button addButton, cancelButton;

    // grab cached data from main appointments view
    public static final CachedData cachedData = AppointmentsController.cachedData;

    /**
     * Initializes and populates all ui elements with needed data for user to add appointment information.
     */
    @FXML
    private void initialize() {
        // import needed data
        cachedData.importContacts();
        cachedData.importCustomers();
        cachedData.importUsers();
        // set data and display values
        typeChoiceBox.setItems(cachedData.getAppointmentTypes());
        customerChoiceBox.setItems(cachedData.getAllCustomers());
        contactChoiceBox.setItems(cachedData.getAllContacts());
        userChoiceBox.setItems(cachedData.getAllUsers());
        // set date picker to todays date
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

    /**
     * Grabs user inputs, validates inputs with {@link InputValidation#areAppointmentInputsValid}, which also checks if
     * appointment date and times are within business hours and do not conflict with other customer appointments, before
     * saving the new appointment to the database and local cache, and finally navigating the user back to the main
     * appointments view with a success alert.
     */
    private void setButtonActions() {
        addButton.setOnAction(actionEvent -> {
            // generate random appt id
            int tempApptID = InputValidation.generateAppointmentID();
            // make sure there is a selection for these dropdowns before passing to validation func
            Integer customerID = !customerChoiceBox.getSelectionModel().isEmpty() ? customerChoiceBox.getSelectionModel().getSelectedItem().getCustID() : null;
            Integer contactID = !contactChoiceBox.getSelectionModel().isEmpty() ? contactChoiceBox.getSelectionModel().getSelectedItem().getContactID() : null;
            Integer userID = !userChoiceBox.getSelectionModel().isEmpty() ? userChoiceBox.getSelectionModel().getSelectedItem().getId() : null;

            // validate user inputs and appointment checks before moving forward;
            // generate error message if any fields are invalid or if conflicts exist
            try {
                if (InputValidation.areAppointmentInputsValid(
                        tempApptID,
                        titleTextField.getText(),
                        typeChoiceBox.getSelectionModel().getSelectedItem(),
                        customerID,
                        contactID,
                        userID,
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
                    PreparedStatement saveApptStatement = Database.getDBConnection().prepareStatement("INSERT INTO appointments " +
                            "(Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    // set values from user inputs in query
                    saveApptStatement.setInt(1, tempApptID);
                    saveApptStatement.setString(2, titleTextField.getText().trim());
                    saveApptStatement.setString(3, descriptionTextArea.getText());
                    saveApptStatement.setString(4, locationTextField.getText().trim());
                    saveApptStatement.setString(5, typeChoiceBox.getValue().trim());
                    saveApptStatement.setTimestamp(6, Timestamp.valueOf(startUTC));
                    saveApptStatement.setTimestamp(7, Timestamp.valueOf(endUTC));
                    saveApptStatement.setInt(8, customerID);
                    saveApptStatement.setInt(9, userID);
                    saveApptStatement.setInt(10, contactID);

                    //check if appointment saved or not
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
                                userID,
                                contactID
                        ));
                        // clear data so updated data can repopulate appointments table
                        cachedData.clearAppointments();
                        cachedData.clearContacts();
                        cachedData.clearCustomers();
                        cachedData.clearUsers();
                        // navigate user back to main appointments view
                        SchedulingApplication.switchScenes(Paths.appointmentsPath);
                        // generate success alert for user
                        Alerts.GenerateAlert(
                                "INFORMATION",
                                "Appointment Added",
                                "Appointment Added",
                                "Appointment has been successfully added.",
                                "ShowAndWait"
                        );
                    }
                }
            } catch (ParseException | SQLException | IOException e) {
                e.printStackTrace();
                Alerts.GenerateAlert("ERROR", "Appointment Save Error", "Appointment Save Error", "Appointment could not be saved!", "ShowAndWait");
            }
        });
        cancelButton.setOnAction(actionEvent -> {
            // can't use Alerts class here due to needing to verify against user response from alert
            Alert cancelConfirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel?", ButtonType.OK, ButtonType.CANCEL);
            cancelConfirmAlert.showAndWait();
            // if user clicks ok, continue with navigation back to main appointments view
            if (cancelConfirmAlert.getResult() == ButtonType.OK) {
                // clear these from cache for fresh data pull in main appointments view
                cachedData.clearAppointments();
                cachedData.clearContacts();
                cachedData.clearCustomers();
                try {
                    SchedulingApplication.switchScenes(Paths.appointmentsPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Disables days in date picker before current date as an extra layer of validation for appointments. Uses lambda
     * for better iteration through cells with updateItem function to disable.
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
     * Formats LocalTime cells to display as 12hr time format. Lambda is used for iterating through list cells more
     * efficiently to perform updateItem function on each one.
     */
    private void convertTimeSelectionCells() {
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
     * Callback to reformat combobox selection for start/end. Does NOT use lambda for iteration since only single object
     * that we need to convert.
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
