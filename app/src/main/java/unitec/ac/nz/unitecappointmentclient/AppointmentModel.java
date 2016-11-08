package unitec.ac.nz.unitecappointmentclient;

import java.io.Serializable;

/**
 * A class representing the data model for an available appointment. This class is also serializable
 * so its objects can be passed as extra information between Activities via Intents.
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class AppointmentModel implements Serializable {

    private final String date;      // available appointment date
    private final String start;     // available appointment start time
    private final String end;       // available appointment end time

    /**
     * Constructor for an available appointment data model
     *
     * @param date           appointment date
     * @param start          appointment start time
     * @param end            appointment end time
     */
    public AppointmentModel(String date, String start, String end) {
        this.date = date;
        this.start = start;
        this.end = end;
    }

    /**
     * @return the appointment date
     */
    public String getDate() {
        return date;
    }

    /**
     * @return the appointment start
     */
    public String getStart() {
        return start;
    }

    /**
     * @return the appointemnt end
     */
    public String getEnd() {
        return end;
    }

    /**
     * String representation of an appointment data model
     *
     * @return string containing appointment details
     */
    public String toString() {
        return date + " " + start + " " + end;
    }

}
