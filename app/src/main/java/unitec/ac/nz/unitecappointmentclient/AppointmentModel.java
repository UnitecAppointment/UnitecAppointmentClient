package unitec.ac.nz.unitecappointmentclient;

import java.io.Serializable;

/**
 * Created by zezo on 26/09/16.
 */
public class AppointmentModel implements Serializable {

    private final String date;
    private final String start;
    private final String end;

    public AppointmentModel(String date, String start, String end) {
        this.date = date;
        this.start = start;
        this.end = end;
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

    public String toString() {
        return date + " " + start + " " + end;
    }

}
