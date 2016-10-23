package unitec.ac.nz.unitecappointmentclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zezo on 25/09/16.
 */
public class LecturerModel implements Serializable {

    private String username;
    private String title;
    private String firstName;
    private String lastName;
    private String department;
    private List<String> subjects;

    public LecturerModel(String username, String title, String firstName,
                         String lastName, String department) {
        this.username = username;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        subjects = new ArrayList<>();
    }


    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDepartment() {
        return department;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void addSubject(String subject) {
        subjects.add(subject);
    }

    public String toString() {
        return title + " " + firstName + " " + lastName;
    }
}