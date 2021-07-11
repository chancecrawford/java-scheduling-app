package Controllers;

import Data.Paths;
import Main.SchedulingApplication;
import Models.User;

import Utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainLoginController {
    // javafx instantiation for ui elements
    @FXML
    private Button loginButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordInputField;
    @FXML
    private Label timezoneLabel;
    // grabs resource bundle for users locale to use on login page
    private static final ResourceBundle resBundle = ResourceBundle.getBundle("Locale/Login", Locale.forLanguageTag(Locale.getDefault().getCountry()));

    @FXML
    private void initialize() {
        // set text based on user locale
        titleLabel.setText(resBundle.getString("loginTitle"));
        usernameLabel.setText(resBundle.getString("username"));
        passwordLabel.setText(resBundle.getString("password"));
        loginButton.setText(resBundle.getString("login"));
        timezoneLabel.setText(ZoneId.systemDefault().toString());

        setLoginButtonEvent();
    }

    /**
     * Handles actions once login button is pressed. Grabs username and password input, runs them through
     * {@link InputValidation#checkLoginInputs(String username, String password)} and if inputs are fine,
     * attempts to authenticate user via {@link #authenticateUser(String username, String password)}
     */
    private void setLoginButtonEvent() {
        loginButton.setOnAction(actionEvent -> {
            // get user inputs
            String username = usernameTextField.getText().trim();
            String password = passwordInputField.getText();
            // check inputs are valid, authenticate user, and then switch to appt screen if successful
            if (InputValidation.checkLoginInputs(username, password)) {
                if (authenticateUser(username, password)) {
                    try {
                        SchedulingApplication.switchScenes(Paths.appointmentsPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Grabs user login inputs, gets row with user info if found, verifies password against provided password,
     * generates error alerts if authentication failed due to incorrect login information or a database connection
     * error, and logs all successful or unsuccessful attempts
     * @param usernameInput grabbed from username text field
     * @param passwordInput grabbed from password text field
     * @return returns true if authentication successful via database call with above inputs
     */
    private boolean authenticateUser(String usernameInput, String passwordInput) {
        try {
            // create and execut query to find user
            PreparedStatement getUser = Database.getDBConnection().prepareStatement("SELECT * FROM users WHERE User_Name=?");
            getUser.setString(1, usernameInput);
            ResultSet userResult = getUser.executeQuery();
            // if cannot find user based on provided username, generate error and log
            if (!userResult.next()) {
                Alerts.GenerateAlert(
                        "WARNING",
                        resBundle.getString("loginErrorTitleHeader"),
                        resBundle.getString("usernameHeaderError"),
                        resBundle.getString("cannotFindUsernameError"),
                        "ShowAndWait"
                );
                ActivityLogger.log(
                        "An unknown user unsuccessfully attempted to log in on " +
                                DateFormatter.formatLocalDateTime(LocalDateTime.now(ZoneId.of("UTC")), "iso") + " at " +
                                DateFormatter.formatLocalDateTime(LocalDateTime.now(ZoneOffset.UTC), "loggerTime") + " UTC"
                );
                return false;
            }
            // if password doesn't match, generate error and log
            if (!passwordInput.equals(userResult.getString("Password"))) {
                Alerts.GenerateAlert(
                        "WARNING",
                        resBundle.getString("loginErrorTitleHeader"),
                        resBundle.getString("passwordHeaderError"),
                        resBundle.getString("passwordIncorrectError"),
                        "ShowAndWait"
                );
                ActivityLogger.log(
                        "User ID " + userResult.getInt("User_ID") + " unsuccessfully attempted to log in on " +
                                DateFormatter.formatLocalDateTime(LocalDateTime.now(ZoneId.of("UTC")), "iso") + " at " +
                                DateFormatter.formatLocalDateTime(LocalDateTime.now(ZoneOffset.UTC), "loggerTime") + " UTC"
                );
                return false;
            }
            // if successful, set user info for use throughout application
            SchedulingApplication.setUser(new User(
                    userResult.getInt("User_ID"),
                    userResult.getString("User_Name"),
                    userResult.getString("Password")
            ));
            // log successful login
            ActivityLogger.log(
                    "User ID " + userResult.getInt("User_ID") + " successfully logged in on " +
                            DateFormatter.formatLocalDateTime(LocalDateTime.now(ZoneId.of("UTC")), "iso") + " at " +
                            DateFormatter.formatLocalDateTime(LocalDateTime.now(ZoneOffset.UTC), "loggerTime") + " UTC"
            );
            // for tracking to show upcoming appointment alert
            SchedulingApplication.setLastScene("login");
            return true;
        } catch (SQLException error) {
            // alert for if database connection could not be established
            Alerts.GenerateAlert(
                    "ERROR",
                    resBundle.getString("databaseError"),
                    resBundle.getString("databaseConnectionIssueError"),
                    resBundle.getString("databaseConnectionError"),
                    "ShowAndWait");
            error.printStackTrace();
        }

        return false;
    }
}
