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

/**
 * Edit customer view that loads the selected customer from the main customers page and populates all inputs
 * for that selected customer. Once the user hits save, all inputs are run through validation and any updates saved
 * for that customer.
 */
public class EditCustomerController {
    // javafx instantiation for ui elements
    @FXML
    private Label customerIDLabel;
    @FXML
    private TextField nameTextField, addressTextField, cityTextField, postalCodeTextField, phoneTextField;
    @FXML
    private ChoiceBox<Division> divisionChoiceBox;
    @FXML
    private ChoiceBox<Country> countryChoiceBox;
    @FXML
    private Button editButton, cancelButton;

    // grab cache from main customers view
    public static final CachedData cachedData = CustomersController.cachedData;
    // get selected customer from main customers view
    private final Customer selectedCustomer = CustomersController.getSelectedCustomer();

    /**
     * Initializes and populates all inputs with selected customer information. Set actions for all buttons and listeners.
     */
    @FXML
    private void initialize() {
        // import needed data
        cachedData.importCountries();
        cachedData.importDivisions();
        // get countries for choice box
        countryChoiceBox.setItems(cachedData.getAllCountries());
        // populate customer inputs with info from selected customer
        populateCustomerInfo();
        // set listeners and button events
        setDivisionListener();
        preventNonNumerics();
        setButtonActions();
    }

    /**
     * Populates inputs in view with info from selected customer
     */
    private void populateCustomerInfo() {
        customerIDLabel.setText(String.valueOf(selectedCustomer.getCustID()));
        nameTextField.setText(selectedCustomer.getName());
        // ternaries to make sure correct address values are separated and set
        addressTextField.setText(
                selectedCustomer.getAddress().contains(",") ? selectedCustomer.getAddress().substring(0, selectedCustomer.getAddress().indexOf(",")) :
                selectedCustomer.getAddress());
        cityTextField.setText(
                selectedCustomer.getAddress().contains(",") ? selectedCustomer.getAddress().substring(selectedCustomer.getAddress().indexOf(",") + 1) :
                        "");
        // need to grab division and country to set said choiceboxes
        Division customerDivision = cachedData.getDivisionByID(selectedCustomer.getDivisionID());
        countryChoiceBox.setValue(cachedData.getCountryByID(customerDivision.getCountryID()));
        divisionChoiceBox.setItems(cachedData.getDivisionsByCountryID(countryChoiceBox.getValue().getId()));
        divisionChoiceBox.setValue(customerDivision);
        postalCodeTextField.setText(selectedCustomer.getPostalCode());
        phoneTextField.setText(selectedCustomer.getPhoneNum());
    }

    /**
     * Grabs user inputs, validates inputs with {@link InputValidation#areCustomerInputsValid}, before updating the
     * customer to the database and local cache, and finally navigating the user back to the main customers view with a
     * success alert.
     */
    private void setButtonActions() {
        editButton.setOnAction(actionEvent -> {
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
                        cityTextField.getText().trim(),
                        divisionID,
                        countryID,
                        postalCodeTextField.getText().trim(),
                        phoneTextField.getText().trim()
                )) {
                    // create update customer query
                    PreparedStatement updateCustomerStatement = Database.getDBConnection().prepareStatement("UPDATE customers " +
                            "SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? " +
                            "WHERE Customer_ID = ?");
                    // set user inputs into update query
                    updateCustomerStatement.setString(1, nameTextField.getText().trim());
                    updateCustomerStatement.setString(2, customerAddress);
                    updateCustomerStatement.setString(3, postalCodeTextField.getText().trim());
                    updateCustomerStatement.setString(4, phoneTextField.getText().trim());
                    updateCustomerStatement.setInt(5, divisionID);
                    updateCustomerStatement.setInt(6, selectedCustomer.getCustID());

                    //check if customer was updated or not
                    int saveResult = updateCustomerStatement.executeUpdate();
                    if (saveResult == 1) {
                        // update customer in cached data since update confirmed
                        selectedCustomer.setName(nameTextField.getText().trim());
                        selectedCustomer.setAddress(customerAddress);
                        selectedCustomer.setPostalCode(postalCodeTextField.getText().trim());
                        selectedCustomer.setPhoneNum(phoneTextField.getText().trim());
                        selectedCustomer.setDivisionID(divisionID);
                    }
                    // clear data so updated data can repopulate customers table
                    cachedData.clearCustomers();
                    cachedData.clearCountries();
                    cachedData.clearCountries();
                    // reset customer selection
                    CustomersController.setSelectedCustomer(null);
                    // navigate back to main customers view
                    SchedulingApplication.switchScenes(Paths.customersPath);
                    // generate success alert for user
                    Alerts.GenerateAlert(
                            "INFORMATION",
                            "Customer Updated",
                            "Customer Updated",
                            "Customer has been successfully updated.",
                            "ShowAndWait"
                    );
                }
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        });
        cancelButton.setOnAction(actionEvent -> {
            // can't use Alerts class here due to needing to verify against user response from alert
            Alert cancelConfirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel?", ButtonType.OK, ButtonType.CANCEL);
            cancelConfirmAlert.showAndWait();
            // if user clicks ok, continue with navigation back to main appointments view
            if (cancelConfirmAlert.getResult() == ButtonType.OK) {
                cachedData.clearCustomers();
                cachedData.clearCountries();
                cachedData.clearDivisions();
                // reset customer selection
                CustomersController.setSelectedCustomer(null);
                // navigate back to main customers view
                try {
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
     * to be entered into the phone number text field. Lambdas used for better iteration through chars in field for
     * validation/input sanitation
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
     * Filters and populates the division choice box based on country selected in country choice box. Lambda used for
     * iteration through country list items to perform function to get relevant divisions for divisions choice box.
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
