package unitec.ac.nz.unitecappointmentclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zezo on 25/09/16.
 */
public class LecturerAdapter extends ArrayAdapter<LecturerModel> {

    public LecturerAdapter(Context context, List<LecturerModel> lecturers) {
        super(context, 0, lecturers);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LecturerModel lecturer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lecturer_item, parent, false);
        }

        TextView lblLecturerTitleName = (TextView) convertView.findViewById(R.id.lblLecturerTitleName);
        TextView lblDepartment = (TextView) convertView.findViewById(R.id.lblDepartment);
        TextView lblSubjects = (TextView) convertView.findViewById(R.id.lblSubjects);

        lblLecturerTitleName.setText(lecturer.getTitle() + " " + lecturer.getFirstName() + " " +
                lecturer.getLastName());

        lblDepartment.setText(lecturer.getDepartment());

        String subjects = "Subjects: ";
        for (String subject : lecturer.getSubjects()) {
            subjects = subjects.concat(subject + ", ");
        }
        subjects = subjects.substring(0, subjects.length() - 2);
        lblSubjects.setText(subjects);

        return convertView;
    }
}
