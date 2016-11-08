package unitec.ac.nz.unitecappointmentclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * A view adapter class to customize the list view for displaying available appointments
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class AppointmentAdapter extends ArrayAdapter<AppointmentModel> {

    /**
     * Constructor for the adapter class
     *
     * @param context           context from parent display
     * @param appointments      collection of available appointments to populate the adapted list view
     */
    public AppointmentAdapter(Context context, List<AppointmentModel> appointments) {
        super(context, 0, appointments);
    }

    /**
     * Returns an adapted view for a given appointment item in the list
     *
     * @param position          index of item in the list view
     * @param convertView       list item view to be adapted
     * @param parent            parent view of all adapted list view items
     * @return                  adapted list view item
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //obtain an appointment for a given index position in the list view
        AppointmentModel appointment = getItem(position);

        //if a adapted list item  view has not been created, then inflate one from its repective layout template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.appointment_item, parent, false);
        }

        //obtain references to the sub-view items contained within the adapted list view item
        TextView lblDate = (TextView) convertView.findViewById(R.id.lblDate);
        TextView lblStart = (TextView) convertView.findViewById(R.id.lblStart);
        TextView lblEnd = (TextView) convertView.findViewById(R.id.lblEnd);

        //populate the sub-view items with details for an available appointment
        lblDate.setText(appointment.getDate());
        lblStart.setText(appointment.getStart());
        lblEnd.setText(appointment.getEnd());

        return convertView;
    }
}
