package Utils;

import Controllers.AppointmentsController;
import Controllers.CustomersController;
import Data.Text;
import Models.Appointment;
import Models.Customer;

import java.text.ParseException;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for input validations to be done when adding or editing appointments and customers.
 */
public class InputValidation {
    // get resource bundle for user local for login errors
    private static final ResourceBundle resBundle = ResourceBundle.getBundle("Locale/Login", Locale.forLanguageTag(Locale.getDefault().getCountry()));

    /**
     * Checks if provided input is null or empty
     * @param userInput
     * @return true/false depending on input validity
     */
    public static boolean isInputNull(String userInput) {
        return userInput == null || userInput.trim().isEmpty();
    }

    /**
     * Checks if provided username/password is null or empty
     * @param username
     * @param password
     * @return
     */
    public static boolean checkLoginInputs(String username, String password) {
        // use string builder for generating multiple errors
        StringBuilder loginInputErrors = new StringBuilder();
        // validate username
        if (username == null || username.isEmpty()) {
            loginInputErrors.append(resBundle.getString("noUsernameEnteredError")).append("\n");
        }
        // validate password
        if (password == null || password.isEmpty()) {
            loginInputErrors.append(resBundle.getString("noPasswordEnteredError")).append("\n");
        }
        // generates alert and populates it with error messages if inputErrors string builder isn't empty
        if (loginInputErrors.length() > 0 || !loginInputErrors.toString().equals("")) {
            Alerts.GenerateAlert(
                    "WARNING",
                    resBundle.getString("loginErrorTitleHeader"),
                    resBundle.getString("loginErrorTitleHeader"),
                    loginInputErrors.toString(),
                    "ShowAndWait"
            );
            return false;
        }
        return true;
    }

    /**
     * Checks if any inputs are null and if scheduled times are outside business hours or there is a conflict with customer
     * appointment times.
     * @param apptID
     * @param title
     * @param type
     * @param customerID
     * @param contactID
     * @param location
     * @param localDate
     * @param start
     * @param end
     * @return true/false depending on inputs
     * @throws ParseException
     */
    public static boolean areAppointmentInputsValid(
            int apptID,
            String title,
            String type,
            Integer customerID,
            Integer contactID,
            String location,
            LocalDate localDate,
            LocalTime start,
            LocalTime end
            ) throws ParseException {
        // use string builder to add all validation errors to push into one alert
        StringBuilder inputErrors = new StringBuilder();

        // general checks for null/empty inputs
        if (isInputNull(title)) {
            inputErrors.append(Text.appointmentTitleError).append("\n");
        }
        if (isInputNull(type)) {
            inputErrors.append(Text.appointmentTypeError).append("\n");
        }
        if (customerID == null) {
            inputErrors.append(Text.appointmentCustomerError).append("\n");
        }
        if (contactID == null) {
            inputErrors.append(Text.appointmentContactError).append("\n");
        }
        if (isInputNull(location)) {
            inputErrors.append(Text.appointmentLocationError).append("\n");
        }
        if (isInputNull(localDate.toString())) {
            inputErrors.append(Text.appointmentNullDateError).append("\n");
        }
        if (!isInputNull(localDate.toString()) && localDate.compareTo(LocalDate.now()) < 0) {
            inputErrors.append(Text.appointmentPriorDateError).append("\n");
        }
        if (start == null || end == null) {
            inputErrors.append(Text.appointmentNullStartEndError).append("\n");
        }
        // check if times are selected for appointment
        if (start != null && end != null) {
            // ensures end time is after start time
            if (end.equals(start) || end.isBefore(start)) {
                inputErrors.append(Text.appointmentEndNotAfterStartError).append("\n");
            }
            // make sure customer id isn't null before further validation
            if (customerID != null) {
                // check if times within business hours
                if (!isAppointmentInBusinessHours(localDate, start, end)) {
                    inputErrors.append(Text.appointmentOutsideHoursError).append("\n");
                }
                // check if times conflict with another of that customers appointments
                if (doesAppointmentConflictExist(localDate, start, end, customerID, apptID)) {
                    inputErrors.append(Text.appointmentConflictError).append("\n");
                }
            }
        }
        // generates alert and populates it with error messages if inputErrors string builder isn't empty
        if (inputErrors.length() > 0 || !inputErrors.toString().equals("")) {
            Alerts.GenerateAlert("WARNING", "Appointment Entry Warning", "Required Fields Empty or Invalid", inputErrors.toString(), "ShowAndWait");
            return false;
        }
        return true;
    }

    /**
     * Validates if start/end times are not on the weekend or outside of weekday business hours
     * @param localDate to determine date and UTC checks
     * @param start to check against business hours
     * @param end to check against business hours
     * @return true/false depending on inputs
     * @throws ParseException if date/times can't be parsed to needed format
     */
    public static boolean isAppointmentInBusinessHours(LocalDate localDate, LocalTime start, LocalTime end) throws ParseException {
        // get day of week for appointment
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        int dayForDate = calendar.get(Calendar.DAY_OF_WEEK);

        // check if date is on weekend
        if (dayForDate == Calendar.SATURDAY || dayForDate == Calendar.SUNDAY) {
            return false;
        }
        // check if outside weekday business hours
        ZonedDateTime startBusinessTime = AppointmentsController.cachedData.businessOpenZDT(localDate);
        ZonedDateTime endBusinessTime = AppointmentsController.cachedData.businessCloseZDT(localDate);

        return !DateFormatter.convertToUTC(localDate, start).isBefore(startBusinessTime) &&
                !DateFormatter.convertToUTC(localDate, end).isBefore(startBusinessTime) &&
                !DateFormatter.convertToUTC(localDate, start).isAfter(endBusinessTime) &&
                !DateFormatter.convertToUTC(localDate, end).isAfter(endBusinessTime);
    }

