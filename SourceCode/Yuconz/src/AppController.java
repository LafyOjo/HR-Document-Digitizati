import java.util.Scanner;
/**
 * * controls the actions to everything
 *  *
 *  * @author Alice Jaffray and Kieran D'Arcy
 *  * @version 2019/02/28
 */
public class AppController {
    private Scanner scan;
    private User loggedInUser;
    private AccessLevel accessLevel;
    private HRDatabase hrDatabase;
    private AuthServer authServer;
    private boolean loggedIn;

    /**
     * constructor
     * @param hr HR Database object to use.
     * @param a AuthServer object to use.
     */
    AppController(HRDatabase hr, AuthServer a) {
        scan = new Scanner(System.in);
        hrDatabase = hr;
        authServer = a;
        loggedIn = false;
    }

    /**
     * Calls display methods for the user.
     */
    void runController() {
        while(!loggedIn) {
            loginPrompt();
        }
        baseMainMenu();
        switch(accessLevel) {
            case EMPLOYEE: employeeMainMenu(); break;
            case HREMPLOYEE: hREmployeeMainMenu(); break;
            case DIRECTOR: directorMainMenu(); break;
            default: employeeMainMenu(); break;
        }
    }

    /**
     * Logs the user into the system
     *
     * @param empNo employee number of the user
     * @param password password of the user
     * @return true if the login was successful and false otherwise
     */
    boolean login(String empNo, String password){
        String access = authServer.authenticate(empNo, password);
        if(access.equals("denied")) {
            loggedIn = false;
            return false;
        } else {
            switch(access) {
                case "employee": accessLevel = AccessLevel.EMPLOYEE; break;
                case "hremployee": accessLevel = AccessLevel.HREMPLOYEE; break;
                case "manager": accessLevel = AccessLevel.MANAGER; break;
                case "director": accessLevel = AccessLevel.DIRECTOR; break;
                default: return false;
            }
            loggedInUser = hrDatabase.getUser(empNo);
            loggedIn = true;
            System.out.println(accessLevel.toString());
            return true;
        }
    }

    /**
     * logs the user out.
     */
    void logout(){
        System.out.println("Logged Out");
        loggedInUser = null;
        loggedIn = false;
    }

    /**
     * Get the personal details document for a different employee.
     * @param empNo The owner of the document
     * @return the document associated with empNo.
     */
    private PersonalDetails readPersonalDetails(String empNo) {
        return hrDatabase.readPersonalDetails(empNo, loggedInUser);
    }

    /**
     * Create a new blank document for the employee associated with an employee number.
     * @param empNo the employee the document is for.
     * @return true if successful.
     */
    private boolean createPersonalDetails(String empNo) {
        return hrDatabase.createPersonalDetails(empNo, loggedInUser);
    }

    /**
     * Amend a personal details document associated with an employee number.
     * @param empNo employee number
     * @param field field to change
     * @param newVal new value for the field.
     */
    private void amendPersonalDetails(String empNo, String field, String newVal) {
        hrDatabase.amendPersonalDetails(empNo, field, newVal, loggedInUser);
    }

    /**
     * sets the user to have an employee access level
     */
    void setBasicAccess(){
        accessLevel = AccessLevel.EMPLOYEE;
    }

    /**
     * Getter for access level.
     * @return access level for the logged in user.
     */
    AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Getter for logged in user.
     * @return the logged in user.
     */
    User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Getter for if anyone is logged into the system.
     * @return true if there is a user logged in.
     */
    boolean isLoggedIn() {
        return loggedIn;
    }

    // Methods for displaying menus and such. Untestable, so out of the way.

    /**
     * Displays the login screen functionality.
     */
    private void loginPrompt() {
        System.out.println("Welcome to Yuconz Document System. Please enter your employee number and password:");
        System.out.println();
        System.out.print("Employee Number: ");
        String empNo = scan.next();
        System.out.println();
        System.out.print("Password: ");
        String password = scan.next();
        if(login(empNo, password)) {
            System.out.println();
            System.out.println("Logged In");
            // Higher level users get the option to login as a base level employee.
            try {
                if (accessLevel != AccessLevel.EMPLOYEE) {
                    basicAccessPrompt();
                }
            } catch (NullPointerException ex) {
                System.out.println("User not present in HR database, contact system administrator.");
            }
        } else {
            System.out.println();
            System.out.println("Invalid employee number or password.");
        }
    }

