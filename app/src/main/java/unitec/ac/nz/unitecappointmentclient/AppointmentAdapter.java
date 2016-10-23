package unitec.ac.nz.unitecappointmentclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zezo on 26/09/16.
 */
public class AppointmentAdapter extends ArrayAdapter<AppointmentModel> {
    public AppointmentAdapter(Context context, List<AppointmentModel> appointments) {
        super(context, 0, appointments);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AppointmentModel appointment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.appointment_item, parent, false);
        }

        TextView lblDate = (TextView) convertView.findViewById(R.id.lblDate);
        TextView lblStart = (TextView) convertView.findViewById(R.id.lblStart);
        TextView lblEnd = (TextView) convertView.findViewById(R.id.lblEnd);

        lblDate.setText(appointment.getDate());
        lblStart.setText(appointment.getStart());
        lblEnd.setText(appointment.getEnd());

        return convertView;
    }
}
