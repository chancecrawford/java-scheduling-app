package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // Database connection parameters
    private static final String DBUSERNAME = "U083ch";
    private static final String DBPASSWORD = "53689201625";
    // do we need this for connection?
    private static final String DBDRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String CONNECTION_STRING = "jdbc:mysql://wgudb.ucertify.com:3306/WJ083ch";

    private static Connection databaseConnection;

    public static Connection dbConnect() {
        try {
            Class.forName(DBDRIVER);
            databaseConnection = DriverManager.getConnection(CONNECTION_STRING, DBUSERNAME, DBPASSWORD);
            System.out.println("DB Connected");
        } catch (SQLException | ClassNotFoundException dbError) {
            System.err.println(dbError.getMessage());
        }
        return databaseConnection;
    }

    public static void dbDisconnect() {
        try {
            databaseConnection.close();
            System.out.println("DB Disconnected");
        } catch (SQLException dbError) {
            System.err.println(dbError.getMessage());
        }
    }

    public static Connection getDBConnection() {
        return databaseConnection;
    }
}
