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
    // view navigation
    @FXML
    private Button appointmentsNavButton, reportsNavButton, logoutButton;
    @FXML
    private TableView<Customer> customerTableView;
    // tableview and columns for customers
    @FXML
    private TableColumn<Customer, String>
            customerIDColumn,
            customerNameColumn,
            customerAddressColumn,
            customerPostalCodeColumn,
            customerPhoneColumn,
            customerDivisionIDColumn;
    // customer manipulation buttons
    @FXML
    private Button addButton, editButton, deleteButton;

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
        cachedData.importCustomers();
        populateCustomersTable();

        setNavigationButtonEvents();
        setCustomerEditingButtonEvents();
        setCustomerTableListener();
    }

    private void populateCustomersTable() {
        customerTableItems.clear();
        customerTableView.setItems(customerTableItems);

        ObservableList<Customer> customerList = cachedData.getAllCustomers();
        if (!customerList.isEmpty()) {
            customerTableItems.addAll(customerList);
            customerTableView.setItems(customerTableItems);
        }
    }

    private void setNavigationButtonEvents() {
        appointmentsNavButton.setOnAction(actionEvent -> {
            try {
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
            // check for selected customer and if customer had any appointments
            if (selectedCustomer != null && cachedData.getAppointmentsByCustomerID(selectedCustomer.getCustID()).isEmpty()) {
                // can't use Alerts class here due to needing to verify against user response from alert
                Alert deleteCustomerWarning = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedCustomer.getName() + "?", ButtonType.OK, ButtonType.CANCEL);
                deleteCustomerWarning.showAndWait();
                // after user confirms, delete customer
                if (deleteCustomerWarning.getResult() == ButtonType.OK) {
                    try {
                        PreparedStatement deleteCustomerStatement = Database.getDBConnection().prepareStatement("DELETE FROM customers WHERE Customer_ID = ?");
                        deleteCustomerStatement.setInt(1, selectedCustomer.getCustID());
                        int deleteResult = deleteCustomerStatement.executeUpdate();
                        if (deleteResult == 1) {
                            cachedData.deleteCustomer(selectedCustomer);
                            customerTableView.getSelectionModel().select(null);
                            refreshCache();
                            populateCustomersTable();
                            // TODO: add succcess alert here
                            System.out.println("----- Customer Deleted! -----");
                        }
                    } catch (SQLException throwables) {
                        if (cachedData.getAppointmentsByCustomerID(selectedCustomer.getCustID()).isEmpty()) {
                            Alerts.GenerateAlert(
                                    "WARNING",
                                    "Delete Customer Error",
                                    "Cannot Delete Customer",
                                    "Cannot delete customer if they have appointments",
                                    "ShowAndWait"
                            );
                        }
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }

    private void refreshCache() throws SQLException {
        // clear all lists to repopulate
        cachedData.clearCache();
        // pull down fresh data
        cachedData.importCustomers();
    }

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

    private void setCustomerColumns() {
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("custID"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        customerDivisionIDColumn.setCellValueFactory(new PropertyValueFactory<>("divisionID"));
    }
}
