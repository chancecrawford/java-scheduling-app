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
    public int getDivisionID() { return divisionID; }
    public String getDivisionName() { return divisionName; }
    public int getCountryID() { return countryID; }
    // setters for division attributes
    public void setDivisionID(int divisionID) { this.divisionID = divisionID; }
    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }
    public void setCountryID(int countryID) { this.countryID = countryID; }

    /**
     * For displaying divisions in choice box
     * @return division name
     */
    @Override
    public String toString() { return divisionName; }
}
