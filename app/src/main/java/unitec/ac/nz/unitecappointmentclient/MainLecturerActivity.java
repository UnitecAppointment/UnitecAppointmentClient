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

public class MainLecturerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lblLogin;
    private TextView lblName;
    private TextView lblDepartment;
    private Button btnCreateAppointment;
    private Button btnViewAppointment;
    private Button btnLogout;
    private String username;
    private String firstName;
    private String lastName;
    private String title;
    private String department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lecturer);

        lblLogin = (TextView) findViewById(R.id.lblLogin);
        lblName = (TextView) findViewById(R.id.lblName);
        lblDepartment = (TextView) findViewById(R.id.lblDepartment);

        btnCreateAppointment = (Button) findViewById(R.id.btnCreateAppointment);
        btnCreateAppointment.setOnClickListener(this);
        btnViewAppointment = (Button) findViewById(R.id.btnViewAppointment);
        btnViewAppointment.setOnClickListener(this);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        title = intent.getStringExtra("title");
        department = intent.getStringExtra("department");

        lblLogin.setText("Logged in as: " + username);
        lblName.setText(title + " " + firstName + " " + lastName);
        lblDepartment.setText(department);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCreateAppointment) {
            Intent intent = new Intent(MainLecturerActivity.this, CreateAppointmentActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("username", username);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("title", title);
            intent.putExtra("department", department);
            startActivity(intent);
        } else if (v == btnViewAppointment){
            JSONObject userDetails = new JSONObject();
            try {
                userDetails.put("username", username);
                userDetails.put("type", "lecturer");
                new AsyncViewAppointmentTask(userDetails).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(MainLecturerActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private class AsyncViewAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject userDetails;
        private ProgressDialog progressDialog;
        private JSONObject response;

        public AsyncViewAppointmentTask(JSONObject userDetails) {
            this.userDetails = userDetails;
            progressDialog = new ProgressDialog(MainLecturerActivity.this);
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
                    toast = Toast.makeText(MainLecturerActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
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
                        toast = Toast.makeText(MainLecturerActivity.this, "No Booked Appointments", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                toast = Toast.makeText(MainLecturerActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
