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
 * Lecturer main menu activity, used by lecturers to access application functionality
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class MainLecturerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lblLogin;                  //username display
    private TextView lblName;                   //full name display
    private TextView lblDepartment;             //department display
    private Button btnCreateAppointment;        //button to create appointments
    private Button btnViewAppointment;          //button to view booked appointments
    private Button btnLogout;                   //button to log out
    private String username;                    //username
    private String firstName;                   //first name
    private String lastName;                    //last name
    private String title;                       //title
    private String department;                  //department

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
        setContentView(R.layout.activity_main_lecturer);

        //obtain references to activity sub-views
        lblLogin = (TextView) findViewById(R.id.lblLogin);
        lblName = (TextView) findViewById(R.id.lblName);
        lblDepartment = (TextView) findViewById(R.id.lblDepartment);

        btnCreateAppointment = (Button) findViewById(R.id.btnCreateAppointment);
        // attach listener to handle button selection touch events
        btnCreateAppointment.setOnClickListener(this);
        btnViewAppointment = (Button) findViewById(R.id.btnViewAppointment);
        // attach listener to handle button selection touch events
        btnViewAppointment.setOnClickListener(this);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        // attach listener to handle button selection touch events
        btnLogout.setOnClickListener(this);

        // obtain intent passed from previous activity
        Intent intent = getIntent();
        // obtain extra information passed from previous activity via intent
        username = intent.getStringExtra("username");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        title = intent.getStringExtra("title");
        department = intent.getStringExtra("department");

        lblLogin.setText("Logged in as: " + username);
        lblName.setText(title + " " + firstName + " " + lastName);
        lblDepartment.setText(department);
    }

    /**
     * Call back handler for events arising from touch click event listener
     *
     * @param v           view responsible for raising the touch click evevnt
     */
    @Override
    public void onClick(View v) {
        if (v == btnCreateAppointment) {
            //create appointment function selected
            Intent intent = new Intent(MainLecturerActivity.this, CreateAppointmentActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("username", username);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("title", title);
            intent.putExtra("department", department);
            startActivity(intent);
        } else if (v == btnViewAppointment){
            // create JSON object with user details to obtain booked appointments
            JSONObject userDetails = new JSONObject();
            try {
                userDetails.put("username", username);
                userDetails.put("type", "lecturer");
                new AsyncViewAppointmentTask(userDetails).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //logout
            Intent intent = new Intent(MainLecturerActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Private helper class to handle asynchronous web service call for obtaining booked appointments
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
            progressDialog = new ProgressDialog(MainLecturerActivity.this);
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
                    toast = Toast.makeText(MainLecturerActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    // successful response result
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
                        //obtain booked appointments and pass these to view booked appointment activity via intent
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
                        //create intent and start next activity
                        Intent intent = new Intent(MainLecturerActivity.this, BookedAppointmentActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("title", title);
                        intent.putExtra("department", department);
                        intent.putExtra("type", "lecturer");
                        intent.putExtra("bookedAppointments", (ArrayList<BookedAppointmentModel>)bookedAppointmentsList);
                        startActivity(intent);
                    } else {
                        // no booked appointments
                        toast = Toast.makeText(MainLecturerActivity.this, "No Booked Appointments", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                //invalid json response
                e.printStackTrace();
                toast = Toast.makeText(MainLecturerActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
