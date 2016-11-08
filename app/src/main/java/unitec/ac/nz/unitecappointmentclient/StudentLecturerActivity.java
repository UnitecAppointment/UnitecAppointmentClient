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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Student associated lecturers activity, used by students to select a lecturer to book an appointment with
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class StudentLecturerActivity extends AppCompatActivity implements OnClickListener,
        OnItemClickListener {

    private ListView lstLecturers;              //list display of associated lecturers
    private Button btnMenu;                     //button to go back to main menu
    private String username;                    //student username
    private String firstName;                   //first name
    private String lastName;                    //last name
    private List<LecturerModel> lecturers;      //list model of available lecturers

    /**
     * Create and initialise the activity view
     *
     * @param savedInstanceState           any state information persisted from activity's previous life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call ancestor constructor for proper initialisation, assign the respective layout for
        // this activity's view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lecturer);
        // obtain intent passed from previous activity
        Intent intent = getIntent();
        // obtain extra information passed from previous activity via intent
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        username = intent.getStringExtra("username");
        lecturers = (ArrayList<LecturerModel>)intent.getSerializableExtra("lecturers");
        //create adapter using intent obtained model data for lecturer list display
        LecturerAdapter lecturerAdapter = new LecturerAdapter(this, lecturers);
        lstLecturers = (ListView) findViewById(R.id.lstLecturers);
        lstLecturers.setAdapter(lecturerAdapter);
        // attach listener to handle selection touch events
        lstLecturers.setOnItemClickListener(this);
        btnMenu = (Button) findViewById(R.id.btnMenu1);
        // attach listener to handle button selection touch events
        btnMenu.setOnClickListener(this);
    }

    /**
     * Call back handler for events arising from touch click event listener
     *
     * @param v           view responsible for raising the touch click event
     */
    @Override
    public void onClick(View v) {
        if (v == btnMenu) {
            //back to main menu, pass back menu view information via intent
            Intent intent = new Intent(StudentLecturerActivity.this, MainStudentActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
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
            // create JSON object with details from the list lecturer model to book
            // view available appointments for a particular lecturer
            JSONObject userDetails = new JSONObject();
            LecturerModel lecturer = (LecturerModel) lstLecturers.getItemAtPosition(position);
            userDetails.put("username", lecturer.getUsername());
            // pass Json object to asynchronous task thread responsible for web service call
            new AsyncAvailableAppointmentTask(userDetails).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Private helper class to handle asynchronous web service call for obtaining available appointments for a lecturer
     *
     * @author      Marzouq Almarzooq (1380949)
     * @author      Nawaf Altuwayjiri (1377387)
     */
    private class AsyncAvailableAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject userDetails;             // Json object with lecturer details
        private ProgressDialog progressDialog;      // dialog for progress of web service call
        private JSONObject response;                // web service response Json object

        /**
         * Constructor for asynchronous task
         *
         * @param userDetails           Json object with lecturer details
         */
        public AsyncAvailableAppointmentTask(JSONObject userDetails) {
            // initialise task object attributes
            this.userDetails = userDetails;
            progressDialog = new ProgressDialog(StudentLecturerActivity.this);
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
            response = WebService.invokeWebService(WebService.GET_AVAILABLE_APPOINTMENT_METHOD,
                    WebService.GET_AVAILABLE_APPOINTMENT_PARAMETER, userDetails);
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
                    toast = Toast.makeText(StudentLecturerActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    // successful response result
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
                        // Json result returns available appointments for a lecturer
                        JSONArray appointments = response.getJSONArray("appointments");
                        List<AppointmentModel> appointmentsList = new ArrayList<>();
                        for (int index = 0; index < appointments.length(); index++) {
                            JSONObject appointment = appointments.getJSONObject(index);
                            AppointmentModel appointmentItem = new AppointmentModel(appointment.getString("date"),
                                    appointment.getString("start"), appointment.getString("end"));
                            appointmentsList.add(appointmentItem);
                        }
                        //pass avaialble appointments via intent to appointment booking view
                        Intent intent = new Intent(StudentLecturerActivity.this, AvailableAppointmentsActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("lecturers", (ArrayList<LecturerModel>)lecturers);
                        intent.putExtra("lecturerUsername", userDetails.getString("username"));
                        intent.putExtra("appointments", (ArrayList<AppointmentModel>)appointmentsList);
                        startActivity(intent);
                    } else {
                        // available appointments for lecturer
                        toast = Toast.makeText(StudentLecturerActivity.this, "No Available Appointments", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                //invalid Json
                e.printStackTrace();
                toast = Toast.makeText(StudentLecturerActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
