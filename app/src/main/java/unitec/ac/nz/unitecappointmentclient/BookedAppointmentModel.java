package unitec.ac.nz.unitecappointmentclient;

import java.io.Serializable;

/**
 * A class representing the data model for a booked appointment. This class is also serializable
 * so its objects can be passed as extra information between Activities via Intents.
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class BookedAppointmentModel implements Serializable {

    private final String username;          //username of the person the appointment is made with (student for lecturer booking view, lecturer for student booking view)
    private final String firstName;         //user's first name
    private final String lastName;          //user's last name
    private final String date;              //appointment date
    private final String start;             //appointment start time
    private final String end;               //appointment end time
    private final String status;            //appointment status (booked, or cancelled)

    /**
     * Constructor for a booked appointment data model
     *
     * @param username       username of the person the appointment is made with (student for lecturer booking view, lecturer for student booking view)
     * @param firstName      user's first name
     * @param lastName       user's last name
     * @param date           appointment date
     * @param start          appointment start time
     * @param end            appointment end time
     * @param status         appointment status (booked, or cancelled)
     */
    public BookedAppointmentModel(String username, String firstName, String lastName, String date, String start, String end, String status) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    /**
     * @return the username
     */
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
