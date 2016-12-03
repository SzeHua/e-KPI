package com.example.user.e_kpiv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by USER on 11/27/2016.
 */

public class SpinnerDataParserMeasures extends AsyncTask<Void, Void, Integer> {

    Context c;
    Spinner sp;
    String jsonData;
    ProgressDialog pd;
    ArrayList<String> measures = new ArrayList<>();

    JSONArray jsonArray;
    JSONObject jsonObject;

    public SpinnerDataParserMeasures(Context c, Spinner sp, String jsonData) {
        this.c = c;
        this.sp = sp;
        this.jsonData = jsonData;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return this.parseData();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        /*pd= new ProgressDialog(c);
        pd.setTitle("In Progress");
        pd.setMessage("In progress...Please wait");
        pd.show();*/
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        //pd.dismiss();

        if(result == 0)
        {
            //Toast.makeText(c, "Unable to parse", Toast.LENGTH_SHORT).show();
        }else
        {
            //Toast.makeText(c, "Parsed Successfully", Toast.LENGTH_SHORT).show();

            //BIND
            ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_list_item_1, measures);
            sp.setAdapter(adapter);

        }
    }

    private int parseData()
    {
        try {
            jsonArray = new JSONArray(jsonData);
            jsonObject = null;

            measures.clear();
            SpinnerObjCat cat3 = null;

            for(int i=0; i<jsonArray.length(); i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                int measuresID = jsonObject.getInt("measuresID");
                String measuresName = jsonObject.getString("measuresName");
                measures.add(measuresName);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
