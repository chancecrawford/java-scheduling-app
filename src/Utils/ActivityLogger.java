package Utils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class ActivityLogger {
    static Handler fileHandler = null;
    private static final Logger logger = Logger.getLogger(ActivityLogger.class.getName());

    public static void log(String logMessage) {
        try {
            fileHandler = new FileHandler("./login_activity.txt", true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler((fileHandler));
            logger.info(logMessage);
        } catch (IOException e) {
            logger.severe("----- User activity could not be logged! -----");
            e.printStackTrace();
        }
    }
}