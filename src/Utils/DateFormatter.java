package Utils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Class for formatting dates to simple format, converting to UTC
 */
public class DateFormatter {
    /**
     * Formats given date to requested format by passed in string
     * @param date date object to format
     * @param formatType type requested
     * @return formatted string for that type
     */
    public static String formatToSimpleDate(Date date, String formatType) {
        // make sure date isn't null first
        if (date != null) {
            if (formatType.equals("month")) {
                return new SimpleDateFormat("MMMM").format(date);
            }
            if (formatType.equals("monthDay")) {
                return new SimpleDateFormat("MMM d").format(date);
            }
            if (formatType.equals("monthYear")) {
                return new SimpleDateFormat("MMMM yyyy").format(date);
            }
            if (formatType.equals("iso")) {
                return new SimpleDateFormat("yyyy-MM-dd").format(date);
            }
            if (formatType.equals("isoSingleDay")) {
                return new SimpleDateFormat("yyyy-MM-d").format(date);
            }
            if (formatType.equals("isoYearMonth")) {
                return new SimpleDateFormat("yyyy-MM").format(date);
            }
            if (formatType.equals("dbFormat")) {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(date);
            }
        }
        return null;
    }

    /**
     * Formats given LocalDateTime to requested format
     * @param localDateTime given LocalDateTime to format
     * @param formatType requested format
     * @return formatted string for that type
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String formatType) {
        // make sure date isn't null first
        if (localDateTime != null) {
            if (formatType.equals("iso")) {
                return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if (formatType.equals("simpleTime")) {
                return localDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
            }
            if (formatType.equals("loggerTime")) {
                return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            }
        }
        return null;
    }

    /**
     * Converts given date/time to UTC ZonedDateTime
     * @param localDate passed date
     * @param localTime passed time
     * @return formatted ZonedDateTime for UTC
     */
    public static ZonedDateTime convertToUTC(LocalDate localDate, LocalTime localTime) {
        // make sure passed objects aren't null
        if (localDate != null && localTime != null) {
            LocalDateTime tempLDT = LocalDateTime.of(localDate, localTime);
            return tempLDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        }
        return null;
    }
}
