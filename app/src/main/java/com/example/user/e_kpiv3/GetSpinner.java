package com.example.user.e_kpiv3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetSpinner extends AppCompatActivity {

    private Spinner sCategory;
    private Spinner sKPI;
    private Spinner sMeasures;
    private SharedPreferences preferences;
    public static final String PREF_NAME = "PrefKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_spinner);

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sCategory = (Spinner) findViewById(R.id.sCategory);
        sKPI = (Spinner) findViewById(R.id.sKPI);
        sMeasures = (Spinner) findViewById(R.id.sMeasures);

        //category
        final List<String> category = new ArrayList<String>();
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, category);
        sCategory.setAdapter(adapterCategory);

        //kpi
        final List<String> kpi = new ArrayList<String>();

        sCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }

            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        ViewCategoryList viewCategoryList = new ViewCategoryList();
        viewCategoryList.execute();

    }
    public class ViewCategoryList extends AsyncTask<String, Void, String>   {
        Context context;

        @Override
        protected String doInBackground(String... params) {
            String category ="";
            String getCategory = "http://192.168.173.1/e-KPI/php/GetSpinner.php";
            URL url = null;
            try {
                url = new URL(getCategory);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return category;
        }

    }
}
