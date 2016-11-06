package unitec.ac.nz.unitecappointmentclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Available appointment activity, used by students, and responsible for displaying available
 * appointments for a specific lecturer, and selecting available appointments for booking
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class AvailableAppointmentsActivity extends AppCompatActivity implements OnClickListener,
        OnItemClickListener {

    private ListView lstAppointments;               // list view for displaying all available lecturer appointments
    private Button btnLecturers;                    // button for going back to lecturer list activity
    private String username;                        // student's username
    private String firstName;                       // student's first name
    private String lastName;                        // student's last name
    private String lecturerUsername;                // lecture username
    private List<LecturerModel> lecturers;          // lecturer data model collection
    private List<AppointmentModel> appointments;    // available appointment data model collection

    /**
     * Create and initialise the available appointment activity view
     *
     * @param savedInstanceState           any state information persisted from activity's previous life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call ancestor constructor for proper initialisation, assign the respective layout for
        // this activity's view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_appointments);

        // obtain intent passed from previous activity
        Intent intent = getIntent();
        // obtain extra information passed from previous activity via intent
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        username = intent.getStringExtra("username");
        lecturers = (ArrayList<LecturerModel>)intent.getSerializableExtra("lecturers");
        lecturerUsername = intent.getStringExtra("lecturerUsername");
        appointments = (ArrayList<AppointmentModel>)intent.getSerializableExtra("appointments");

        // create list view adapter from appointment data model passed via intent, then pass
        // adapter to customize and populate the list view
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(this, appointments);
        lstAppointments = (ListView) findViewById(R.id.lstAppointments);
        lstAppointments.setAdapter(appointmentAdapter);
        // attach listener to handle list view item selection touch events
        lstAppointments.setOnItemClickListener(this);

        // obtain button used to go back to the previous lecturer list view, and assign listener
        // to handle button touch events
        btnLecturers = (Button) findViewById(R.id.btnLecturers);
        btnLecturers.setOnClickListener(this);
    }

    /**
     * Call back handler for events arising from touch click event listener
     *
     * @param v           view responsible for raising the touch click evevnt
     */
    @Override
    public void onClick(View v) {
        // if touch click event was raised by back to lecturer list activity
        if (v == btnLecturers) {
            // create intent and pass extra information required for initializing activity about to be started
            Intent intent = new Intent(AvailableAppointmentsActivity.this, StudentLecturerActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("lecturers", (ArrayList<LecturerModel>)lecturers);
            startActivity(intent);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            // create JSON object with details from the list item appointment model to book
            // a particular available appointment
            JSONObject appointmentDetails = new JSONObject();
            AppointmentModel appointment = (AppointmentModel) lstAppointments.getItemAtPosition(position);
            appointmentDetails.put("studentUsername", username);
            appointmentDetails.put("lecturerUsername", lecturerUsername);
            appointmentDetails.put("date", appointment.getDate());
            appointmentDetails.put("start", appointment.getStart());
            appointmentDetails.put("end", appointment.getEnd());
            // pass Json object to asynchronous task thread responsible for web service call
            new AsyncBookAppointmentTask(appointmentDetails).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Private helper class to handle asynchronous web service call for booking appointments
     *
     * @author      Marzouq Almarzooq (1380949)
     * @author      Nawaf Altuwayjiri (1377387)
     */
    private class AsyncBookAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject appointmentDetails;      // Json object with appointment booking details
        private ProgressDialog progressDialog;      // dialog for progress of web service call
        private JSONObject response;                // web service response Json object

        /**
         * Constructor for asynchronous task
         *
         * @param appointmentDetails           Json object with appointment booking details
         */
        public AsyncBookAppointmentTask(JSONObject appointmentDetails) {
            // initialise task object attributes
            this.appointmentDetails = appointmentDetails;
            progressDialog = new ProgressDialog(AvailableAppointmentsActivity.this);
        }

        /**
         * Call back handler called prior to asynchronous task execution
         **/
        @Override
        protected void onPreExecute() {
            // display progress dialog
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        /**
         * Asynchronous task execution
         *
         * @param params    //extra parameters can be optionally passed
         **/
        @Override
        protected Void doInBackground(Void... params) {
            // make web service call passing appointment details json object, and obtain
            // response back from web service call
            response = WebService.invokeWebService(WebService.BOOK_APPOINTMENT_METHOD,
                    WebService.BOOK_APPOINTMENT_PARAMETER, appointmentDetails);
            return null;
        }

        /**
         * Call back handler called after asynchronous task execution is complete
         *
         * @param arg       //optional arguments passed from asynchronous task completion
         **/
        @Override
        protected void onPostExecute(Void arg) {
            // close progress dialog
            progressDialog.dismiss();
            Toast toast = null;

            try {
                // Json response object availability and result
                if (response == null || response.getString("result").compareToIgnoreCase("error") == 0) {
                    // if unavailable or error result then problem occurred at web server
                    toast = Toast.makeText(AvailableAppointmentsActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    // successful response result
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
                        // appointment successfully booked
                        // go back to previous activity passing any initialisation information needed via intent
                        Intent intent = new Intent(AvailableAppointmentsActivity.this, StudentLecturerActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("lecturers", (ArrayList<LecturerModel>)lecturers);
                        startActivity(intent);
                    } else {
                        // unable to book appointment as it is no longer available
                        toast = Toast.makeText(AvailableAppointmentsActivity.this, "Unable to make appointment", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                toast = Toast.makeText(AvailableAppointmentsActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