    /**
     * User access menu.
     */
    private void basicAccessPrompt() {
        while (true) {
            System.out.print("Would you like to login with base employee access? (y/n): ");
            String option = scan.next().toLowerCase();
            if (option.equals("y")) {
                setBasicAccess();
                return;
            } else if (option.equals("n")) {
                return;
            } else {
                System.out.println("That is not a valid option. Please enter either 'y' or 'n' (no quotes).");
            }
        }
    }

    /**
     * Basic user main menu.
     */
    private void baseMainMenu() {
        System.out.println("Welcome to the Yuconz document system.");
        System.out.println("Please select an option.");
        System.out.println();

        System.out.println("1. Logout");
        System.out.println("2. Read Own Personal Details");
    }

    /**
     * Options for employee.
     */
    private void employeeMainMenu() {
        System.out.println("3. Amend own personal details.");
        String option = scan.next();
        switch (option) {
            case "1": logout(); break;
            case "2": readOwnPersonalDetails(); break;
            case "3": amendOwnPersonalDetails(); break;
            default: System.out.println("That is not a valid option.");
        }
        runController();
    }

    /**
     * Options for HR Employee.
     */
    private void hREmployeeMainMenu() {
        System.out.println("3. Add new login");
        System.out.println("4. Read other personal details.");
        System.out.println("5. Create new personal details");
        System.out.println("6. Amend existing personal details.");

        String option = scan.next();
        switch (option) {
            case "1": logout(); break;
            case "2": readOwnPersonalDetails(); break;
            case "3": addNewLogin(); break;
            case "4": readOtherPersonalDetails(); break;
            case "5": createPersonalDetails(); break;
            case "6": amendPersonalDetails(); break;
            default: System.out.println("That is not a valid option.");
        }
        runController();
    }

    /**
     * Options for director.
     */
    private void directorMainMenu() {
        System.out.println("3. Read other personal details document.");
        String option = scan.next();
        switch (option) {
            case "1": logout(); break;
            case "2": readOwnPersonalDetails(); break;
            case "3": readOtherPersonalDetails(); break;
            default: System.out.println("That is not a valid option.");
        }
        runController();
    }

    /**
     * Prints result of read own personal details.
     */
    private void readOwnPersonalDetails() {
        PersonalDetails p = readPersonalDetails(loggedInUser.getEmpNo());
        printPersonalDetails(p);
    }

    /**
     * Prints result of read other personal details.
     */
    private void readOtherPersonalDetails() {
        System.out.print("Enter employee number of document owner:");
        PersonalDetails p = readPersonalDetails(scan.next());
        printPersonalDetails(p);

    }

    /**
     * Prints a personal details document to the terminal.
     * @param p The document to print.
     */
    private void printPersonalDetails(PersonalDetails p) {
        if(p != null) {
            System.out.println();
            System.out.println("Employee Number: " + p.getEmpNo());
            System.out.println("Forename: " + p.getForename());
            System.out.println("Surname: " + p.getSurname());
            System.out.println("Date of Birth: " + p.getDob());
            System.out.println("Mobile: " + p.getMobileNo());
            System.out.println("Tel: " + p.getTelephoneNo());
            System.out.println("Emergency Contact: " + p.getEmergContact());
            System.out.println("Emergency Tel: " + p.getEmergTel());
        } else {
            System.out.println("No personal details document for that employee was found. Contact HR.");
        }
    }

    /**
     * Menu for creating personal details.
     */
    private void createPersonalDetails() {
        System.out.print("Enter employee number of new employee: ");
        boolean success = createPersonalDetails(scan.next());
        System.out.println();
        if (success) {
            System.out.println("Success! Please amend the document to add values.");
        } else {
            System.out.println("Failure, document not created. Document may already exist.");
        }
    }

    /**
     * Passes logged in user to amendPersonalDetails menu.
     */
    private void amendOwnPersonalDetails() {
        amendPersonalDetailsMenu(loggedInUser.getEmpNo());
    }