    /**
     * Validates if appointment times conflict with another one of that same customer
     * @param localDate to determine date and UTC checks
     * @param start to check against other appointment times
     * @param end to check against other appointment times
     * @param customerID to find other customer appointments
     * @param apptID to see if appointment is being edited or if a new appointment
     * @return true/false depending on if conflict arises or not
     */
    public static boolean doesAppointmentConflictExist(LocalDate localDate, LocalTime start, LocalTime end, int customerID, int apptID) {
        // create local date times
        LocalDateTime startLDT = LocalDateTime.of(localDate, start);
        LocalDateTime endLDT = LocalDateTime.of(localDate, end);

        // iterate through all appointments for that customer on specified date
        for (Appointment appointment : AppointmentsController.cachedData.getCustomerAppointmentsByDate(localDate.toString(), customerID)) {
            // if apptID is the same, skip this since it can't conflict with previous appt info
            if (apptID != appointment.getApptID()) {
                if ((startLDT.isAfter(appointment.getStart()) && startLDT.isBefore(appointment.getEnd())) ||
                        (endLDT.isAfter(appointment.getStart()) && endLDT.isBefore(appointment.getEnd())) ||
                        startLDT.isEqual(appointment.getStart()) || endLDT.isEqual(appointment.getEnd())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * For generating random appointment id that isn't the same as another appointment
     * @return random id
     */
    public static int generateAppointmentID() {
        // generate random id
        int tempApptID = (int) (Math.random() * (9999 - 1 + 1) + 1);
        // compare id to all other appointments to make sure of no duplicate ids
        for (Appointment appointment : AppointmentsController.cachedData.getAllAppointments()) {
            if (tempApptID == appointment.getApptID()) {
                // start over if matching ids
                generateAppointmentID();
            } else {
                return tempApptID;
            }
        }
        // if no match occurs, assign id to appointment
        return tempApptID;
    }

    /**
     * Checks for any null/empty inputs and validates postal code and phone number are valid.
     * @param name
     * @param address1
     * @param divisionID
     * @param countryID
     * @param postalCode
     * @param phoneNum
     * @return true/false depending on input validity
     */
    public static boolean areCustomerInputsValid(String name, String address1, Integer divisionID, Integer countryID, String postalCode, String phoneNum) {
        // to hold multiple error messages
        StringBuilder inputErrors = new StringBuilder();
        // check for nulls/empty values
        if (isInputNull(name)) {
            inputErrors.append(Text.customerNameError).append("\n");
        }
        if (isInputNull(address1)) {
            inputErrors.append(Text.customerAddressError).append("\n");
        }
        if (divisionID == null) {
            inputErrors.append(Text.customerCityError).append("\n");
        }
        if (countryID == null) {
            inputErrors.append(Text.customerCountryError).append("\n");
        }
        // ensures postal code is valid length
        if (isInputNull(postalCode) || postalCode.length() != 5) {
            inputErrors.append(Text.customerPostalCodeError).append("\n");
        }
        // ensures phone number is valid and correct format
        if (isInputNull(phoneNum) || !isPhoneNumberValid(phoneNum)) {
            inputErrors.append(Text.customerPhoneNumberError).append("\n");
        }

        // generates alert and populates it with error messages if inputErrors string builder isn't empty
        if (inputErrors.length() > 0 || !inputErrors.toString().equals("")) {
            Alerts.GenerateAlert("WARNING", "Customer Entry Warning", "Required Fields Empty or Invalid", inputErrors.toString(), "ShowAndWait");
            return false;
        }
        return true;
    }

    /**
     * For generating random customer id that isn't the same as another customer
     * @return random id
     */
    public static int generateCustomerID() {
        // generate random id
        int tempApptID = (int) (Math.random() * (9999 - 1 + 1) + 1);
        for (Customer customer : CustomersController.cachedData.getAllCustomers()) {
            if (tempApptID == customer.getCustID()) {
                // if id matches another, regenerate id
                generateAppointmentID();
            } else {
                return tempApptID;
            }
        }
        // if id doesn't match any other customers, assign customer id
        return tempApptID;
    }

    /**
     * Regex to check if phone number is entered correctly. Handles multiple formats including international formats.
     * source for regex --> https://regexr.com/38pvb
     * @param phoneNum input from customer
     * @return true/false depending on match to regex
     */
    public static boolean isPhoneNumberValid(String phoneNum) {
        // regex
        String patterns = "^\\s*(?:\\+?(\\d{1,3}))?([-. (]*(\\d{3})[-. )]*)?((\\d{3})[-. ]*(\\d{2,4})(?:[-.x ]*(\\d+))?)\\s*$";
        // create pattern to match from regex
        Pattern pattern = Pattern.compile(patterns);
        Matcher matcher = pattern.matcher(phoneNum);
        // return pattern match result
        return matcher.matches();
    }
}
