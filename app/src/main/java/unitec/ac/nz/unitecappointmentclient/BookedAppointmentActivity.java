package unitec.ac.nz.unitecappointmentclient;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Booked appointment activity, used by students and lecturers, and responsible for displaying booked
 * appointments for a specific user, and selecting booked appointments for cancelling
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class BookedAppointmentActivity extends AppCompatActivity implements OnClickListener,
        OnItemClickListener {

    private ListView lstBookedAppointments;                         // list view for displaying all booked appointments
    private Button btnMenu;                                         // button for going back to main menu
    private String username;                                        // username
    private String firstName;                                       // user first name
    private String lastName;                                        // user last name
    private String title;                                           // user title (applicable to lecturer user only)
    private String department;                                      // user department (applicable to lecturer user only)
    private String type;                                            // user type (lecturer or student)
    private List<BookedAppointmentModel> bookedAppointments;        // booked appointment data model collection
    private int position;                                           // index of selected item in list view
    private BookedAppointmentAdapter bookedAppointmentAdapter;      // adapter to customize booked appointment list view

    /**
     * Create and initialise the booked appointment activity view
     *
     * @param savedInstanceState           any state information persisted from activity's previous life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call ancestor constructor for proper initialisation, assign the respective layout for
        // this activity's view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_appointments);

        // obtain intent passed from previous activity
        Intent intent = getIntent();
        // obtain extra information passed from previous activity via intent
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        username = intent.getStringExtra("username");
        type = intent.getStringExtra("type");
        if (type.compareTo("lecturer") == 0) {
            title = intent.getStringExtra("title");
            department = intent.getStringExtra("department");
        }
        // create list view adapter from booked appointment data model passed via intent, then pass
        // adapter to customize and populate the list view
        bookedAppointments = (ArrayList<BookedAppointmentModel>) intent.getSerializableExtra("bookedAppointments");
        bookedAppointmentAdapter = new BookedAppointmentAdapter(this, bookedAppointments);
        lstBookedAppointments = (ListView) findViewById(R.id.lstBookedAppointments);
        lstBookedAppointments.setAdapter(bookedAppointmentAdapter);
        // attach listener to handle list view item selection touch events
        lstBookedAppointments.setOnItemClickListener(this);
        btnMenu = (Button) findViewById(R.id.btnMenu1);
        // obtain button used to go back to the previous main menu view, and assign listener
        // to handle button touch events
        btnMenu.setOnClickListener(this);
    }

    /**
     * Call back handler for events arising from touch click event listener
     *
     * @param v           view responsible for raising the touch click evevnt
     */
    @Override
    public void onClick(View v) {
        // if touch click event was raised by back to main menu activity
        if (v == btnMenu) {
            // pass appropriate information according to user type to initialize starting activity
            if (type.compareTo("student") == 0) {
                Intent intent = new Intent(BookedAppointmentActivity.this, MainStudentActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("type", "student");
                startActivity(intent);
            } else {
                Intent intent = new Intent(BookedAppointmentActivity.this, MainLecturerActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("type", "lecturer");
                intent.putExtra("title", title);
                intent.putExtra("department", department);
                startActivity(intent);
            }
        }
    }

    /**
     * Call back handler for events arising from touch click event list view listener
     *
     * @param parent           parent adapted list view
     * @param view             list item view responsible for touch click event
     * @param position         position of list item view
     * @param id               unique id for list item
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        this.position = position; // tracks selected list view item index used by anonymous dialog box class
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (bookedAppointments.get(position).getStatus().compareToIgnoreCase("cancelled") != 0) {
                    // User clicked OK button
                    JSONObject appointmentDetails = new JSONObject();
                    try {
                        appointmentDetails.put("username", bookedAppointments.get(position).getUsername());
                        appointmentDetails.put("date", bookedAppointments.get(position).getDate());
                        appointmentDetails.put("start", bookedAppointments.get(position).getStart());
                        appointmentDetails.put("end", bookedAppointments.get(position).getEnd());
                        appointmentDetails.put("firstName", firstName);
                        appointmentDetails.put("lastName", lastName);
                        appointmentDetails.put("type", type);
                        new AsyncCancelAppointmentTask(appointmentDetails).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast toast = Toast.makeText(BookedAppointmentActivity.this, "Cannot cancel appointment", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class AsyncCancelAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject appointmentDetails;
        private ProgressDialog progressDialog;
        private JSONObject response;

        public AsyncCancelAppointmentTask(JSONObject appointmentDetails) {
            this.appointmentDetails = appointmentDetails;
            progressDialog = new ProgressDialog(BookedAppointmentActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = WebService.invokeWebService(WebService.CANCEL_APPOINTMENT_METHOD,
                    WebService.CANCEL_APPOINTMENT_PARAMETER, appointmentDetails);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            progressDialog.dismiss();
            Toast toast = null;

            try {
                if (response == null || response.getString("result").compareToIgnoreCase("error") == 0) {
                    toast = Toast.makeText(BookedAppointmentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    if (response.getString("result").compareToIgnoreCase("true") == 0 ||
                            response.getString("result").compareToIgnoreCase("booked") == 0 ) {
                        bookedAppointments.remove(position);
                        bookedAppointmentAdapter.notifyDataSetChanged();

                        if (response.getString("result").compareToIgnoreCase("booked") == 0 ) {
                            toast = Toast.makeText(BookedAppointmentActivity.this, "This appointment has been booked", Toast.LENGTH_LONG);
                            toast.show();
                        }

                        if (bookedAppointments.isEmpty()) {
                            Intent intent = new Intent(BookedAppointmentActivity.this, MainStudentActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            startActivity(intent);
                        }
                    } else {
                        toast = Toast.makeText(BookedAppointmentActivity.this, "No Assigned Lecturers", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                toast = Toast.makeText(BookedAppointmentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
