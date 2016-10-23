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

public class MainStudentActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lblWelcome;
    private Button btnMakeAppointment;
    private Button btnViewAppointment;
    private Button btnLogout;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);
        lblWelcome = (TextView) findViewById(R.id.lblWelcome);
        btnMakeAppointment = (Button) findViewById(R.id.btnMakeAppointment);
        btnMakeAppointment.setOnClickListener(this);
        btnViewAppointment = (Button) findViewById(R.id.btnViewAppointment);
        btnViewAppointment.setOnClickListener(this);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        Intent intent = getIntent();
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        username = intent.getStringExtra("username");
        lblWelcome.setText("Welcome " + firstName + " " + lastName);
    }

    @Override
    public void onClick(View v) {

        if (v == btnMakeAppointment) {
            JSONObject userDetails = new JSONObject();
            try {
                userDetails.put("username", username);
                new AsyncMakeAppointmentTask(userDetails).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (v == btnViewAppointment) {
            JSONObject userDetails = new JSONObject();
            try {
                userDetails.put("username", username);
                userDetails.put("type", "student");
                new AsyncViewAppointmentTask(userDetails).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (v == btnLogout) {
            Intent intent = new Intent(MainStudentActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private class AsyncViewAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject userDetails;
        private ProgressDialog progressDialog;
        private JSONObject response;

        public AsyncViewAppointmentTask(JSONObject userDetails) {
            this.userDetails = userDetails;
            progressDialog = new ProgressDialog(MainStudentActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = WebService.invokeWebService(WebService.VIEW_APPOINTMENT_METHOD,
                    WebService.VIEW_APPOINTMENT_PARAMETER, userDetails);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            progressDialog.dismiss();
            Toast toast = null;

            try {
                if (response == null || response.getString("result").compareToIgnoreCase("error") == 0) {
                    toast = Toast.makeText(MainStudentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
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

                        Intent intent = new Intent(MainStudentActivity.this, BookedAppointmentActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("type", "student");
                        intent.putExtra("bookedAppointments", (ArrayList<BookedAppointmentModel>)bookedAppointmentsList);
                        startActivity(intent);
                    } else {
                        toast = Toast.makeText(MainStudentActivity.this, "No Booked Appointments", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                toast = Toast.makeText(MainStudentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }

    private class AsyncMakeAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject userDetails;
        private ProgressDialog progressDialog;
        private JSONObject response;

        public AsyncMakeAppointmentTask(JSONObject userDetails) {
            this.userDetails = userDetails;
            progressDialog = new ProgressDialog(MainStudentActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = WebService.invokeWebService(WebService.MAKE_APPOINTMENT_METHOD,
                    WebService.MAKE_APPOINTMENT_PARAMETER, userDetails);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            progressDialog.dismiss();
            Toast toast = null;

            try {
                if (response == null || response.getString("result").compareToIgnoreCase("error") == 0) {
                    toast = Toast.makeText(MainStudentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
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
                e.printStackTrace();
                toast = Toast.makeText(MainStudentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
