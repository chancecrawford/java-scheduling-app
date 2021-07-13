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
    /**
     * Gets requested user id
     * @return user id
     */
    public int getId() { return id; }

    /**
     * Gets requested username
     * @return username
     */
    public String getUsername() { return username; }

    /**
     * Gets requested user password
     * @return user password
     */
    public String getPassword() { return password; }

    // setters for user attributes (these could be useful for admin roles later on)
    /**
     * Sets new user id
     * @param id
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets new username
     * @param username
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Sets new user password (could be useful for a forgot password feature)
     * @param password
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * For displaying in user choice box in add/edit appointment views
     * @return
     */
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
