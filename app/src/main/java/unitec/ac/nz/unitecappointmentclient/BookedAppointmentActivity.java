package unitec.ac.nz.unitecappointmentclient;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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

public class BookedAppointmentActivity extends AppCompatActivity implements OnClickListener,
        OnItemClickListener {

    private ListView lstBookedAppointments;
    private Button btnMenu;
    private String username;
    private String firstName;
    private String lastName;
    private String title;
    private String department;
    private String type;
    private List<BookedAppointmentModel> bookedAppointments;
    private int position;
    private BookedAppointmentAdapter bookedAppointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_appointments);

        Intent intent = getIntent();
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        username = intent.getStringExtra("username");
        type = intent.getStringExtra("type");
        if (type.compareTo("lecturer") == 0) {
            title = intent.getStringExtra("title");
            department = intent.getStringExtra("department");
        }
        bookedAppointments = (ArrayList<BookedAppointmentModel>) intent.getSerializableExtra("bookedAppointments");
        bookedAppointmentAdapter = new BookedAppointmentAdapter(this, bookedAppointments);
        lstBookedAppointments = (ListView) findViewById(R.id.lstBookedAppointments);
        lstBookedAppointments.setAdapter(bookedAppointmentAdapter);
        lstBookedAppointments.setOnItemClickListener(this);
        btnMenu = (Button) findViewById(R.id.btnMenu1);
        btnMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnMenu) {
            if (type.compareTo("student") == 0) {
                Intent intent = new Intent(BookedAppointmentActivity.this, MainStudentActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("type", "student");
                startActivity(intent);
            } else {
                Intent intent = new Intent(BookedAppointmentActivity.this, MainLecturerActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("type", "lecturer");
                intent.putExtra("title", title);
                intent.putExtra("department", department);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        this.position = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (bookedAppointments.get(position).getStatus().compareToIgnoreCase("cancelled") != 0) {
                    // User clicked OK button
                    JSONObject appointmentDetails = new JSONObject();
                    try {
                        appointmentDetails.put("username", bookedAppointments.get(position).getUsername());
                        appointmentDetails.put("date", bookedAppointments.get(position).getDate());
                        appointmentDetails.put("start", bookedAppointments.get(position).getStart());
                        appointmentDetails.put("end", bookedAppointments.get(position).getEnd());
                        appointmentDetails.put("firstName", firstName);
                        appointmentDetails.put("lastName", lastName);
                        appointmentDetails.put("type", type);
                        new AsyncCancelAppointmentTask(appointmentDetails).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast toast = Toast.makeText(BookedAppointmentActivity.this, "Cannot cancel appointment", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class AsyncCancelAppointmentTask extends AsyncTask<Void, Void, Void> {

        private JSONObject appointmentDetails;
        private ProgressDialog progressDialog;
        private JSONObject response;

        public AsyncCancelAppointmentTask(JSONObject appointmentDetails) {
            this.appointmentDetails = appointmentDetails;
            progressDialog = new ProgressDialog(BookedAppointmentActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = WebService.invokeWebService(WebService.CANCEL_APPOINTMENT_METHOD,
                    WebService.CANCEL_APPOINTMENT_PARAMETER, appointmentDetails);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            progressDialog.dismiss();
            Toast toast = null;

            try {
                if (response == null || response.getString("result").compareToIgnoreCase("error") == 0) {
                    toast = Toast.makeText(BookedAppointmentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    if (response.getString("result").compareToIgnoreCase("true") == 0) {
                        bookedAppointments.remove(position);
                        bookedAppointmentAdapter.notifyDataSetChanged();

                        if (bookedAppointments.isEmpty()) {
                            Intent intent = new Intent(BookedAppointmentActivity.this, MainStudentActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            startActivity(intent);
                        }
                    } else {
                        toast = Toast.makeText(BookedAppointmentActivity.this, "No Assigned Lecturers", Toast.LENGTH_LONG);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                toast = Toast.makeText(BookedAppointmentActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
