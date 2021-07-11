package Models;

/**
 * Class object for user. Allows for easier tracking of user across application.
 */
public class User {
    private int id;
    private String username;
    private String password;

    /**
     * Constructor for user object
     * @param id
     * @param username
     * @param password
     */
    public User (int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Default constructor
     */
    public User() {}

    // getters for user attributes
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    // setters for user attributes
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
