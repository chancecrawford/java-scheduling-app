package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is for logging user login activity to a text file in the project source directory.
 */
public class ActivityLogger {
//    private final Logger logger = Logger.getLogger(ActivityLogger.class.getName());

    /**
     * Takes in specified log message and creates a new login_activity.txt file if one doesn't already exist or
     * appends to an existing one.
     * @param logMessage retrieved to create/add to log file
     */
    public static void log(String logMessage) {
        try {
            FileWriter fileWriter = new FileWriter("./login_activity", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(logMessage);
            bufferedWriter.newLine();
            bufferedWriter.close();
    } catch (IOException e) {
            e.printStackTrace();
        }
    }
}