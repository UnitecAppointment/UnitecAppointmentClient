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

/**
 * login activity, used by students and lecturers
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtUsername;       //login username
    private EditText txtPassword;       //user password
    private Button btnLogin;            //button to submit login details

    /**
     * Create and initialise the login activity view
     *
     * @param savedInstanceState           any state information persisted from activity's previous life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call ancestor constructor for proper initialisation, assign the respective layout for
        // this activity's view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //obtain references to activity sub-views
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // attach listener to handle button selection touch events
        btnLogin.setOnClickListener(this);
    }

    /**
     * Call back handler for events arising from touch click event listener
     *
     * @param v           view responsible for raising the touch click evevnt
     */
    @Override
    public void onClick(View v) {
        // create JSON object with user login details
        JSONObject loginDetails = new JSONObject();
        try {
            loginDetails.put("username", txtUsername.getText().toString());
            loginDetails.put("password", txtPassword.getText().toString());
            // pass Json object to asynchronous task thread responsible for web service call
            new AsyncLoginTask(loginDetails).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Private helper class to handle asynchronous web service call for logging in
     *
     * @author      Marzouq Almarzooq (1380949)
     * @author      Nawaf Altuwayjiri (1377387)
     */
    private class AsyncLoginTask extends AsyncTask<Void, Void, Void> {

        private JSONObject userLoginDetails;        // Json object with login details
        private ProgressDialog progressDialog;      // dialog for progress of web service call
        private JSONObject response;                // web service response Json object

        /**
         * Constructor for asynchronous task
         *
         * @param userLoginDetails           Json object with user login details
         */
        public AsyncLoginTask(JSONObject userLoginDetails) {
            // initialise task object attributes
            this.userLoginDetails = userLoginDetails;
            progressDialog = new ProgressDialog(LoginActivity.this);
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
            response = WebService.invokeWebService(WebService.LOGIN_METHOD, WebService.LOGIN_PARAMETER, userLoginDetails);
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
                    toast = Toast.makeText(LoginActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
                } else {
                    // successful response result
                    if (response.getString("result").compareToIgnoreCase("false") == 0) {
                        //invalid login
                        toast = Toast.makeText(LoginActivity.this, "Invalid login details", Toast.LENGTH_LONG);
                    } else {
                        //valid login
                        String user = response.getString("user");
                        String username = userLoginDetails.getString("username");
                        String firstName = response.getString("firstName");
                        String lastName = response.getString("lastName");
                        if (user.compareToIgnoreCase("lecturer") == 0) {
                            //lecturer user valid login
                            Intent intent = new Intent(LoginActivity.this, MainLecturerActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            intent.putExtra("title", response.getString("title"));
                            intent.putExtra("department", response.getString("department"));
                            startActivity(intent);
                        } else {
                            //student user valid login
                            Intent intent = new Intent(LoginActivity.this, MainStudentActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("lastName", lastName);
                            startActivity(intent);
                        }
                    }
                }
            } catch (JSONException e) {
                //invalid json response
                e.printStackTrace();
                toast = Toast.makeText(LoginActivity.this, "Problem communicating with server", Toast.LENGTH_LONG);
            }
            if (toast != null) {
                toast.show();
            }
        }
    }
}
