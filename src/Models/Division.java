package Models;

public class Division {
    private int countryID, divisionID;
    private String divisionName;

    public Division(int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }
    // default constructor
    public Division() { }

    public int getDivisionID() { return divisionID; }
    public String getDivisionName() { return divisionName; }
    public int getCountryID() { return countryID; }

    public void setDivisionID(int divisionID) { this.divisionID = divisionID; }
    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }
    public void setCountryID(int countryID) { this.countryID = countryID; }

    // for displaying cities in choice box
    @Override
    public String toString() { return divisionName; }
}
