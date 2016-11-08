package unitec.ac.nz.unitecappointmentclient;

import android.util.Log;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * Class for making web service calls and obtaining results to and from web-server
 *
 * @author      Marzouq Almarzooq (1380949)
 * @author      Nawaf Altuwayjiri (1377387)
 */
public class WebService {

    //services available form web server
    public static String LOGIN_METHOD = "login";
    public static String LOGIN_PARAMETER = "userLoginDetails";
    public static String CREATE_APPOINTMENT_METHOD = "createAppointment";
    public static String CREATE_APPOINTMENT_PARAMETER = "appointmentDetails";
    public static String MAKE_APPOINTMENT_METHOD = "makeAppointment";
    public static String MAKE_APPOINTMENT_PARAMETER = "userDetails";
    public static String GET_AVAILABLE_APPOINTMENT_METHOD = "getAvailableAppointment";
    public static String GET_AVAILABLE_APPOINTMENT_PARAMETER = "userDetails";
    public static String BOOK_APPOINTMENT_METHOD = "bookAppointment";
    public static String BOOK_APPOINTMENT_PARAMETER = "appointmentDetails";
    public static String VIEW_APPOINTMENT_METHOD = "viewAppointment";
    public static String VIEW_APPOINTMENT_PARAMETER = "userDetails";
    public static String CANCEL_APPOINTMENT_METHOD = "cancelAppointment";
    public static String CANCEL_APPOINTMENT_PARAMETER = "appointmentDetails";

    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = "http://unitecappointmentserver/";
    //Webservice URL - WSDL File location
    private static String URL = "https://192.168.1.3:8181/Unitec_Appointment_Server/AppointementServices?WSDL";
    //SOAP Action URI again Namespace + Web method name
    private static String SOAP_ACTION = "http://unitecappointmentserver/AppointementServices/";

    /**
     * Create and initialise the activity view
     *
     * @param methodName           web service name
     * @param parameterName        web service parameter name
     * @param data                 parameter data
     */
    public static JSONObject invokeWebService(String methodName, String parameterName, JSONObject data) {

        JSONObject result = null;

        try {
            // Create request
            SoapObject request = new SoapObject(NAMESPACE, methodName);
            // Property which holds input parameters
            PropertyInfo propertyInfo = new PropertyInfo();
            // Set Name
            propertyInfo.setName(parameterName);
            // Set Value
            propertyInfo.setValue(data.toString());
            // Set dataType
            propertyInfo.setType(String.class);
            // Add the property to request object
            request.addProperty(propertyInfo);
            // Create envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            // Set output SOAP object
            envelope.setOutputSoapObject(request);

            // Create HTTP call object
            SSLConection.allowAllSSL();
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

            // Invoke web service
            httpTransportSE.call(SOAP_ACTION + methodName, envelope);
            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            // Assign it to result variable static variable
            result = new JSONObject(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        //Return result to calling object
        return result;
    }

    /**
     * private class for application certificate trust manager, defined to trust all certificates
     *
     * @author      Marzouq Almarzooq (1380949)
     * @author      Nawaf Altuwayjiri (1377387)
     */
    private static class SSLConection {

        private static TrustManager[] trustManagers;

        public static class TrustManager implements X509TrustManager {


            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }


        }

        /**
         * Allows self-signed certifcates to be accepted
         */
        public static void allowAllSSL() {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context;
            if (trustManagers == null) {
                trustManagers = new TrustManager[]{new TrustManager()};
            }
            try {
                context = SSLContext.getInstance("TLS");
                context.init(null, trustManagers, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                Log.e("allowAllSSL", e.toString());
            } catch (KeyManagementException e) {
                Log.e("allowAllSSL", e.toString());
            }
        }
    }
}

