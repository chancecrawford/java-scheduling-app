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
        if (password == null || password.isEmpty()) {
            loginInputErrors.append(resBundle.getString("noPasswordEnteredError")).append("\n");
        }

        // generates alert and populates it with error messages if inputErrors string builder isn't empty
        if (loginInputErrors.length() > 0 || !loginInputErrors.toString().equals("")) {
            Alerts.GenerateAlert("WARNING", "Login Error", "Username or Password Error", loginInputErrors.toString(), "ShowAndWait");
            return false;
        }
        return true;
    }

      // TODO: for when done with add appointments
//    public static boolean areAppointmentInputsValid() {
//
//    }

      // TODO: validations for when adding customer add/edit/delete
//    public static boolean areCustomerInputsValid() {
//
//    }
//
//    public static boolean canCustomerBeDeleted() {
//
//    }
}
