package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Contact;
import Utils.CachedData;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

public class AddAppointmentController {
    @FXML private TextField titleTextField, locationTextField;
    @FXML private ChoiceBox<String> typeChoiceBox, customerChoiceBox, contactChoiceBox;
    @FXML private TextArea descriptionTextArea;
    @FXML private DatePicker apptDatePicker;
    @FXML private ComboBox<Date> startComboBox, endComboBox;
    @FXML private Label businessHoursLabel;
    @FXML private Button addButton, cancelButton;

    public static final CachedData cachedData = AppointmentsController.cachedData;

    private Contact selectedContact;

    @FXML
    private void initialize() {
        typeChoiceBox.setItems(cachedData.getAppointmentTypes());
        customerChoiceBox.setItems(cachedData.getCustomersByName());
        contactChoiceBox.setItems(cachedData.getContactsByName());

        apptDatePicker.setValue(LocalDateTime.now().toLocalDate());


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
}
