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
    public BookedAppointmentAdapter(Context context, List<BookedAppointmentModel> bookedAppointments) {
        super(context, 0, bookedAppointments);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        BookedAppointmentModel bookedAppointment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.booked_appointment_item, parent, false);
        }

        TextView lblFirstName = (TextView) convertView.findViewById(R.id.lblFirstName);
        TextView lblLastName = (TextView) convertView.findViewById(R.id.lblLastName);
        TextView lblDate = (TextView) convertView.findViewById(R.id.lblDate);
        TextView lblStart = (TextView) convertView.findViewById(R.id.lblStart);
        TextView lblEnd = (TextView) convertView.findViewById(R.id.lblEnd);
        TextView lblStatus = (TextView) convertView.findViewById(R.id.lblStatus);

        lblFirstName.setText(bookedAppointment.getFirstName());
        lblLastName.setText(bookedAppointment.getLastName());
        lblDate.setText(bookedAppointment.getDate());
        lblStart.setText(bookedAppointment.getStart());
        lblEnd.setText(bookedAppointment.getEnd());
        lblStatus.setText(bookedAppointment.getStatus());

        return convertView;
    }
}
