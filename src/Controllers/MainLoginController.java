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
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainLoginController {
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

    private static final ResourceBundle resBundle = ResourceBundle.getBundle("Locale/Login", Locale.forLanguageTag(Locale.getDefault().getCountry()));
    // for logging date and time of user activity
    private static final Calendar calendar = Calendar.getInstance();

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
                        SchedulingApplication.switchScenes(Paths.appointmentsPath);
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

            SchedulingApplication.setUser(new User(
                    userResult.getInt("User_ID"),
                    userResult.getString("User_Name"),
                    userResult.getString("Password")
            ));
            ActivityLogger.log(
                    "User ID " + userResult.getInt("User_ID") + " successfully logged in on " +
                            DateFormatter.formatLocalDateTime(LocalDateTime.now(ZoneId.of("UTC")), "iso") + " at " +
                            DateFormatter.formatLocalDateTime(LocalDateTime.now(ZoneOffset.UTC), "loggerTime") + " UTC"
            );
            return true;
        } catch (SQLException error) {
            // could be the type of exception error we're throwing
            Alerts.GenerateAlert("ERROR", "Database Error", "Database Connection Issue", resBundle.getString("databaseConnectionError"), "ShowAndWait");
            error.printStackTrace();
        }

        return false;
    }
}
