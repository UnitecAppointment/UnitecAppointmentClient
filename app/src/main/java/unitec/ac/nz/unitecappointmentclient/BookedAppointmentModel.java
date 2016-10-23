package unitec.ac.nz.unitecappointmentclient;

import java.io.Serializable;

/**
 * Created by zezo on 8/10/16.
 */
public class BookedAppointmentModel implements Serializable {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final String date;
    private final String start;
    private final String end;
    private final String status;

    public BookedAppointmentModel(String username, String firstName, String lastName, String date, String start, String end, String status) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public String getEnd() {
        return end;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
}
