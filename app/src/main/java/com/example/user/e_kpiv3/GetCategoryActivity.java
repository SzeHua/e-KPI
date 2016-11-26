package com.example.user.e_kpiv3;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.List;

public class GetCategoryActivity extends AppCompatActivity{

    private Spinner sCategory;
    private String categoryName;
    private static final String URL_CATEGORY = "http://192.168.173.1/e-KPI/php/GetCategory.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_category);

        final Spinner sCategory = (Spinner) findViewById(R.id.sCategory);
        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                //int index = parent.getSelectedItemPosition();
                categoryName = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

            new SpinnerDownloader(GetCategoryActivity.this, URL_CATEGORY, sCategory).execute();


    }

    }

