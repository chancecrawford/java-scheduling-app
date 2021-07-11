package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Houses connection settings needed to connect to WGU database, connects and disconnects to database.
 */
public class Database {
    // Database connection parameters
    private static final String DBUSERNAME = "U083ch";
    private static final String DBPASSWORD = "53689201625";
    private static final String DBDRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String CONNECTION_STRING = "jdbc:mysql://wgudb.ucertify.com:3306/WJ083ch";

    private static Connection databaseConnection;

    /**
     * Creates connection via mysql driver
     */
    public static void dbConnect() {
        try {
            Class.forName(DBDRIVER);
            databaseConnection = DriverManager.getConnection(CONNECTION_STRING, DBUSERNAME, DBPASSWORD);
            System.out.println("DB Connected");
        } catch (SQLException | ClassNotFoundException dbError) {
            System.err.println(dbError.getMessage());
        }
    }

    /**
     * Terminates connection to database
     */
    public static void dbDisconnect() {
        try {
            databaseConnection.close();
            System.out.println("DB Disconnected");
        } catch (SQLException dbError) {
            System.err.println(dbError.getMessage());
        }
    }

    /**
     * Gets connection for queries throughout app
     * @return database connection
     */
    public static Connection getDBConnection() {
        return databaseConnection;
    }
}
