package Utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class InputValidation {
    private static final ResourceBundle resBundle = ResourceBundle.getBundle("Locale/Login", Locale.forLanguageTag(Locale.getDefault().getCountry()));

    public static boolean checkLoginInputs(String username, String password) {
        StringBuilder loginInputErrors = new StringBuilder();

        if (username == null || username.isEmpty()) {
            loginInputErrors.append(resBundle.getString("noUsernameEnteredError")).append("\n");
        }

        // generates alert and populates it with error messages if inputErrors string builder isn't empty
        if (loginInputErrors.length() > 0 || !loginInputErrors.toString().equals("")) {
            Alerts.GenerateAlert("WARNING", "Login Error", "Username or Password Error", loginInputErrors.toString(), "ShowAndWait");
            return false;
        }
        return true;
    }
}
