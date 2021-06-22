package Controllers;

import Utils.InputValidation;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

            InputValidation.checkLoginInputs(username, password);
        });
    }

    private void authenticateUser(String usernameInput, String passwordInput) {

    }
}
