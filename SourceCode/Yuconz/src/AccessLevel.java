/**
 * Enumeration class AccessLevel
 * Represents the different access levels of users
 *
 * @author Alice Jaffray and Kieran D'Arcy
 * @version 2019/02/28
 */
public enum AccessLevel {
    EMPLOYEE("employee"),
    HREMPLOYEE("hremployee"),
    MANAGER("manager"),
    DIRECTOR("director");

    // the access level string
    public final String accessLevel;

    /**
     * Initialise with the corresponding accessLevel string
     * @param accessLevel The accessLevel
     */
    AccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * @return the accessLevel as a string
     */
    public String toString() {
        return accessLevel;
    }

}
