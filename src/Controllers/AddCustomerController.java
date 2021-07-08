package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Division;
import Utils.CachedData;
import Utils.InputValidation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AddCustomerController {
    @FXML
    private TextField nameTextField, address1TextField, address2TextField, postalCodeTextField, phoneTextField;
    @FXML
    private ChoiceBox<Division> cityChoiceBox;
    @FXML
    private ChoiceBox<String> countryChoiceBox;
    @FXML
    private Button addButton, cancelButton;

    public static final CachedData cachedData = CustomersController.cachedData;

    @FXML
    private void initialize() {
        cachedData.importDivisions();

        countryChoiceBox.setItems(cachedData.getCountries());

        setDivisionListener();
        preventNonNumerics();
        setButtonActions();
    }

    private void setButtonActions() {
        addButton.setOnAction(actionEvent -> {
            // generate customer id
            int tempCustomerID = InputValidation.generateCustomerID();
            // make sure there is selection for city/country choiceboxes
            Integer divisionID = !cityChoiceBox.getSelectionModel().isEmpty() ? cityChoiceBox.getSelectionModel().getSelectedItem().getDivisionID() : null;
            Integer countryID = !countryChoiceBox.getSelectionModel().isEmpty() ? cachedData.getCountryID(countryChoiceBox.getValue()) : null;

            try {
                if (InputValidation.areCustomerInputsValid(
                        nameTextField.getText().trim(),
                        address1TextField.getText().trim(),
                        divisionID,
                        countryID,
                        postalCodeTextField.getText().trim(),
                        phoneTextField.getText().trim()
                )) {
                    System.out.println("----- Yay! -----");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction(actionEvent -> {
            cachedData.clearCustomers();
            cachedData.clearDivisions();
            try {
                // TODO: add dialog for confirmation before cancelling
                SchedulingApplication.switchScenes(Paths.customersPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void preventNonNumerics() {
        // lambda for better iteration
        postalCodeTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("[\\d*]")) {
                postalCodeTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        phoneTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("^[\\d -]+$")) {
                phoneTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void setDivisionListener() {
        // used lambda for setting listeners on table view
        countryChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (countryChoiceBox.getSelectionModel().getSelectedItem() != null) {
                // iterate through list and get divisions
                cityChoiceBox.setItems(cachedData.getDivisionsByCountryID(cachedData.getCountryID(newValue)));
            }
        });
    }
}
