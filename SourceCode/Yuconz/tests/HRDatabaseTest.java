import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class HRDatabaseTest {

    private HRDatabase hr = new HRDatabase("DatabasesTest/AuthorisationRecordsTest.csv", "jdbc:sqlite:DatabasesTest/YuconzTest.db");

    @BeforeEach
    void setUp() {
        hr.addUser("cfi000", "human resources", "mro000", "hremployee");
        hr.addUser("mro000", "human resources", null, "director");
        hr.addUser("aaa000", "services delivery", null, "employee");
        hr.addUser("man000", "administration", null, "manager");

    }

    @AfterEach
    void tearDown() {
        try {
            Connection con = this.connect("jdbc:sqlite:DatabasesTest/YuconzTest.db");
            Statement s = con.createStatement();
            String sql = "DELETE FROM employees;";
            s.executeUpdate(sql);
        } catch (SQLException e) {
            e.getMessage();
        }
        try {
            Connection con = this.connect("jdbc:sqlite:DatabasesTest/YuconzTest.db");
            Statement s = con.createStatement();
            String sql = "DELETE FROM PersonalDetails;";
            s.executeUpdate(sql);
        } catch (SQLException e) {
            e.getMessage();
        }

    }

    @Test
    void getUserHREmployee() {
        assertEquals("cfi000", hr.getUser("cfi000").getEmpNo(), "User object login wrong.");
        assertEquals(AccessLevel.HREMPLOYEE, hr.getUser("cfi000").getAccessLevel(), "User object access wrong.");
        assertEquals("human resources", hr.getUser("cfi000").getDepartment(), "User object department wrong.");
        assertEquals("mro000", hr.getUser("cfi000").getManager(), "User object manager wrong.");
    }

    @Test
    void getUserDirector() {
        assertEquals("mro000", hr.getUser("mro000").getEmpNo(), "User object login wrong.");
        assertEquals(AccessLevel.DIRECTOR, hr.getUser("mro000").getAccessLevel(), "User object access wrong.");
        assertEquals("human resources", hr.getUser("mro000").getDepartment(), "User object department wrong.");
        assertNull(hr.getUser("mro000").getManager(), "User object manager wrong.");
    }
    @Test
    void getUserEmployee() {
        assertEquals("aaa000", hr.getUser("aaa000").getEmpNo(), "User object login wrong.");
        assertEquals(AccessLevel.EMPLOYEE, hr.getUser("aaa000").getAccessLevel(), "User object access wrong.");
        assertEquals("services delivery", hr.getUser("aaa000").getDepartment(), "User object department wrong.");
        assertNull(hr.getUser("aaa000").getManager(), "User object manager wrong.");
    }
    @Test
    void getUserManager() {
        assertEquals("man000", hr.getUser("man000").getEmpNo(), "User object login wrong.");
        assertEquals(AccessLevel.MANAGER, hr.getUser("man000").getAccessLevel(), "User object access wrong.");
        assertEquals("administration", hr.getUser("man000").getDepartment(), "User object department wrong.");
        assertNull(hr.getUser("man000").getManager(), "User object manager wrong.");
    }

    @Test
    void addUser() {
        assertTrue(hr.addUser("cfi001", "human resources", "mro000", "hremployee"), "User was not added to database.");
        assertFalse(hr.addUser("cfi001", "human resources", "mro000", "hremployee"), "User was able to be added more than once. ");
    }

    @Test
    void readPersonalDetails() {
        hr.createPersonalDetails("cfi000", hr.getUser("cfi000"));
        assertEquals(new PersonalDetails("cfi000").getEmpNo(), hr.readPersonalDetails("cfi000", hr.getUser("cfi000")).getEmpNo());
    }

    @Test
    void readNonExistantPersonalDetails() {
        assertNull(hr.readPersonalDetails("emp001", hr.getUser("cfi000")), "Not null result.");
    }

    @Test
    void createPersonalDetails() {
        assertTrue(hr.createPersonalDetails("cfi000", hr.getUser("cfi000")));
        assertFalse(hr.createPersonalDetails("cfi000", hr.getUser("cfi000")));
    }

    @Test
    void illegalCreatePersonalDetails() {
        assertFalse(hr.createPersonalDetails("aaa000", hr.getUser("aaa000")));
    }

    @Test
    void amendOwnPersonalDetails() {
        hr.createPersonalDetails("aaa000", hr.getUser("cfi000"));
        assertNull(hr.readPersonalDetails("aaa000", hr.getUser("aaa000")).getForename(), "Forename did not start empty.");
        hr.amendPersonalDetails("aaa000", "forename", "John", hr.getUser("aaa000"));
        assertEquals("John", hr.readPersonalDetails("aaa000", hr.getUser("aaa000")).getForename(), "Forename was not changed.");
    }

    @Test
    void amendPersonalDetailsHR() {
        hr.createPersonalDetails("aaa000", hr.getUser("cfi000"));
        assertNull(hr.readPersonalDetails("aaa000", hr.getUser("cfi000")).getForename(), "Forename did not start empty.");
        hr.amendPersonalDetails("aaa000", "forename", "John", hr.getUser("cfi000"));
        assertEquals("John", hr.readPersonalDetails("aaa000", hr.getUser("cfi000")).getForename(), "Forename was not changed.");
    }

    private Connection connect(String url) {
        Connection con = null;
        try{
            con = DriverManager.getConnection(url);
        } catch (Exception ex) {
            System.exit(0);
        }
        return con;
    }
}