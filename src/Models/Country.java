package Models;

/**
 * Class object for countries. Allows for easier loading from cache and displaying
 */
public class Country {
    private int id;
    private String name;

    /**
     * Constructor for country object
     * @param id
     * @param name
     */
    public Country (int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Default constructor
     */
    public Country() { }

    // getters only for country
    /**
     * Gets requested country id
     * @return country id
     */
    public int getId() { return id; }

    /**
     * Gets requested country name
     * @return country name
     */
    public String getName() { return name; }

    /**
     * For displaying country name in app
     * @return country name attribute
     */
    @Override
    public String toString() {
        return name;
    }
}
