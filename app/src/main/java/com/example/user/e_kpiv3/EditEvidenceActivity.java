package com.example.user.e_kpiv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

public class EditEvidenceActivity extends AppCompatActivity {

    private SharedPreferences preference;
    public static final String PREF_NAME = "PrefKey";
    public static final String UPDATE_URL = "http://192.168.173.1/e-KPI/php/EditEvidence.php";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY = "categoryName";
    public static final String KEY_KPI = "kpiName";
    public static final String KEY_MEASURES = "measuresName";
    public static final String KEY_STAFFID = "staffID";
    public static final String KEY_EVIDENCEID = "evidenceID";
    public static final String KEY_ROLE_TYPE = "roleType";

    private int PICK_IMAGE_REQUEST = 1;
    private Spinner sCategory;
    private Spinner sKPI;
    private Spinner sMeasures;
    private String categoryName = "";
    private int categoryID = 0;
    private String kpiName = "";
    private int kpiID = 0;
    private String measuresName = "";
    private EditText etTitle;
    private EditText etDescription;
    private Button bChooseFile;
    private Button bSave;
    private Button bCancel;
    private Button bLogout;
    private Button bAction;
    private String staffID = "";
    private int isLecturer;
    private ImageView imageView;
    private String evidenceID = "";
    private String roleType = "";

    private Bitmap bitmap;
    private Bitmap resized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_evidence);
        preference = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etDescription = (EditText) findViewById(R.id.etDescription);
        sCategory = (Spinner) findViewById(R.id.sCategory);
        sKPI = (Spinner) findViewById(R.id.sKPI);
        sMeasures = (Spinner)findViewById(R.id.sMeasures);
        imageView = (ImageView) findViewById(R.id.imageView);
        //DateTime date = (DateTime) findViewById(R.id.tvDateDB);
        bLogout = (Button) findViewById(R.id.bLogout);
        bChooseFile = (Button) findViewById(R.id.bChooseFile);
        bSave = (Button) findViewById(R.id.bSave);
        bCancel = (Button) findViewById(R.id.bCancel);
        bAction = (Button) findViewById(R.id.bAction);
        isLecturer = preference.getInt("isLecturer", 1);
        etTitle.setText(preference.getString("title", ""));
        etDescription.setText(preference.getString("description", ""));
        staffID = preference.getString("staffid","");
        evidenceID = preference.getString("evidenceid", "");
        roleType = preference.getString("roleType", roleType);
        getImage(preference.getString("evidenceid", ""));
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt("categoryID", 0);
        editor.putInt("kpiID", 0);
        editor.commit();
        String URL_CATEGORY = "http://192.168.173.1/e-KPI/php/GetCategory.php?isLecturer="+isLecturer;


        bChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        new SpinnerDownloader(EditEvidenceActivity.this, URL_CATEGORY, sCategory).execute();
        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                categoryName = parent.getItemAtPosition(position).toString();
                categoryID = position+1;

                SharedPreferences.Editor editor = preference.edit();
                editor.putInt("categoryID", categoryID);
                editor.commit();
                KPISpinner(categoryID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void KPISpinner(int categoryID)
    {
        String URL_KPI = "http://192.168.173.1/e-KPI/php/GetKPI_Spinner.php?categoryID="+categoryID;
        new SpinnerDownloader(EditEvidenceActivity.this, URL_KPI, sKPI).execute();
        sKPI.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                kpiName = parent.getItemAtPosition(position).toString();
                kpiID = position+1;

                SharedPreferences.Editor editor = preference.edit();
                editor.putInt("kpiID", kpiID);
                editor.commit();
                MeasuresSpinner(kpiID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void MeasuresSpinner(int kpiID)
    {
        String URL_MEASURES = "http://192.168.173.1/e-KPI/php/GetMeasures_Spinner.php?kpiID="+kpiID;
        new SpinnerDownloader(EditEvidenceActivity.this, URL_MEASURES, sMeasures).execute();
        sMeasures.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                measuresName = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    //convert bitmap to base64 String
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void save() {

        if(etTitle.getText().toString().equals("") || etDescription.getText().toString().equals("")){
            Toast.makeText(EditEvidenceActivity.this, "Please fill in required fields.", Toast.LENGTH_LONG).show();
        }
        else{
            //Showing the progress dialog
            final ProgressDialog loading = ProgressDialog.show(this, "Saving...", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog
                            loading.dismiss();
                            //Showing toast message of the response
                            if (s.equals("")) {
                                Toast.makeText(EditEvidenceActivity.this, "Changes have been saved.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent (EditEvidenceActivity.this, EvidenceListActivity.class);
                                EditEvidenceActivity.this.startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(EditEvidenceActivity.this, s, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog
                            loading.dismiss();

                            //Showing toast
                            Toast.makeText(EditEvidenceActivity.this, "Please choose an image." , Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Converting Bitmap to String
                    String image = getStringImage(resized);

                    //Getting details
                    String title = etTitle.getText().toString();
                    String description = etDescription.getText().toString();

                    //Creating parameters
                    Map<String, String> params = new Hashtable<String, String>();

                    //Adding parameters

                    params.put(KEY_STAFFID, staffID);
                    params.put(KEY_TITLE, title);
                    params.put(KEY_DESCRIPTION, description);
                    params.put(KEY_CATEGORY, categoryName);
                    params.put(KEY_KPI, kpiName);
                    params.put(KEY_MEASURES, measuresName);
                    params.put(KEY_ROLE_TYPE, roleType);
                    params.put(KEY_IMAGE, image);
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
    }
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences(EditEvidenceActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent (EditEvidenceActivity.this, LoginActivity.class);
        EditEvidenceActivity.this.startActivity(intent);
    }
    private void cancel(){

        Intent intent = new Intent (EditEvidenceActivity.this, EvidenceListActivity.class);
        EditEvidenceActivity.this.startActivity(intent);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            try {
                //Getting bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                resized = bitmap.createScaledBitmap(bitmap, 1080, 1920, true);
                //Setting bitmap to ImageView
                imageView.setImageBitmap(resized);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
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
            loading = ProgressDialog.show(EditEvidenceActivity.this, "Loading...", null,true,true);
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            super.onPostExecute(b);
            loading.dismiss();
            imageView.setImageBitmap(b);

            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            resized = bitmap.createScaledBitmap(bitmap, 1080, 1920, true);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String id = params[0];
            String add = "http://192.168.173.1/e-KPI/php/LoadImage.php?id="+id;
            URL url = null;
            Bitmap imageView = null;
            try {
                url = new URL(add);
                imageView = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return imageView;
        }
    }
}
