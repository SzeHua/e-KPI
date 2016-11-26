package com.example.user.e_kpiv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by USER on 11/23/2016.
 */

public class SpinnerDownloader extends AsyncTask<Void, Void, String>{

    public static final String PREF_NAME = "PrefKey";
    Context c;
    String urlAddress;
    Spinner sp;
    ProgressDialog pd;

    public SpinnerDownloader(Context c, String urlAddress, Spinner sp){
        this.c = c;
        this.urlAddress = urlAddress;
        this.sp = sp;
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        pd= new ProgressDialog(c);
        pd.setTitle("In Progress");
        pd.setMessage("In progress...Please wait");
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        return this.downloadData();
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
        pd.dismiss();

        if(s==null){
            Toast.makeText(c, "Unable to Retrieve, null Returned", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(c, "Success", Toast.LENGTH_SHORT).show();

            //CALL PARSES CLASS TO PARSE
            SharedPreferences preferences = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            int categoryID = preferences.getInt("categoryID", 0);
            String response;
            if(categoryID == 0) {
                SpinnerDataParser parser = new SpinnerDataParser(c, sp, s);
                parser.execute();
            }else
            {
                SpinnerDataParserKPI parserKPI = new SpinnerDataParserKPI(c,sp,s);
                parserKPI.execute();
            }
        }
    }

    private String downloadData(){
        HttpURLConnection httpURLConnection = Connector.connect(urlAddress);

        if(httpURLConnection == null){
            return null;
        }

        InputStream inputStream = null;
        try{
            inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            StringBuffer response = new StringBuffer();

            if(bufferedReader != null){
                while((line = bufferedReader.readLine()) != null){
                    response.append(line+"\n");
                }
                bufferedReader.close();
            }else{
                return null;
            }
            return response.toString();
            }catch (IOException e){
            e.printStackTrace();
            }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

