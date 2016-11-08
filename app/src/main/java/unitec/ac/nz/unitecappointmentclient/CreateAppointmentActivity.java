package unitec.ac.nz.unitecappointmentclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used by lecturers to create appointments for students to book
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class CreateAppointmentActivity extends AppCompatActivity implements View.OnClickListener {

    private String username;                //username
    private String firstName;               //first name
    private String lastName;                //last name
    private String title;                   //title (Dr., Prof., Ms., etc...)
    private String department;              //school department
    private EditText txtDay;                //appointment day of week
    private EditText txtMonth;              //appointment month
    private EditText txtYear;               //appointment year
    private EditText txtStartHour;          //appointment start hour
    private EditText txtStartMinute;        //appointment start minute
    private EditText txtEndHour;            //appointment end hour
    private EditText txtEndMinute;          //appointment minute hour
    private Button btnMakeAppointment;      //button to submit creating an appointment
    private Button btnCancel;               //button to cancel creating an appointment
    private CheckBox chkDaily;              //check box indicate appointment will reoccur daily
    private CheckBox chkWeekly;             //check box indicate appointment will reoccur weekly
    private EditText txtRecurrence;         //number of appointment recurrences

    /**
     * Create and initialise the create appointment activity view
     *
     * @param savedInstanceState           any state information persisted from activity's previous life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call ancestor constructor for proper initialisation, assign the respective layout for
        // this activity's view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        //obtain references to activity sub-views
        ((LinearLayout) findViewById(R.id.background)).getBackground().setAlpha(127);
        txtDay = (EditText) findViewById(R.id.txtDay);
        txtMonth = (EditText) findViewById(R.id.txtMonth);
        txtYear = (EditText) findViewById(R.id.txtYear);
        txtStartHour = (EditText) findViewById(R.id.txtStartHour);
        txtStartMinute = (EditText) findViewById(R.id.txtStartMinute);
        txtEndHour = (EditText) findViewById(R.id.txtEndHour);
        txtEndMinute = (EditText) findViewById(R.id.txtEndMinute);
        btnMakeAppointment = (Button) findViewById(R.id.btnCreateAppointment);
        btnMakeAppointment.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        chkDaily = (CheckBox) findViewById(R.id.chkDialy);
        chkDaily.setOnClickListener(this);
        chkWeekly = (CheckBox) findViewById(R.id.chkWeekly);
        chkWeekly.setOnClickListener(this);
        txtRecurrence = (EditText) findViewById(R.id.txtRecurrence);

        // obtain intent passed from previous activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        title = intent.getStringExtra("title");
        department = intent.getStringExtra("department");
    }

    /**
     * Call back handler for events arising from touch click event listener
     *
     * @param v           view responsible for raising the touch click event
     */
    @Override
    public void onClick(View v) {
        if (v == btnMakeAppointment) {
            //submits details to create an appointment
            JSONObject appointmentDetails = new JSONObject();
            try {
                appointmentDetails.put("username", username);
                appointmentDetails.put("day", txtDay.getText().toString());
                appointmentDetails.put("month", txtMonth.getText().toString());
                appointmentDetails.put("year", txtYear.getText().toString());
                appointmentDetails.put("startHour", txtStartHour.getText().toString());
                appointmentDetails.put("startMinute", txtStartMinute.getText().toString());
                appointmentDetails.put("endHour", txtEndHour.getText().toString());
                appointmentDetails.put("endMinute", txtEndMinute.getText().toString());
                appointmentDetails.put("isDaily", chkDaily.isChecked() ? "true":"false");
                appointmentDetails.put("isWeekly", chkWeekly.isChecked() ? "true":"false");
                appointmentDetails.put("recurrence", txtRecurrence.getText().toString());
                new AsyncCreateAppointmentTask(appointmentDetails).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //cancels and goes back to previous activity
        } else if (v == btnCancel) {
            Intent intent = new Intent(CreateAppointmentActivity.this, MainLecturerActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("title", title);
            intent.putExtra("department", department);
            startActivity(intent);
        } else if (v == chkDaily) {
            // daily indicated then weekly should be deactivated
            if (chkDaily.isChecked()) {
                chkWeekly.setChecked(false);
            }
        } else if (v == chkWeekly) {
            // weekly indicated then daily should be deactivated
            if(chkWeekly.isChecked()) {
                chkDaily.setChecked(false);
            }
        }
    }

    /**
     * Private helper class to handle asynchronous web service call for creating appointments
     *
     * @author      Marzouq Almarzooq (1380949)
     * @author      Nawaf Altuwayjiri (1377387)
     */
    private class AsyncCreateAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject appointmentDetails;      // Json object with appointment creation details
        private ProgressDialog progressDialog;      // dialog for progress of web service call
        private JSONObject response;                // web service response Json object

        /**
         * Constructor for asynchronous task
         *
         * @param appointmentDetails           Json object with appointment creation details
         */
        public AsyncCreateAppointmentTask(JSONObject appointmentDetails) {
            // initialise task object attributes
            this.appointmentDetails = appointmentDetails;
            progressDialog = new ProgressDialog(CreateAppointmentActivity.this);
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
            // make web service call passing appointment details json object, and obtain
            // response back from web service call
            response = WebService.invokeWebService(WebService.CREATE_APPOINTMENT_METHOD,
                    WebService.CREATE_APPOINTMENT_PARAMETER, appointmentDetails);
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
                    toast = Toast.makeText(CreateAppointmentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    // successful response result
                    // appointment cannot be created as there's a class
                    if (response.getString("result").compareToIgnoreCase("false") == 0) {
                        toast = Toast.makeText(CreateAppointmentActivity.this, "This appointment clashes with an existing appointment", Toast.LENGTH_LONG);
                    } else {
                        // appointment created
                        toast = Toast.makeText(CreateAppointmentActivity.this, "Appointment created", Toast.LENGTH_LONG);
                    }
                }
            } catch (JSONException e) {
                //invalid json response
                e.printStackTrace();
                toast = Toast.makeText(CreateAppointmentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
