package com.example.user.e_kpiv3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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

public class RegisterActivity extends AppCompatActivity {
    EditText email, staffID, password, confirmPassword;
    String str_email, str_staffID, str_password, str_confirmPassword;
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (EditText) findViewById(R.id.etEmail);
        staffID = (EditText) findViewById(R.id.etStaffID);
        password = (EditText) findViewById(R.id.etPassword);
        confirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
    }
    public void OnRegister(View view) {
        str_email = email.getText().toString();
        str_staffID = staffID.getText().toString();
        str_password = password.getText().toString();
        str_confirmPassword = confirmPassword.getText().toString();
        if(!str_confirmPassword.equals(str_password)){
            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Your password is not match with your confirm password.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.show();
        }else {
            BackgroundWorker backgroundWorker = new BackgroundWorker();
            backgroundWorker.execute(str_email, str_staffID, str_password, str_confirmPassword);
        }
    }

    public class BackgroundWorker extends AsyncTask<String, String, String> {
        Context context;
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String staffID = params[1];
            String password = params[2];
            String result = "";
            int line;
            String register_url = "http://192.168.173.1/e-KPI/php/Register.php";
            try {
                URL url = new URL(register_url);
                String urlParams = "email=" + email + "&staffID=" + staffID + "&password=" + password;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(urlParams.getBytes());
                outputStream.flush();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                while ((line = inputStream.read()) != -1) {
                    result += (char) line;
                }
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("")) {
                result = "Data saved successfully.";
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
            } else {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            }
        }
    }
}
