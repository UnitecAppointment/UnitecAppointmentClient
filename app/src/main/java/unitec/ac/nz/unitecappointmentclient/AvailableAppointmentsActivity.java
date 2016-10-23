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

public class AvailableAppointmentsActivity extends AppCompatActivity implements OnClickListener,
        OnItemClickListener {

    private ListView lstAppointments;
    private Button btnLecturers;
    private String username;
    private String firstName;
    private String lastName;
    private String lecturerUsername;
    private List<LecturerModel> lecturers;
    private List<AppointmentModel> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_appointments);

        Intent intent = getIntent();
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        username = intent.getStringExtra("username");
        lecturers = (ArrayList<LecturerModel>)intent.getSerializableExtra("lecturers");
        lecturerUsername = intent.getStringExtra("lecturerUsername");
        appointments = (ArrayList<AppointmentModel>)intent.getSerializableExtra("appointments");
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(this, appointments);
        lstAppointments = (ListView) findViewById(R.id.lstAppointments);
        lstAppointments.setAdapter(appointmentAdapter);
        lstAppointments.setOnItemClickListener(this);
        btnLecturers = (Button) findViewById(R.id.btnLecturers);
        btnLecturers.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLecturers) {
            Intent intent = new Intent(AvailableAppointmentsActivity.this, StudentLecturerActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("lecturers", (ArrayList<LecturerModel>)lecturers);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            JSONObject appointmentDetails = new JSONObject();
            AppointmentModel appointment = (AppointmentModel) lstAppointments.getItemAtPosition(position);
            appointmentDetails.put("studentUsername", username);
            appointmentDetails.put("lecturerUsername", lecturerUsername);
            appointmentDetails.put("date", appointment.getDate());
            appointmentDetails.put("start", appointment.getStart());
            appointmentDetails.put("end", appointment.getEnd());
            new AsyncBookAppointmentTask(appointmentDetails).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AsyncBookAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject appointmentDetails;
        private ProgressDialog progressDialog;
        private JSONObject response;

        public AsyncBookAppointmentTask(JSONObject appointmentDetails) {
            this.appointmentDetails = appointmentDetails;
            progressDialog = new ProgressDialog(AvailableAppointmentsActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = WebService.invokeWebService(WebService.BOOK_APPOINTMENT_METHOD,
                    WebService.BOOK_APPOINTMENT_PARAMETER, appointmentDetails);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            progressDialog.dismiss();
            Toast toast = null;

            try {
                if (response == null || response.getString("result").compareToIgnoreCase("error") == 0) {
                    toast = Toast.makeText(AvailableAppointmentsActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
                        Intent intent = new Intent(AvailableAppointmentsActivity.this, StudentLecturerActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("lecturers", (ArrayList<LecturerModel>)lecturers);
                        startActivity(intent);
                    } else {
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
