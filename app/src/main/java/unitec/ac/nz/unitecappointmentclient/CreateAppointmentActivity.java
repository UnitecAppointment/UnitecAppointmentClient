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

public class CreateAppointmentActivity extends AppCompatActivity implements View.OnClickListener {

    private String username;
    private String firstName;
    private String lastName;
    private String title;
    private String department;
    private EditText txtDay;
    private EditText txtMonth;
    private EditText txtYear;
    private EditText txtStartHour;
    private EditText txtStartMinute;
    private EditText txtEndHour;
    private EditText txtEndMinute;
    private Button btnMakeAppointment;
    private Button btnCancel;
    private CheckBox chkDaily;
    private CheckBox chkWeekly;
    private EditText txtRecurrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

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

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        title = intent.getStringExtra("title");
        department = intent.getStringExtra("department");
    }

    @Override
    public void onClick(View v) {
        if (v == btnMakeAppointment) {
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
        } else if (v == btnCancel) {
            Intent intent = new Intent(CreateAppointmentActivity.this, MainLecturerActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("title", title);
            intent.putExtra("department", department);
            startActivity(intent);
        } else if (v == chkDaily) {
            if (chkDaily.isChecked()) {
                chkWeekly.setChecked(false);
            }
        } else if (v == chkWeekly) {
            if(chkWeekly.isChecked()) {
                chkDaily.setChecked(false);
            }
        }
    }

    private class AsyncCreateAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject appointmentDetails;
        private ProgressDialog progressDialog;
        private JSONObject response;

        public AsyncCreateAppointmentTask(JSONObject appointmentDetails) {
            this.appointmentDetails = appointmentDetails;
            progressDialog = new ProgressDialog(CreateAppointmentActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = WebService.invokeWebService(WebService.CREATE_APPOINTMENT_METHOD,
                    WebService.CREATE_APPOINTMENT_PARAMETER, appointmentDetails);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            progressDialog.dismiss();
            Toast toast = null;
            try {
                if (response == null || response.getString("result").compareToIgnoreCase("error") == 0) {
                    toast = Toast.makeText(CreateAppointmentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    if (response.getString("result").compareToIgnoreCase("false") == 0) {
                        toast = Toast.makeText(CreateAppointmentActivity.this, "This appointment clashes with an existing appointment", Toast.LENGTH_LONG);
                    } else {
                        toast = Toast.makeText(CreateAppointmentActivity.this, "Appointment created", Toast.LENGTH_LONG);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                toast = Toast.makeText(CreateAppointmentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
