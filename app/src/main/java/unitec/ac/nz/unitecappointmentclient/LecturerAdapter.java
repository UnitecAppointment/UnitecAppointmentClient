package unitec.ac.nz.unitecappointmentclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * A view adapter class to customize the list view for displaying lecturers assigned to a particular student user
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class LecturerAdapter extends ArrayAdapter<LecturerModel> {

    /**
     * Constructor for the adapter class
     *
     * @param context           context from parent display
     * @param lecturers         collection of lecturers to populate the adapted list view
     */
    public LecturerAdapter(Context context, List<LecturerModel> lecturers) {
        super(context, 0, lecturers);
    }

    /**
     * Returns an adapted view for a given lecturer item in the list
     *
     * @param position          index of item in the list view
     * @param convertView       list item view to be adapted
     * @param parent            parent view of all adapted list view items
     * @return                  adapted list view item
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        //obtain a lecturer for a given index position in the list view
        LecturerModel lecturer = getItem(position);

        //if a adapted list item  view has not been created, then inflate one from its repective layout template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lecturer_item, parent, false);
        }

        //obtain references to the sub-view items contained within the adapted list view item
        TextView lblLecturerTitleName = (TextView) convertView.findViewById(R.id.lblLecturerTitleName);
        TextView lblDepartment = (TextView) convertView.findViewById(R.id.lblDepartment);
        TextView lblSubjects = (TextView) convertView.findViewById(R.id.lblSubjects);

        //populate the sub-view items with details for an available appointment
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
