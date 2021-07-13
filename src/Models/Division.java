package Models;

/**
 * Class object for divisions. Allows for easier data population for cache and displaying
 */
public class Division {
    private int countryID, divisionID;
    private String divisionName;

    /**
     * Constructor for division object
     * @param divisionID
     * @param divisionName
     * @param countryID
     */
    public Division(int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }

    /**
     * Default constructor
     */
    public Division() { }

    // getters for division attributes
    /**
     * Gets requested division id
     * @return division id
     */
    public int getDivisionID() { return divisionID; }

    /**
     * Gets requested division name
     * @return division name
     */
    public String getDivisionName() { return divisionName; }

    /**
     * Gets divisions country id
     * @return division country id
     */
    public int getCountryID() { return countryID; }

    // setters for division attributes (not utilized but could be useful for later devs)
    /**
     * Sets new division id
     * @param divisionID
     */
    public void setDivisionID(int divisionID) { this.divisionID = divisionID; }

    /**
     * Sets new division name
     * @param divisionName
     */
    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }

    /**
     * Sets new division country id
     * @param countryID
     */
    public void setCountryID(int countryID) { this.countryID = countryID; }

    /**
     * For displaying divisions in choice box
     * @return division name
     */
    @Override
    public String toString() { return divisionName; }
}
