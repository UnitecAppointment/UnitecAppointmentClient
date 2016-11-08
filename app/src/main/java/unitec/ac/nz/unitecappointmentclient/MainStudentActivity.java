package unitec.ac.nz.unitecappointmentclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Student main menu activity, used by students to access application functionality
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class MainStudentActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lblWelcome;            //welcome text display
    private Button btnMakeAppointment;      //button to book appointments
    private Button btnViewAppointment;      //button to view booked appointments
    private Button btnLogout;               //button to logout
    private String username;                //student username
    private String firstName;               //last name
    private String lastName;                //first name

    /**
     * Create and initialise the menu activity view
     *
     * @param savedInstanceState           any state information persisted from activity's previous life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call ancestor constructor for proper initialisation, assign the respective layout for
        // this activity's view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);
        //obtain references to activity sub-views
        lblWelcome = (TextView) findViewById(R.id.lblWelcome);
        btnMakeAppointment = (Button) findViewById(R.id.btnMakeAppointment);
        // attach listener to handle button selection touch events
        btnMakeAppointment.setOnClickListener(this);
        btnViewAppointment = (Button) findViewById(R.id.btnViewAppointment);
        // attach listener to handle button selection touch events
        btnViewAppointment.setOnClickListener(this);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        // attach listener to handle button selection touch events
        btnLogout.setOnClickListener(this);

        // obtain intent passed from previous activity
        Intent intent = getIntent();
        // obtain extra information passed from previous activity via intent
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        username = intent.getStringExtra("username");
        lblWelcome.setText("Welcome " + firstName + " " + lastName);
    }

    /**
     * Call back handler for events arising from touch click event listener
     *
     * @param v           view responsible for raising the touch click evevnt
     */
    @Override
    public void onClick(View v) {
        if (v == btnMakeAppointment) {
            // create JSON object with user details to obtain available appointments for booking
            JSONObject userDetails = new JSONObject();
            try {
                userDetails.put("username", username);
                new AsyncMakeAppointmentTask(userDetails).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (v == btnViewAppointment) {
            // create JSON object with user details to obtain booked appointments
            JSONObject userDetails = new JSONObject();
            try {
                userDetails.put("username", username);
                userDetails.put("type", "student");
                new AsyncViewAppointmentTask(userDetails).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (v == btnLogout) {
            //logout
            Intent intent = new Intent(MainStudentActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Private helper class to handle asynchronous web service call for obtaining appointments available for booking
     *
     * @author      Marzouq Almarzooq (1380949)
     * @author      Nawaf Altuwayjiri (1377387)
     */
    private class AsyncViewAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject userDetails;             // Json object with user details
        private ProgressDialog progressDialog;      // dialog for progress of web service call
        private JSONObject response;                // web service response Json object

        /**
         * Constructor for asynchronous task
         *
         * @param userDetails           Json object with user details
         */
        public AsyncViewAppointmentTask(JSONObject userDetails) {
            // initialise task object attributes
            this.userDetails = userDetails;
            progressDialog = new ProgressDialog(MainStudentActivity.this);
        }

        /**
         * Call back handler called prior to asynchronous task execution
         **/
        @Override
        protected void onPreExecute() {
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
            // make web service call passing login details json object, and obtain
            // response back from web service call
            response = WebService.invokeWebService(WebService.VIEW_APPOINTMENT_METHOD,
                    WebService.VIEW_APPOINTMENT_PARAMETER, userDetails);
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
                    toast = Toast.makeText(MainStudentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    // successful response result
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
                        //json result of appointments available for booking
                        JSONArray appointments = response.getJSONArray("appointments");
                        List<BookedAppointmentModel> bookedAppointmentsList = new ArrayList<>();
                        for (int index = 0; index < appointments.length(); index++) {
                            JSONObject appointment = appointments.getJSONObject(index);
                            BookedAppointmentModel bookedAppointmentModelItem = new BookedAppointmentModel(
                                    appointment.getString("username"),
                                    appointment.getString("firstName"),
                                    appointment.getString("lastName"),
                                    appointment.getString("date"),
                                    appointment.getString("start"),
                                    appointment.getString("end"),
                                    appointment.getString("status"));
                            bookedAppointmentsList.add(bookedAppointmentModelItem);
                        }
                        //pass list of available appointments in intent to booked appointment view
                        Intent intent = new Intent(MainStudentActivity.this, BookedAppointmentActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("type", "student");
                        intent.putExtra("bookedAppointments", (ArrayList<BookedAppointmentModel>)bookedAppointmentsList);
                        startActivity(intent);
                    } else {
                        //no appointments available for booking
                        toast = Toast.makeText(MainStudentActivity.this, "No Booked Appointments", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                //invalid json response
                e.printStackTrace();
                toast = Toast.makeText(MainStudentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }

    /**
     * Private helper class to handle asynchronous web service call for obtaining booked appointments
     *
     * @author      Marzouq Almarzooq (1380949)
     * @author      Nawaf Altuwayjiri (1377387)
     */
    private class AsyncMakeAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject userDetails;             // Json object with login details
        private ProgressDialog progressDialog;      // dialog for progress of web service call
        private JSONObject response;                // web service response Json object

        /**
         * Constructor for asynchronous task
         *
         * @param userDetails           Json object with user login details
         */
        public AsyncMakeAppointmentTask(JSONObject userDetails) {
            // initialise task object attributes

            this.userDetails = userDetails;
            progressDialog = new ProgressDialog(MainStudentActivity.this);
        }

        /**
         * Call back handler called prior to asynchronous task execution
         **/
        @Override
        protected void onPreExecute() {
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
            // make web service call passing login details json object, and obtain
            // response back from web service call
            response = WebService.invokeWebService(WebService.MAKE_APPOINTMENT_METHOD,
                    WebService.MAKE_APPOINTMENT_PARAMETER, userDetails);
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
                    toast = Toast.makeText(MainStudentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    // successful response result
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
                        //Json result returns a list lecturers asscoiated with the student
                        JSONArray lecturers = response.getJSONArray("lecturers");
                        List<LecturerModel> lecturesList = new ArrayList<>();
                        for (int index = 0; index < lecturers.length(); index++) {
                            JSONObject lecturer = lecturers.getJSONObject(index);
                            LecturerModel lecturerItem = new LecturerModel(lecturer.getString("username"),
                                    lecturer.getString("title"), lecturer.getString("firstName"),
                                    lecturer.getString("lastName"), lecturer.getString("department"));
                            JSONArray subjects = lecturer.getJSONArray("subjects");
                            for (int index1 = 0; index1 < subjects.length(); index1++) {
                                lecturerItem.addSubject(subjects.getJSONObject(index1).getString("subject"));
                            }
                            lecturesList.add(lecturerItem);
                        }
                        //pass list of lecturers to appointment booking view
                        Intent intent = new Intent(MainStudentActivity.this, StudentLecturerActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("lecturers", (ArrayList<LecturerModel>)lecturesList);
                        startActivity(intent);
                    } else {
                        toast = Toast.makeText(MainStudentActivity.this, "No Assigned Lecturers", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                //invalid json response
                e.printStackTrace();
                toast = Toast.makeText(MainStudentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
