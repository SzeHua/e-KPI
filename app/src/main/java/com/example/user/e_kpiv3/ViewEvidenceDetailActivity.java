package com.example.user.e_kpiv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ViewEvidenceDetailActivity extends AppCompatActivity {
    private List<Evidence> evidenceList = new ArrayList<>();
    public static final String PREF_NAME = "PrefKey";
    public static final String DELETE_URL = "http://192.168.173.1/e-KPI/php/DeleteEvidence.php";
    public static final String KEY_EVIDENCEID = "evidenceID";

    private SharedPreferences preference;
    private ImageView image;
    private String evidenceID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_evidence_detail);

        TextView title = (TextView)findViewById(R.id.tvTitleDB);
        TextView description = (TextView)findViewById(R.id.tvDescriptionDB);
        TextView categoryName = (TextView)findViewById(R.id.tvCategoryDB);
        TextView kpiName = (TextView)findViewById(R.id.tvKPIDB);
        TextView measureName = (TextView)findViewById(R.id.tvMeasuresDB);
        TextView date = (TextView)findViewById(R.id.tvDateDB);
        image = (ImageView)findViewById(R.id.ivViewImage);
        Button bLogout = (Button) findViewById(R.id.bLogout);
        Button bEdit = (Button) findViewById(R.id.bEdit);
        Button bDelete = (Button) findViewById(R.id.bDelete);
        Button bHome = (Button) findViewById(R.id.bHome);

        preference = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        title.setText(preference.getString("title", ""));
        description.setText(preference.getString("description", ""));
        date.setText(preference.getString("date", "0000-00-00"));
        categoryName.setText(preference.getString("categoryName", ""));
        kpiName.setText(preference.getString("kpiName", ""));
        measureName.setText(preference.getString("measureName", ""));

        evidenceID = preference.getString("evidenceid", "");
        getImage(preference.getString("evidenceid", ""));

        bHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homepage();
            }
        });

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }

        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteEvidence();
            }

        });
    }

    private void homepage(){

        Intent intent = new Intent (ViewEvidenceDetailActivity.this, HomepageActivity.class);
        ViewEvidenceDetailActivity.this.startActivity(intent);
    }

    private void logout(){
        preference = getSharedPreferences(ViewEvidenceDetailActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent (ViewEvidenceDetailActivity.this, LoginActivity.class);
        ViewEvidenceDetailActivity.this.startActivity(intent);
    }

    private void delete(){

       /* class DeleteEvidence extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewEvidenceDetailActivity.this, "Updating...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ViewEvidenceDetailActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                //String id = params[0];
                String del = "http://192.168.173.1/e-KPI/php/DeleteEvidence.php?id="+evidenceID;
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(del);
                    //image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return del;
            }
        }

        DeleteEvidence de = new DeleteEvidence();
        de.execute(); */

            //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Deleting...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        if (s.equals("")) {
                            Toast.makeText(ViewEvidenceDetailActivity.this, "Evidence has been deleted.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent (ViewEvidenceDetailActivity.this, EvidenceListActivity.class);
                            ViewEvidenceDetailActivity.this.startActivity(intent);

                        } else {
                            Toast.makeText(ViewEvidenceDetailActivity.this, s, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(ViewEvidenceDetailActivity.this, null , Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_EVIDENCEID, evidenceID);
                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    private void confirmDeleteEvidence(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to delete this evidence?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        delete();
                        Intent intent = new Intent (ViewEvidenceDetailActivity.this, EvidenceListActivity.class);
                        ViewEvidenceDetailActivity.this.startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void edit(){

        Intent intent = new Intent(this, EditEvidenceActivity.class);
        ViewEvidenceDetailActivity.this.startActivity(intent);
    }

    private void getImage(String id){
        GetImage gi = new GetImage();
        gi.execute(id);
    }

    public class GetImage extends AsyncTask<String,Void,Bitmap> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ViewEvidenceDetailActivity.this, "Loading...", null,true,true);
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            super.onPostExecute(b);
            loading.dismiss();
            image.setImageBitmap(b);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String id = params[0];
            String add = "http://192.168.173.1/e-KPI/php/LoadImage.php?id="+id;
            URL url = null;
            Bitmap image = null;
            try {
                url = new URL(add);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }
    }
}
