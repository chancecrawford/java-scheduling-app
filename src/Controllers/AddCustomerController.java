package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Country;
import Models.Customer;
import Models.Division;
import Utils.CachedData;
import Utils.Database;
import Utils.InputValidation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCustomerController {
    @FXML
    private TextField nameTextField, addressTextField, cityTextField, postalCodeTextField, phoneTextField;
    @FXML
    private ChoiceBox<Division> divisionChoiceBox;
    @FXML
    private ChoiceBox<Country> countryChoiceBox;
    @FXML
    private Button addButton, cancelButton;

    public static final CachedData cachedData = CustomersController.cachedData;

    @FXML
    private void initialize() {
        cachedData.importCountries();
        cachedData.importDivisions();

        countryChoiceBox.setItems(cachedData.getAllCountries());

        setDivisionListener();
        preventNonNumerics();
        setButtonActions();
    }

    private void setButtonActions() {
        addButton.setOnAction(actionEvent -> {
            // generate customer id
            int tempCustomerID = InputValidation.generateCustomerID();
            // make sure there is selection for city/country choiceboxes
            Integer divisionID = !divisionChoiceBox.getSelectionModel().isEmpty() ? divisionChoiceBox.getSelectionModel().getSelectedItem().getDivisionID() : null;
            Integer countryID = !countryChoiceBox.getSelectionModel().isEmpty() ? countryChoiceBox.getSelectionModel().getSelectedItem().getId() : null;
            String customerAddress = buildAddress(addressTextField.getText().trim(), cityTextField.getText().trim());

            try {
                if (InputValidation.areCustomerInputsValid(
                        nameTextField.getText().trim(),
                        addressTextField.getText().trim(),
                        divisionID,
                        countryID,
                        postalCodeTextField.getText().trim(),
                        phoneTextField.getText().trim()
                )) {
                    System.out.println("----- Yay! -----");

                    PreparedStatement saveCustomerStatement = Database.getDBConnection().prepareStatement("INSERT INTO customers " +
                            "(Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID)" +
                            "VALUES (?, ?, ?, ?, ?, ?)");
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
                    // TODO: add success alert here
                    System.out.println("----- Customer Saved! -----");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            cachedData.clearCustomers();
            cachedData.clearCountries();
            cachedData.clearCountries();
            try {
                SchedulingApplication.switchScenes(Paths.customersPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction(actionEvent -> {
            cachedData.clearCustomers();
            cachedData.clearCountries();
            cachedData.clearDivisions();
            try {
                // TODO: add dialog for confirmation before cancelling
                SchedulingApplication.switchScenes(Paths.customersPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String buildAddress(String address, String city) {
        return address + ", " + city;
    }

    private void preventNonNumerics() {
        // lambdas for better iteration

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
