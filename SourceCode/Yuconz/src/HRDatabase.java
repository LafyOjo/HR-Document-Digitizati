 import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

/**
 * stores all the records of the employees
 *
 * @author Alice Jaffray and Kieran D'Arcy
 * @version 2019/02/28
 */
public class HRDatabase {
    private final String DATABASE;
    private final String FILENAME;
    private Document document;

    /**
     * constructor
     */
    public HRDatabase(String filename, String database) {
        DATABASE = database;
        FILENAME = filename;
    }

    /**
     * Connects to the Authentication database.
     *
     * @return A connection to the database.
     */
    private Connection connect() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(DATABASE);
        } catch (Exception ex) {
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        }
        return con;
    }

    /**
     * Get a user object from the hr database.
     * @param empNo the user to get.
     * @return a user object with the user's information.
     */
    public User getUser(String empNo) {
        // SQL Query
        String sql = "SELECT * FROM employees WHERE empID = ?";
        try(Connection con = this.connect();
            PreparedStatement prep = con.prepareStatement(sql)) {
            prep.setString(1, empNo);

            ResultSet results = prep.executeQuery();
            if(results.getString(1) != null) {
                String user = results.getString(1);
                String department = results.getString(2);
                String manager = results.getString(3);
                String accessString = results.getString(4);
                AccessLevel access;
                switch (accessString) {
                    case "employee": access = AccessLevel.EMPLOYEE; break;
                    case "hremployee": access = AccessLevel.HREMPLOYEE; break;
                    case "manager": access = AccessLevel.MANAGER; break;
                    case "director": access = AccessLevel.DIRECTOR; break;
                    default: access = AccessLevel.EMPLOYEE; break;
                }
                return new User(user, department, access, manager);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Add a user to the HR database.
     * @param empNo employee number to add.
     * @param department department the user works in.
     * @param manager the employee number of the user's direct superior.
     * @param access the access level of the new user.
     * @return true if successful.
     */
    boolean addUser(String empNo, String department, String manager, String access) {
        //Query
        String sql = "INSERT INTO employees (empID, department, manager, access) values (?, ?, ?, ?);";

        try (Connection con = this.connect();
             PreparedStatement prep = con.prepareStatement(sql)) {
            prep.setString(1, empNo);
            prep.setString(2, department);
            prep.setString(3, manager);
            prep.setString(4, access);
            prep.executeUpdate();
            return true;
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx.getMessage());
            return false;
        }
    }

    /**
     * allows user to read the personal details record of an employee
     *
     * @param empNo     the number of the employee who's record it refers to
     * @param requester the user who has requested to view the record
     * @return personal details record of an employee or null otherwise
     */
    public PersonalDetails readPersonalDetails(String empNo, User requester) {
        if (requester.getAccessLevel().toString().equals("hremployee") || requester.getAccessLevel().toString().equals("director") || empNo.equals(requester.getEmpNo())) {
            // SQL Query
            String sql = "SELECT * FROM PersonalDetails where empNo = ?;";
            try (Connection con = this.connect();
                 PreparedStatement prep = con.prepareStatement(sql)) {
                // Fill placeholders (? characters).
                prep.setString(1, empNo);

                ResultSet results = prep.executeQuery();
                if(results.getString(1) != null) {
                    PersonalDetails document = new PersonalDetails(empNo);
                    document.setForename(results.getString(2));
                    document.setSurname(results.getString(3));
                    document.setDob(results.getString(4));
                    document.setMobileNo(results.getString(5));
                    document.setTelephoneNo(results.getString(6));
                    document.setEmergContact(results.getString(7));
                    document.setEmergTel(results.getString(8));
                    writeToFile(requester.getEmpNo(), empNo + ".ReadPersonalDetails", true);
                    return document;
                }
            } catch(SQLException e) {
                System.err.println(e.getMessage());
            }

        }
        writeToFile(requester.getEmpNo(), empNo + ".ReadPersonalDetails", false);
        return null;
    }

    /**
     * allows a user to create a record of personal details of an employee
     *
     * @param empNo     the employee number who the record refers to
     * @param requester the user who is requesting to create a personal details record
     * @return true if a new record of personal details is created and false otherwise
     */
    public boolean createPersonalDetails(String empNo, User requester) {
        if (requester.getAccessLevel().toString().equals("hremployee")) {
            //SQL query
            String sql = "INSERT INTO PersonalDetails (empNo) VALUES (?);";
            try (Connection con = this.connect();
                 PreparedStatement prep = con.prepareStatement(sql)) {
                //fill placeholder
                prep.setString(1, empNo);
                prep.executeUpdate();
                writeToFile(requester.getEmpNo(), empNo + ".CreatePersonalDetails", true);
                return true;
            } catch (SQLException sqlEx) {
                System.err.println(sqlEx.getMessage());
                writeToFile(requester.getEmpNo(), empNo + ".CreatePersonalDetails", false);
                return false;
            }
        } else {
            writeToFile(requester.getEmpNo(), empNo + ".CreatePersonalDetails", false);
            System.err.println("Access level " + requester.getAccessLevel().toString());
            return false;
        }
    }

    /**
     * allows a user to edit a personal details record
     *
     * @param empNo     the number of the employee the record is about
     * @param field     the field of the record the user wants to change
     * @param newVal    the new value of the field
     * @param requester the user who is requesting to change the record or null otherwise
     */
    public PersonalDetails amendPersonalDetails(String empNo, String field, String newVal, User requester) {
        if (requester.getAccessLevel() == AccessLevel.HREMPLOYEE || empNo.equals(requester.getEmpNo())) {
            //SQL query
            String sql = "UPDATE PersonalDetails SET "+field+" = ? WHERE empNo =?;";
            try (Connection con = this.connect();
                 PreparedStatement prep = con.prepareStatement(sql)) {
                //fill placeholder
                prep.setString(1, newVal);
                prep.setString(2, empNo);
                //Execute statement
                prep.executeUpdate();
                writeToFile(requester.getEmpNo(), empNo + ".AmendPersonalDetails", true);
                return readPersonalDetails(empNo, requester);
            } catch (SQLException sqlEx) {
                System.err.println(sqlEx.getMessage());
            }
        }
        writeToFile(requester.getEmpNo(), empNo + ".AmendPersonalDetails", false);
        return null;
    }

    /**
     * allows user to sign a review
     *
     * @param signer the user who signs the record so it's marked as finished
     */
    public void signReview(User signer) {

    }

    /**
     * allows a user to create a review record
     *
     * @param empNo     the number of the employee who the record is about
     * @param requester the user requesting to create the record
     * @param reviewer  the user who reviewed the employee
     * @return true if the record is created and false otherwise
     */
    public AnnualReview createReviewRecord(String empNo, User requester, User reviewer) {
/*        if (document.getEmpNo().equals(empNo) || reviewer.getEmpNo().equals(reviewer)) {
            document = new AnnualReview(empNo, null, null, null, null, null, null);
            authRecord = new AuthRecord(requester.getEmpNo(), null, true);
            authRecords.add(authRecord);
            return true;
        } else {
            authRecord = new AuthRecord(requester.getEmpNo(), null, false);
            authRecords.add(authRecord);
            return false;
        }
        */
        return null;
    }

    /**
     * allows a user to edit a review record
     *
     * @param empNo     the employee's number who the record is about
     * @param year      the year the review was created
     * @param requester the user requesting to amend the record
     */
/*    public AnnualReview amendReviewRecord(String empNo, String year, User requester) {
        if (annualReview.getFirstReviewer().equals(requester.getEmpNo()) ||
                annualReview.getSecondReviewer().equals(requester.getEmpNo())) {
        }
        return null;
    }*/

    /**
     * allows a user to read the review record of an employee
     *
     * @param empNo     the number of the employee the record refers to
     * @param year      the year the review took place
     * @param requester the user who is requesting to read the review
     * @return the document to be read
     */
    public AnnualReview readReviewRecord(String empNo, String year, User requester) {
/*        if (requester.getAccessLevel().equals("hremployee")) {

        }
        */
        return null;
    }

    /**
     * Write data to the log file.
     * @param empNo Employee number.
     * @param documentName Document accessed.
     * @param success true if allowed access.
     */
    private void writeToFile(String empNo, String documentName, boolean success){
        BufferedWriter bw = null;
        FileWriter fw = null;

        try{
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String content = empNo + "," +  documentName + "," + dateTime + "," + success + "\n";
            fw = new FileWriter(FILENAME,true);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }   finally {
            try {
                if (bw!= null)
                    bw.close();

                if(fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
