package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.Customer;
import Utils.Alerts;
import Utils.CachedData;
import Utils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomersController {
    // javafx instantiation for ui elements
    @FXML
    private Button appointmentsNavButton, reportsNavButton, logoutButton;
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String>
            customerIDColumn,
            customerNameColumn,
            customerAddressColumn,
            customerPostalCodeColumn,
            customerPhoneColumn,
            customerDivisionIDColumn;
    @FXML
    private Button addButton, editButton, deleteButton;

    // stores customers for table view population
    private final ObservableList<Customer> customerTableItems = FXCollections.observableArrayList();
    // for keeping track of selections
    private static Customer selectedCustomer = null;
    // for passing/retrieving selected customer between scenes
    public static Customer getSelectedCustomer() {
        return selectedCustomer;
    }
    public static void setSelectedCustomer(Customer customer) {
        selectedCustomer = customer;
    }

    // want this public for referencing in add/edit views
    public static final CachedData cachedData = new CachedData();

    @FXML
    private void initialize() {
        setCustomerColumns();
        customerTableView.setItems(customerTableItems);
        // grab fresh data in case changes occured
        cachedData.importCustomers();
        // populate table with all customers
        populateCustomersTable();
        // set paths and actions for buttons
        setNavigationButtonEvents();
        setCustomerEditingButtonEvents();
        setCustomerTableListener();
        // for tracking to show upcoming appointment alert
        SchedulingApplication.setLastScene("customers");
    }

    /**
     * Clears customer table before populating it with fresh data
     */
    private void populateCustomersTable() {
        // clear customer table
        customerTableItems.clear();
        customerTableView.setItems(customerTableItems);
        // get new list of customers
        ObservableList<Customer> customerList = cachedData.getAllCustomers();
        // check list for data then add to customer table
        if (!customerList.isEmpty()) {
            customerTableItems.addAll(customerList);
            customerTableView.setItems(customerTableItems);
        }
    }

    /**
     * Sets actions to fire for nagivating between different pages (Appointments, Reports, and logout).
     */
    private void setNavigationButtonEvents() {
        appointmentsNavButton.setOnAction(actionEvent -> {
            try {
                cachedData.clearAppointments();
                cachedData.clearCustomers();
                SchedulingApplication.switchScenes(Paths.appointmentsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        reportsNavButton.setOnAction(actionEvent -> {
            try {
                cachedData.clearCustomers();
                SchedulingApplication.switchScenes(Paths.reportAppointmentTypeMonthPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        logoutButton.setOnAction(actionEvent -> {
            try {
                // want to set user to null and clear cached data as security measure
                SchedulingApplication.setUser(null);
                cachedData.clearCache();
                SchedulingApplication.switchScenes(Paths.mainLoginPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets actions for customer add, edit, and delete buttons. Add/edit trigger navigation to their pages and delete
     * asks for user confirmation before doing so ONLY if customer has no appointments left in database.
     */
    private void setCustomerEditingButtonEvents() {
        addButton.setOnAction(actionEvent -> {
            try {
                SchedulingApplication.switchScenes(Paths.addCustomerPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        editButton.setOnAction(actionEvent -> {
            try {
                SchedulingApplication.switchScenes(Paths.editCustomersPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        deleteButton.setOnAction(actionEvent -> {
            // check for selected customer
            if (selectedCustomer != null) {
                // check if customer has any appointments, generate error if they do
                if (!cachedData.getAppointmentsByCustomerID(selectedCustomer.getCustID()).isEmpty()) {
                    Alerts.GenerateAlert(
                            "WARNING",
                            "Delete Customer Error",
                            "Cannot Delete Customer",
                            "Cannot delete customer if they have appointments",
                            "ShowAndWait"
                    );
                } else {
                    // can't use Alerts class here due to needing to verify against user response from alert
                    Alert deleteCustomerWarning = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedCustomer.getName() + "?", ButtonType.OK, ButtonType.CANCEL);
                    deleteCustomerWarning.showAndWait();
                    // after user confirms, delete customer
                    if (deleteCustomerWarning.getResult() == ButtonType.OK) {
                        try {
                            // create and execute query for customer deletion
                            PreparedStatement deleteCustomerStatement = Database.getDBConnection().prepareStatement("DELETE FROM customers WHERE Customer_ID = ?");
                            deleteCustomerStatement.setInt(1, selectedCustomer.getCustID());
                            int deleteResult = deleteCustomerStatement.executeUpdate();
                            // confirm result of deletion
                            if (deleteResult == 1) {
                                // delete customer from local cache
                                cachedData.deleteCustomer(selectedCustomer);
                                // unselect customer from table
                                customerTableView.getSelectionModel().select(null);
                                // grab updated data after delete
                                refreshCache();
                                // repopulate customers table
                                populateCustomersTable();
                                // generate success alert for user
                                Alerts.GenerateAlert(
                                        "INFORMATION",
                                        "Customer Deletion",
                                        "Customer Deletion",
                                        "Customer has been successfully deleted.",
                                        "ShowAndWait"
                                );
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * Clears local cache then repopulates with data from database
     * @throws SQLException in case data could not be retrieved or database connection couldn't be established
     */
    private void refreshCache() throws SQLException {
        // clear all lists to repopulate
        cachedData.clearCache();
        // pull down fresh data
        cachedData.importCustomers();
    }

    /**
     * Sets listener for customers table. Adds validation prefix to ensure edit/delete functions aren't executed with
     * null selections and sets selected customer to pass down to edit and delete pages.
     */
    private void setCustomerTableListener() {
        // used lambda for setting listeners on table view
        customerTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (customerTableView.getSelectionModel().getSelectedItem() != null) {
                selectedCustomer = newValue;
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });
    }

    /**
     * Sets columns in customers table to relevant data from {@link Customer} class
     */
    private void setCustomerColumns() {
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("custID"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        customerDivisionIDColumn.setCellValueFactory(new PropertyValueFactory<>("divisionID"));
    }
}
