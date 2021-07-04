package Data;

public class Text {
    // appointment error messages
    public static String appointmentTitleError = "Entry for appointment title is missing or invalid.";
    public static String appointmentTypeError = "Invalid or no selection made for appointment type.";
    public static String appointmentCustomerError = "Invalid or no selection made for a customer.";
    public static String appointmentContactError = "Invalid or no selection made for a contact.";
    public static String appointmentLocationError = "Entry for appointment location is missing or invalid.";
    public static String appointmentNullDateError = "An appointment date was not selected.";
    public static String appointmentPriorDateError = "Previous dates cannot be selected for an appointment.";
    public static String appointmentNullStartEndError = "A start and end time have not been chosen for the appointment.";
    public static String appointmentEndNotAfterStartError = "The end of the appointment cannot be before the start of it.";
    public static String appointmentOutsideHoursError = "Appointments cannot be scheduled outside of business hours or on weekends.";
    public static String appointmentConflictError = "This appointment conflicts with another customer appointment.";
}
