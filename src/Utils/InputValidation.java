package Utils;

import Controllers.AppointmentsController;
import Data.Text;
import Models.Appointment;

import java.text.ParseException;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class InputValidation {
    private static final ResourceBundle resBundle = ResourceBundle.getBundle("Locale/Login", Locale.forLanguageTag(Locale.getDefault().getCountry()));

    public static boolean isInputNull(String userInput) {
        return userInput == null || userInput.trim().isEmpty();
    }

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
        if (start != null && end != null) {
            if (end.equals(start) || end.isBefore(start)) {
                inputErrors.append(Text.appointmentEndNotAfterStartError).append("\n");
            }
            if (customerID != null) {
                if (!isAppointmentInBusinessHours(localDate, start, end)) {
                    inputErrors.append(Text.appointmentOutsideHoursError).append("\n");
                }
                if (doesAppointmentConflictExist(localDate, start, end, customerID, apptID)) {
                    inputErrors.append(Text.appointmentConflictError).append("\n");
                }
            }
        }

        // generates alert and populates it with error messages if inputErrors string builder isn't empty
        if (inputErrors.length() > 0 || !inputErrors.toString().equals("")) {
            Alerts.GenerateAlert("WARNING", "Part Entry Warning", "Required Fields Empty or Invalid", inputErrors.toString(), "ShowAndWait");
            return false;
        }
        return true;
    }

    public static boolean isAppointmentInBusinessHours(LocalDate localDate, LocalTime start, LocalTime end) throws ParseException {
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

    public static boolean doesAppointmentConflictExist(LocalDate localDate, LocalTime start, LocalTime end, int customerID, int apptID) {
        LocalDateTime startLDT = LocalDateTime.of(localDate, start);
        LocalDateTime endLDT = LocalDateTime.of(localDate, end);

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

    public static int generateAppointmentID() {
        int tempApptID = (int) (Math.random() * (9999 - 1 + 1) + 1);
        for (Appointment appointment : AppointmentsController.cachedData.getAllAppointments()) {
            if (tempApptID == appointment.getApptID()) {
                generateAppointmentID();
            } else {
                return tempApptID;
            }
        }
        return tempApptID;
    }

    // TODO: validations for when adding customer add/edit/delete
//    public static boolean areCustomerInputsValid() {
//
//    }
//
//    public static boolean canCustomerBeDeleted() {
//
//    }
}
