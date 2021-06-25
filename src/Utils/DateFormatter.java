package Utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatter {
    public static String formatToSimpleDate(Date date, String formatType) {
        if (date != null) {
            if (formatType.equals("month")) {
                return new SimpleDateFormat("MMMM").format(date);
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
        }
        return null;
    }

    public static String formatToIsoDate(LocalDateTime date) {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return null;
    }
}
