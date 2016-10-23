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

public class StudentLecturerActivity extends AppCompatActivity implements OnClickListener,
        OnItemClickListener {

    private ListView lstLecturers;
    private Button btnMenu;
    private String username;
    private String firstName;
    private String lastName;
    private List<LecturerModel> lecturers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lecturer);

        Intent intent = getIntent();
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        username = intent.getStringExtra("username");
        lecturers = (ArrayList<LecturerModel>)intent.getSerializableExtra("lecturers");
        LecturerAdapter lecturerAdapter = new LecturerAdapter(this, lecturers);
        lstLecturers = (ListView) findViewById(R.id.lstLecturers);
        lstLecturers.setAdapter(lecturerAdapter);
        lstLecturers.setOnItemClickListener(this);
        btnMenu = (Button) findViewById(R.id.btnMenu1);
        btnMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnMenu) {
            Intent intent = new Intent(StudentLecturerActivity.this, MainStudentActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            JSONObject userDetails = new JSONObject();
            LecturerModel lecturer = (LecturerModel) lstLecturers.getItemAtPosition(position);
            userDetails.put("username", lecturer.getUsername());
            new AsyncAvailableAppointmentTask(userDetails).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AsyncAvailableAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject userDetails;
        private ProgressDialog progressDialog;
        private JSONObject response;

        public AsyncAvailableAppointmentTask(JSONObject userDetails) {
            this.userDetails = userDetails;
            progressDialog = new ProgressDialog(StudentLecturerActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = WebService.invokeWebService(WebService.GET_AVAILABLE_APPOINTMENT_METHOD,
                    WebService.GET_AVAILABLE_APPOINTMENT_PARAMETER, userDetails);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            progressDialog.dismiss();
            Toast toast = null;

            try {
                if (response == null || response.getString("result").compareToIgnoreCase("error") == 0) {
                    toast = Toast.makeText(StudentLecturerActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
                        JSONArray appointments = response.getJSONArray("appointments");
                        List<AppointmentModel> appointmentsList = new ArrayList<>();
                        for (int index = 0; index < appointments.length(); index++) {
                            JSONObject appointment = appointments.getJSONObject(index);
                            AppointmentModel appointmentItem = new AppointmentModel(appointment.getString("date"),
                                    appointment.getString("start"), appointment.getString("end"));
                            appointmentsList.add(appointmentItem);
                        }

                        Intent intent = new Intent(StudentLecturerActivity.this, AvailableAppointmentsActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("lecturers", (ArrayList<LecturerModel>)lecturers);
                        intent.putExtra("lecturerUsername", userDetails.getString("username"));
                        intent.putExtra("appointments", (ArrayList<AppointmentModel>)appointmentsList);
                        startActivity(intent);
                    } else {
                        toast = Toast.makeText(StudentLecturerActivity.this, "No Available Appointments", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                toast = Toast.makeText(StudentLecturerActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
