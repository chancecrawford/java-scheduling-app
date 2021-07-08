package Models;

public class Division {
    private int countryID, divisionID;
    private String city;

    public Division(int divisionID, String city, int countryID) {
        this.divisionID = divisionID;
        this.city = city;
        this.countryID = countryID;
    }
    // default constructor
    public Division() { }

    public int getDivisionID() { return divisionID; }
    public String getCity() { return city; }
    public int getCountryID() { return countryID; }

    public void setDivisionID(int divisionID) { this.divisionID = divisionID; }
    public void setCity(String city) { this.city = city; }
    public void setCountryID(int countryID) { this.countryID = countryID; }

    // for displaying cities in choice box
    @Override
    public String toString() { return city; }
}
