package unitec.ac.nz.unitecappointmentclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * A view adapter class to customize the list view for displaying booked appointments
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class BookedAppointmentAdapter extends ArrayAdapter<BookedAppointmentModel> {

    /**
     * Constructor for the adapter class
     *
     * @param context                 context from parent display
     * @param bookedAppointments      collection of booked appointments to populate the adapted list view
     */
    public BookedAppointmentAdapter(Context context, List<BookedAppointmentModel> bookedAppointments) {
        super(context, 0, bookedAppointments);
    }


    /**
     * Returns an adapted view for a given booked appointment item in the list
     *
     * @param position          index of item in the list view
     * @param convertView       list item view to be adapted
     * @param parent            parent view of all adapted list view items
     * @return                  adapted list view item
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        BookedAppointmentModel bookedAppointment = getItem(position);

        //obtain a booked appointment for a given index position in the list view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.booked_appointment_item, parent, false);
        }

        //if a adapted list item view has not been created, then inflate one from its repective layout template
        TextView lblFirstName = (TextView) convertView.findViewById(R.id.lblFirstName);
        TextView lblLastName = (TextView) convertView.findViewById(R.id.lblLastName);
        TextView lblDate = (TextView) convertView.findViewById(R.id.lblDate);
        TextView lblStart = (TextView) convertView.findViewById(R.id.lblStart);
        TextView lblEnd = (TextView) convertView.findViewById(R.id.lblEnd);
        TextView lblStatus = (TextView) convertView.findViewById(R.id.lblStatus);

        //populate the sub-view items with details for a booked appointment
        lblFirstName.setText(bookedAppointment.getFirstName());
        lblLastName.setText(bookedAppointment.getLastName());
        lblDate.setText(bookedAppointment.getDate());
        lblStart.setText(bookedAppointment.getStart());
        lblEnd.setText(bookedAppointment.getEnd());
        lblStatus.setText(bookedAppointment.getStatus());

        return convertView;
    }
}
