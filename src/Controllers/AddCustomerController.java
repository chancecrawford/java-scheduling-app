package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Country;
import Models.Customer;
import Models.Division;
import Utils.Alerts;
import Utils.CachedData;
import Utils.Database;
import Utils.InputValidation;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCustomerController {
    // javafx instantiation for ui elements
    @FXML
    private TextField nameTextField, addressTextField, cityTextField, postalCodeTextField, phoneTextField;
    @FXML
    private ChoiceBox<Division> divisionChoiceBox;
    @FXML
    private ChoiceBox<Country> countryChoiceBox;
    @FXML
    private Button addButton, cancelButton;

    // grab cache from main customers view
    public static final CachedData cachedData = CustomersController.cachedData;

    @FXML
    private void initialize() {
        // import needed data
        cachedData.importCountries();
        cachedData.importDivisions();
        // get countries for choice box
        countryChoiceBox.setItems(cachedData.getAllCountries());
        // set listeners and button events
        setDivisionListener();
        preventNonNumerics();
        setButtonActions();
    }

    /**
     * Grabs user inputs, validates inputs with {@link InputValidation#areCustomerInputsValid}, before saving the new
     * customer to the database and local cache, and finally navigating the user back to the main customers view with a
     * success alert.
     */
    private void setButtonActions() {
        addButton.setOnAction(actionEvent -> {
            // generate customer id
            int tempCustomerID = InputValidation.generateCustomerID();
            // make sure there is selection for city/country choiceboxes
            Integer divisionID = !divisionChoiceBox.getSelectionModel().isEmpty() ? divisionChoiceBox.getSelectionModel().getSelectedItem().getDivisionID() : null;
            Integer countryID = !countryChoiceBox.getSelectionModel().isEmpty() ? countryChoiceBox.getSelectionModel().getSelectedItem().getId() : null;
            // combine address and city inputs
            String customerAddress = buildAddress(addressTextField.getText().trim(), cityTextField.getText().trim());

            // validate user inputs before moving forward and generate error message if any fields are invalid
            try {
                if (InputValidation.areCustomerInputsValid(
                        nameTextField.getText().trim(),
                        addressTextField.getText().trim(),
                        divisionID,
                        countryID,
                        postalCodeTextField.getText().trim(),
                        phoneTextField.getText().trim()
                )) {
                    // create save customer query
                    PreparedStatement saveCustomerStatement = Database.getDBConnection().prepareStatement("INSERT INTO customers " +
                            "(Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID)" +
                            "VALUES (?, ?, ?, ?, ?, ?)");
                    // set values from user inputs in query
                    saveCustomerStatement.setInt(1, tempCustomerID);
                    saveCustomerStatement.setString(2, nameTextField.getText().trim());
                    saveCustomerStatement.setString(3, customerAddress);
                    saveCustomerStatement.setString(4, postalCodeTextField.getText().trim());
                    saveCustomerStatement.setString(5, phoneTextField.getText().trim());
                    saveCustomerStatement.setInt(6, divisionID);

                    //check if customer saved or not
                    int saveResult = saveCustomerStatement.executeUpdate();
                    if (saveResult == 1) {
                        // add new customer to cached data since save confirmed
                        cachedData.addCustomer(new Customer(
                                tempCustomerID,
                                nameTextField.getText().trim(),
                                customerAddress,
                                postalCodeTextField.getText().trim(),
                                phoneTextField.getText().trim(),
                                divisionID
                        ));
                    }
                    // generate success alert for user
                    Alerts.GenerateAlert(
                            "INFORMATION",
                            "Customer Added",
                            "Customer Added",
                            "Customer has been successfully added.",
                            "ShowAndWait"
                    );
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            // clear data so updated data can repopulate customers table
            cachedData.clearCustomers();
            cachedData.clearCountries();
            cachedData.clearCountries();
            // navigate back to main customers view
            try {
                SchedulingApplication.switchScenes(Paths.customersPath);
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
                // clear data so updated data can repopulate customers table
                cachedData.clearCustomers();
                cachedData.clearCountries();
                cachedData.clearDivisions();
                try {
                    // navigate back to main customers view
                    SchedulingApplication.switchScenes(Paths.customersPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Concats address and city input into desired address format for database
     * @param address street info input from user
     * @param city city input from user
     * @return concantenated string of street and city in proper format
     */
    private String buildAddress(String address, String city) {
        return address + ", " + city;
    }

    /**
     * Prevents special characters from being input in the postal code text field and only allows numerics and hyphens
     * to be entered into the phone number text field.
     */
    private void preventNonNumerics() {
        // lambdas for better iteration through chars in field for validation/input sanitation

        // prevents any chars besides letters and numbers from being input
        postalCodeTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("^[\\w]+$")) {
                postalCodeTextField.setText(newValue.replaceAll("[^\\w]", ""));
            }
        });
        // prevents any chars except numbers and hyphens from being input
        phoneTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("^[\\d -]+$")) {
                phoneTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Filters and populates the division choice box based on country selected in country choice box
     */
    private void setDivisionListener() {
        // used lambda for setting listeners on table view
        countryChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (countryChoiceBox.getSelectionModel().getSelectedItem() != null) {
                // iterate through list and get divisions
                divisionChoiceBox.setItems(cachedData.getDivisionsByCountryID(newValue.getId()));
            }
        });
    }
}
