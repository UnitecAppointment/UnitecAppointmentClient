package unitec.ac.nz.unitecappointmentclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        JSONObject loginDetails = new JSONObject();
        try {
            loginDetails.put("username", txtUsername.getText().toString());
            loginDetails.put("password", txtPassword.getText().toString());
            new AsyncLoginTask(loginDetails).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AsyncLoginTask extends AsyncTask<Void, Void, Void> {

        private JSONObject userLoginDetails;
        private ProgressDialog progressDialog;
        private JSONObject response;

        public AsyncLoginTask(JSONObject userLoginDetails) {
            this.userLoginDetails = userLoginDetails;
            progressDialog = new ProgressDialog(LoginActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = WebService.invokeWebService(WebService.LOGIN_METHOD, WebService.LOGIN_PARAMETER, userLoginDetails);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            progressDialog.dismiss();
            Toast toast = null;

            try {
                if (response == null || response.getString("result").compareToIgnoreCase("error") == 0) {
                    toast = Toast.makeText(LoginActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    if (response.getString("result").compareToIgnoreCase("false") == 0) {
                        toast = Toast.makeText(LoginActivity.this, "Invalid login details", Toast.LENGTH_LONG);
                    } else {
                        String user = response.getString("user");
                        String username = userLoginDetails.getString("username");
                        String firstName = response.getString("firstName");
                        String lastName = response.getString("lastName");
                        if (user.compareToIgnoreCase("lecturer") == 0) {
                            Intent intent = new Intent(LoginActivity.this, MainLecturerActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            intent.putExtra("title", response.getString("title"));
                            intent.putExtra("department", response.getString("department"));
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainStudentActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            startActivity(intent);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                toast = Toast.makeText(LoginActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