    /**
     * Passes entered user to amendPersonalDetails menu.
     */
    private void amendPersonalDetails() {
        System.out.println("Enter employee number of employee: ");
        String emp = scan.next();
        amendPersonalDetailsMenu(emp);
    }

    /**
     * Amends the personal details for a user using a text interface.
     * @param emp The user to change the details of.
     */
    private void amendPersonalDetailsMenu(String emp) {
        boolean done = false;
        while(!done){
            printPersonalDetails(readPersonalDetails(emp));
            System.out.println("Select a field to change: ");
            System.out.println("1. Forename");
            System.out.println("2. Surname");
            System.out.println("3. Date of Birth");
            System.out.println("4. Mobile Number");
            System.out.println("5. Telephone Number");
            System.out.println("6. Emergency Contact Name");
            System.out.println("7. Emergency Telephone Number");
            boolean selected = false;
            while (!selected) {
                String option = scan.next();
                System.out.print("Please enter the new value: ");
                scan.nextLine();
                switch (option) {
                    case "1": amendPersonalDetails(emp, "forename", scan.nextLine()); selected = true;  break;
                    case "2": amendPersonalDetails(emp, "surname", scan.nextLine()); selected = true;  break;
                    case "3": amendPersonalDetails(emp, "dob", scan.nextLine()); selected = true;  break;
                    case "4": amendPersonalDetails(emp, "mobileNo", scan.nextLine()); selected = true;  break;
                    case "5": amendPersonalDetails(emp, "telephoneNo", scan.nextLine()); selected = true;  break;
                    case "6": amendPersonalDetails(emp, "emergContact", scan.nextLine()); selected = true; break;
                    case "7": amendPersonalDetails(emp, "emergTel", scan.nextLine()); selected = true; break;
                    default: System.out.println("Please select a valid option.");
                }
                System.out.println("Done? (y/n)");
                boolean finished = false;
                while (!finished) {
                    option = scan.next();
                    switch (option) {
                        case "y":
                            finished = true;
                            done = true;
                            break;
                        case "n":
                            finished = true;
                            done = false;
                            break;
                        default: System.out.println("Please select a valid option.");
                    }
                }
            }
        }
        System.out.println("Done!");
    }

    /**
     * Add new login menu for HR employees.
     */
    private void addNewLogin() {
        System.out.println("Enter details for new user:");
        System.out.print("Enter Employee Number: ");
        String user = scan.next();
        System.out.println();
        System.out.print("Enter Password: ");
        String pass = scan.next();
        System.out.println();
        System.out.println("Select Access Level: ");
        System.out.println("1. Employee");
        System.out.println("2. HR Employee");
        System.out.println("3. Manager");
        System.out.println("4. Director");
        String access = "";
        boolean selected = false;
        while(!selected) {
            String option = scan.next();
            switch (option) {
                case "1":
                    access = "employee";
                    selected = true;
                    break;
                case "2":
                    access = "hremployee";
                    selected = true;
                    break;
                case "3":
                    access = "manager";
                    selected = true;
                    break;
                case "4":
                    access = "director";
                    selected = true;
                    break;
                default:
                    System.out.println("Please select a valid option.");
            }
        }
        System.out.println("Select Access Level: ");
        System.out.println("1. Human Resources");
        System.out.println("2. Services Delivery");
        System.out.println("3. Sales and Marketing");
        System.out.println("4. Administration");
        selected = false;
        String department = "";
        while(!selected) {
            String option = scan.next();
            switch (option) {
                case "1":
                    department = "human resources";
                    selected = true;
                    break;
                case "2":
                    department = "services delivery";
                    selected = true;
                    break;
                case "3":
                    department = "sales and marketing";
                    selected = true;
                    break;
                case "4":
                    department = "administration";
                    selected = true;
                    break;
                default:
                    System.out.println("Please select a valid option.");
            }
        }

        if(authServer.insertLogin(user, pass, access) && hrDatabase.addUser(user, department, null, access)) {
            System.out.println(user + " added to database.");
        } else {
            System.out.println(user + " could not be added to database.");
        }
    }
}