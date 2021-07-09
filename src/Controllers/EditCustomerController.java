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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditCustomerController {
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

    public static final CachedData cachedData = CustomersController.cachedData;
    public static final Customer selectedCustomer = CustomersController.getSelectedCustomer();

    @FXML
    private void initialize() {
        cachedData.importCountries();
        cachedData.importDivisions();

        countryChoiceBox.setItems(cachedData.getAllCountries());

        populateCustomerInfo();
        setDivisionListener();
        preventNonNumerics();
        setButtonActions();
    }

    private void populateCustomerInfo() {
        nameTextField.setText(selectedCustomer.getName());
        addressTextField.setText(selectedCustomer.getAddress().substring(0, selectedCustomer.getAddress().indexOf(",")));
        cityTextField.setText(selectedCustomer.getAddress().substring(selectedCustomer.getAddress().indexOf(",") + 1));
        // need to grab division and country to set said choiceboxes
        Division customerDivision = cachedData.getDivisionByID(selectedCustomer.getDivisionID());
        countryChoiceBox.setValue(cachedData.getCountryByID(customerDivision.getCountryID()));
        divisionChoiceBox.setItems(cachedData.getDivisionsByCountryID(countryChoiceBox.getValue().getId()));
        divisionChoiceBox.setValue(customerDivision);

        postalCodeTextField.setText(selectedCustomer.getPostalCode());
        phoneTextField.setText(selectedCustomer.getPhoneNum());
    }

    private void setButtonActions() {
        editButton.setOnAction(actionEvent -> {
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

                    PreparedStatement updateCustomerStatement = Database.getDBConnection().prepareStatement("UPDATE customers " +
                            "SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ?)" +
                            "WHERE Customer_ID = ?");
                    updateCustomerStatement.setString(1, nameTextField.getText().trim());
                    updateCustomerStatement.setString(2, customerAddress);
                    updateCustomerStatement.setString(3, postalCodeTextField.getText().trim());
                    updateCustomerStatement.setString(4, phoneTextField.getText().trim());
                    updateCustomerStatement.setInt(5, divisionID);
                    updateCustomerStatement.setInt(6, Integer.parseInt(customerIDLabel.getText()));

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
                    // TODO: add success alert here
                    System.out.println("----- Customer Saved! -----");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            cachedData.clearCustomers();
            cachedData.clearCountries();
            cachedData.clearCountries();
            CustomersController.setSelectedCustomer(null);
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
            CustomersController.setSelectedCustomer(null);
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
