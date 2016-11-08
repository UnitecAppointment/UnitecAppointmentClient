package unitec.ac.nz.unitecappointmentclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the data model for an available appointment. This class is also serializable
 * so its objects can be passed as extra information between Activities via Intents.
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class LecturerModel implements Serializable {

    private String username;                //lecturer username
    private String title;                   //title
    private String firstName;               //first name
    private String lastName;                //last name
    private String department;              //department
    private List<String> subjects;          //subjects taught by lecturer

    /**
     * Constructor for a lecturer data model
     *
     * @param username          //lecturer username
     * @param title             //title
     * @param firstName         //first name
     * @param lastName          //last name
     * @param department        //department
     */
    public LecturerModel(String username, String title, String firstName,
                         String lastName, String department) {
        this.username = username;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        subjects = new ArrayList<>();
    }

    /**
     * @return the lecturer username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the lecturer title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the lecturer firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the lecturer lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the lecturer department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @return the list of lecturer subjects
     */
    public List<String> getSubjects() {
        return subjects;
    }

    /**
     * @param subject   subject to add
     */
    public void addSubject(String subject) {
        subjects.add(subject);
    }

    /**
     * String representation of a lecturer data model
     *
     * @return string containing lecturer details
     */
    public String toString() {
        return title + " " + firstName + " " + lastName;
    }
}
