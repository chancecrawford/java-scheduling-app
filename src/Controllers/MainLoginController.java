package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.User;

import Utils.Alerts;
import Utils.Database;
import Utils.InputValidation;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainLoginController {
    @FXML private Button loginButton;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordInputField;
    @FXML private Label timezoneLabel;

    private static final ResourceBundle resBundle = ResourceBundle.getBundle("Locale/Login", Locale.forLanguageTag(Locale.getDefault().getCountry()));

    private User user;

    @FXML
    private void initialize() {
        titleLabel.setText(resBundle.getString("loginTitle"));
        usernameLabel.setText(resBundle.getString("username"));
        passwordLabel.setText(resBundle.getString("password"));
        loginButton.setText(resBundle.getString("login"));
        timezoneLabel.setText(ZoneId.systemDefault().toString());

        setLoginButtonEvent();
    }

    private void setLoginButtonEvent() {
        loginButton.setOnAction(actionEvent -> {
            String username = usernameTextField.getText().trim();
            String password = passwordInputField.getText();

            if (InputValidation.checkLoginInputs(username, password)) {
                if (authenticateUser(username, password)) {
                    try {
                        SchedulingApplication.switchScenes(Paths.appointmentsMonthlyPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean authenticateUser(String usernameInput, String passwordInput) {
        try {
            PreparedStatement getUser = Database.getDBConnection().prepareStatement("SELECT * FROM users WHERE User_Name=?");
            getUser.setString(1, usernameInput);
            ResultSet userResult = getUser.executeQuery();

            if (!userResult.next()) {
                Alerts.GenerateAlert("WARNING", "Login Error", "User Not Found", resBundle.getString("cannotFindUsernameError"), "ShowAndWait");
                return false;
            }
            if (!passwordInput.equals(userResult.getString("Password"))) {
                Alerts.GenerateAlert("WARNING", "Login Error", "Password Error", resBundle.getString("passwordIncorrectError"), "ShowAndWait");
                return false;
            }

            user = new User(
                    userResult.getInt("User_ID"),
                    userResult.getString("User_Name"),
                    userResult.getString("Password")
            );
            return true;
        } catch (SQLException error) {
            // alert doesn't show when a db connection can't be made?
            Alerts.GenerateAlert("ERROR", "Database Error", "Database Connection Issue", resBundle.getString("databaseConnectionError"), "ShowAndWait");
            error.printStackTrace();
        }

        return false;
    }
}
