package com.example.user.e_kpiv3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText EmailEt, PasswordEt;
    String email, password;
    String staffID = "";
    private int isLecturer = 1;
    private SharedPreferences preferences;
    static final String PREF_NAME = "PrefKey";
    public static final String KEY_STAFFID = "staffID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EmailEt = (EditText)findViewById(R.id.etEmail);
        PasswordEt = (EditText) findViewById(R.id.etPassword);
        preferences = getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }
    public void OnLogin(View view){
        email = EmailEt.getText().toString();
        password = PasswordEt.getText().toString();
        String type = "login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, email, password);
    }
    public void OnRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
    public class BackgroundWorker extends AsyncTask<String, Void, String> {
        Context context;
        AlertDialog alertDialog;

        BackgroundWorker(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String result = "";
            String login_url = "http://192.168.173.1/e-KPI/php/Login.php";

            if (type.equals("login")) {
                try {
                    String email = params[1];
                    String password = params[2];
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    //String result="";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            //alertDialog = new AlertDialog.Builder(context).create();
            //alertDialog.setTitle("Login Status");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            //alertDialog.setMessage(result);
            //alertDialog.show();
            if (result.equals("")) {
                result = "Login unsuccessful.";
            } else {
                staffID = result;
                getSecondaryPosition();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("staffid", result);
                editor.commit();


                Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
                intent.putExtra("result", result);
                LoginActivity.this.startActivity(intent);
            }
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }

        private void getSecondaryPosition()
            {

            // Instantiate the RequestQueue.
            String secondaryPosition_url = "http://192.168.173.1/e-KPI/php/GetSecondaryPosition.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, secondaryPosition_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            if (!(s.equals(""))) {
                                isLecturer = 1;
                            } else {
                                isLecturer = 0;
                                //Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                            }
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("secondaryPosition", s);
                            editor.putInt("isLecturer", isLecturer);
                            editor.commit();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Toast.makeText(LoginActivity.this, s , Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    //Creating parameters
                    Map<String, String> params = new Hashtable<String, String>();

                    //Adding parameters
                    params.put(KEY_STAFFID, staffID);
                    //returning parameters
                    return params;
                }
            };

            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            //Adding request to the queue
            requestQueue.add(stringRequest);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}