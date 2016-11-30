package com.example.user.e_kpiv3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Hashtable;
import java.util.Map;

public class HomepageActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    static final String PREF_NAME = "PrefKey";
    private String secondaryPosition = "";
    private int isLecturer = 1;
    private String roleType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        preferences = getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        secondaryPosition = preferences.getString("secondaryPosition", "");

        //access to Switch Roles context menu
        Button bSwitchRole = (Button) findViewById(R.id.bSwitchRole);

        //Register for context menu
        this.registerForContextMenu(bSwitchRole);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId()==R.id.bSwitchRole){
            this.getMenuInflater().inflate(R.menu.role_context_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
        setTitle(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        preferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        switch (selectedItemId){
            case R.id.miSecondaryPosition:
                item.setChecked(true);
                Toast.makeText(HomepageActivity.this, secondaryPosition+" is selected.", Toast.LENGTH_LONG).show();
                isLecturer = 0;
                if(secondaryPosition.contains("Dean") || secondaryPosition.equals("Administrator")){
                    roleType = "Faculty";
                }else if(secondaryPosition.contains("Department")){
                    roleType = "Departmental";
                }
                editor.putString("roleType", roleType);
                editor.commit();
                break;
            case R.id.miLecturer:
                item.setChecked(true);
                Toast.makeText(HomepageActivity.this, "Lecturer is selected.", Toast.LENGTH_LONG).show();
                isLecturer = 1;
                roleType = "Individual";
                editor.putString("roleType", roleType);
                editor.commit();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void setTitle(Menu menu) {
    if(!secondaryPosition.equals("")) {
        menu.findItem(R.id.miSecondaryPosition).setTitle(secondaryPosition);
    }
        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            invalidateOptionsMenu();
        }*/

    }

    public void OnUpload (View view){
        Intent intent = new Intent(HomepageActivity.this, ImageUploadActivity.class);
        HomepageActivity.this.startActivity(intent);
    }

    public void OnView (View view){
        Intent intent = new Intent(this, EvidenceListActivity.class);
        HomepageActivity.this.startActivity(intent);
    }

    public void OnLogout (View view){
        preferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent (HomepageActivity.this, LoginActivity.class);
        HomepageActivity.this.startActivity(intent);
    }

    /*private void getSecondaryPosition()
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
    }*/
}
