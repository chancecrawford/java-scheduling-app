package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainLoginController {
    @FXML private Button loginButton;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;

    private static final ResourceBundle resBundle = ResourceBundle.getBundle("Locale/English", Locale.forLanguageTag(Locale.getDefault().getCountry()));

    @FXML
    private void initialize() {
        titleLabel.setText(resBundle.getString("loginTitle"));
        usernameLabel.setText(resBundle.getString("username"));
        passwordLabel.setText(resBundle.getString("password"));
        loginButton.setText(resBundle.getString("login"));
    }
}
