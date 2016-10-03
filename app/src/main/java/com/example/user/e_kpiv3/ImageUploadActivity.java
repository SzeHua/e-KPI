package com.example.user.e_kpiv3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public class ImageUploadActivity extends AppCompatActivity {
    public static final String UPLOAD_URL = "http://192.168.173.1/e-KPI/php/UploadEvidence.php";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY = "categoryName";
    public static final String KEY_KPI = "kpiName";
    public static final String KEY_MEASURES = "measuresName";
    public static final String KEY_STAFFID = "staffID";
    public static final String PREF_NAME = "PrefKey";
    public static final int MEDIA_TYPE_IMAGE = 1;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private int PICK_IMAGE_REQUEST = 1;

    private Button bChooseFile;
    private Button bCamera;
    private Button bUpload;
    private Button bLogout;
    private Button bCancel;
    private EditText etTitle;
    private EditText etDescription;
    private ImageView imageView;
    private Spinner sCategory;
    private Spinner sKPI;
    private Spinner sMeasures;
    private int CategoryIndex = 0;
    private int KPIIndex = 0;
    private String categoryName = "";
    private String kpiName = "";
    private String measuresName = "";
    private String staffID = "";

    private Bitmap bitmap;
    private Bitmap resized;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        SharedPreferences preference = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        staffID = preference.getString("staffid", "0");
        bChooseFile = (Button) findViewById(R.id.bChooseFile);
        bCamera = (Button) findViewById(R.id.bCamera);
        bUpload = (Button) findViewById(R.id.bUpload);
        bLogout = (Button) findViewById(R.id.bLogout);
        bCancel = (Button) findViewById(R.id.bCancel);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etDescription = (EditText) findViewById(R.id.etDescription);
        imageView = (ImageView) findViewById(R.id.imageView);
        sCategory = (Spinner) findViewById(R.id.sCategory);
        sKPI = (Spinner) findViewById(R.id.sKPI);
        sMeasures = (Spinner) findViewById(R.id.sMeasures);

        bChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        bCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                confirmCancelEvidence();
            }
        });

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.categories));

        // attaching data adapter to spinner

        //CATEGORY
        sCategory.setAdapter(dataAdapter);
        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = parent.getSelectedItemPosition();
                categoryName = parent.getSelectedItem().toString();
                CategoryIndex = index;
                KPISpinner(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //sKPI.setAdapter(dataAdapter2);
    }


    //convert bitmap to base64 String
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        if (etTitle.getText().toString().equals("") || etDescription.getText().toString().equals("")) {
            Toast.makeText(ImageUploadActivity.this, "Please fill in required fields.", Toast.LENGTH_LONG).show();
        } else {
            //Showing the progress dialog
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog
                            loading.dismiss();
                            //Showing toast message of the response
                            if (s.equals("")) {
                                Toast.makeText(ImageUploadActivity.this, "Successfully uploaded.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ImageUploadActivity.this, EvidenceListActivity.class);
                                ImageUploadActivity.this.startActivity(intent);

                            } else {
                                Toast.makeText(ImageUploadActivity.this, s, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog
                            loading.dismiss();

                            //Showing toast
                            Toast.makeText(ImageUploadActivity.this, "Please choose an image.", Toast.LENGTH_LONG).show();
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
                    params.put(KEY_TITLE, title);
                    params.put(KEY_DESCRIPTION, description);
                    params.put(KEY_CATEGORY, categoryName);
                    params.put(KEY_KPI, kpiName);
                    params.put(KEY_MEASURES, measuresName);
                    params.put(KEY_STAFFID, staffID);
                    params.put(KEY_IMAGE, image);

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


    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(ImageUploadActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(ImageUploadActivity.this, LoginActivity.class);
        ImageUploadActivity.this.startActivity(intent);
    }

    private void confirmCancelEvidence(){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to cancel this?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent (ImageUploadActivity.this, HomepageActivity.class);
                        ImageUploadActivity.this.startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uri filePath = data.getData();
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                resized = bitmap.createScaledBitmap(bitmap, 1080, 1920, true);
                //Setting bitmap to ImageView
                imageView.setImageBitmap(resized);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            // Image captured and saved to fileUri specified in the Intent
            Toast.makeText(this, "Image saved to:\n" +
                    data.getData(), Toast.LENGTH_LONG).show();
            bitmap = (Bitmap) data.getExtras().get("data");
            resized = bitmap.createScaledBitmap(bitmap, 1080, 1920, true);
            imageView.setImageBitmap(resized);
        }
    }

    @Override
    protected void onDestroy() {
            //super.onDestroy();
        }

    public void KPISpinner(int selectedIndex) {
        int index = getKPISpinner(selectedIndex);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(index));
        sKPI.setAdapter(adapter);
        sKPI.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KPIIndex = parent.getSelectedItemPosition();
                kpiName = parent.getSelectedItem().toString();
                int childIndex = parent.getSelectedItemPosition();
                MeasuresSpinner(childIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int getKPISpinner(int selectedIndex) {
        int returnIndex = R.array.kpiRPI;
        switch (selectedIndex) {
            case 0:
                returnIndex = R.array.kpiRPI;
                break;
            case 1:
                returnIndex = R.array.kpiRPS;
                break;
            case 2:
                returnIndex = R.array.kpiA;
                break;
            default:
                returnIndex = R.array.kpiRPI;
                break;
        }
        return returnIndex;
    }

    public void MeasuresSpinner(int selectedIndex) {
        int index = getMeasuresSpinner(selectedIndex);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(index));
        sMeasures.setAdapter(adapter);
        sMeasures.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                measuresName = parent.getSelectedItem().toString();
                //Toast.makeText(getBaseContext(), parent.getSelectedItem().toString(),
                //        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int getMeasuresSpinner(int selectedIndex) {
        int returnIndex = R.array.measures1_1;
        if (CategoryIndex == 0) //reserach....
        {
            switch (selectedIndex) {
                case 0:
                    returnIndex = R.array.measures1_1;
                    break;
                case 1:
                    returnIndex = R.array.measures1_2;
                    break;
                case 2:
                    returnIndex = R.array.measures1_3;
                    break;
                case 3:
                    returnIndex = R.array.measures1_4;
                    break;
                case 4:
                    returnIndex = R.array.measures1_5;
                    break;
                default:
                    returnIndex = R.array.measures1_1;
                    break;
            }
        } else if (CategoryIndex == 1) //recognition..
        {
            switch (selectedIndex) {
                case 0:
                    returnIndex = R.array.measures2_1;
                    break;
                case 1:
                    returnIndex = R.array.measures2_2;
                    break;
                case 2:
                    returnIndex = R.array.measures2_3;
                    break;
                default:
                    returnIndex = R.array.measures2_1;
                    break;
            }
        } else if (CategoryIndex == 2) //award
        {
            switch (selectedIndex) {
                case 0:
                    returnIndex = R.array.measures3_1;
                    break;
                case 1:
                    returnIndex = R.array.measures3_2;
                    break;
                default:
                    returnIndex = R.array.measures3_1;
                    break;
            }
        }
        return returnIndex;
    }

    /** Create a file Uri for saving an image */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "eKPI");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("eKPI", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }
}