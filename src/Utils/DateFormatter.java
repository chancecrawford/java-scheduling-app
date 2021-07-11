package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatter {
    public static String formatToSimpleDate(Date date, String formatType) {
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

    public static String formatLocalDateTime(LocalDateTime date, String formatType) {
        if (date != null) {
            if (formatType.equals("iso")) {
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if (formatType.equals("simpleTime")) {
                return date.format(DateTimeFormatter.ofPattern("hh:mm a"));
            }
            if (formatType.equals("loggerTime")) {
                return date.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            }
        }
        return null;
    }

    public static ZonedDateTime convertToUTC(LocalDate localDate, LocalTime localTime) {
        LocalDateTime tempLDT = LocalDateTime.of(localDate, localTime);
        return tempLDT.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
    }
}
