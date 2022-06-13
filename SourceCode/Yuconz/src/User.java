/**
 * the class is the superclass for all users
 * Stores the details of a user
 *
 * @author Alice Jaffray and Kieran D'Arcy
 * @version 2019/02/28
 */
public class User {
    private String empNo;
    private AccessLevel accessLevel;
    private String department;
    private String manager;

    /**
     * constructor
     * @param empNo
     * @param department
     * @param accessLevel
     */
    public User(String empNo, String department, AccessLevel accessLevel, String manager) {
        this.empNo = empNo;
        this.department = department;
        this.accessLevel = accessLevel;
        this.manager = manager;
    }

    /**
     * gets the employee number of the user
     *
     * @return employee number.
     */
    public String getEmpNo(){
        return empNo;
    }

    /**
     * gets the access level of the user
     *
     * @return the access level of the user
     */
    public AccessLevel getAccessLevel(){
        return accessLevel;
    }

    /**
     * gets user's department.
     * @return the user's department.
     */
    public String getDepartment() {
        return department;
    }

    /**
     * gets the manager of the user.
     * @return manager.
     */
    public String getManager() {
        return manager;
    }
}
