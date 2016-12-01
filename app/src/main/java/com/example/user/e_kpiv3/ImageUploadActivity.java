package com.example.user.e_kpiv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ImageUploadActivity extends AppCompatActivity {
    public static final String UPLOAD_URL = "http://192.168.173.1/e-KPI/php/UploadEvidence.php";

    public static final String KEY_IMAGE = "image";
    public static final String KEY_DOCUMENT = "document";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY = "categoryName";
    public static final String KEY_KPI = "kpiName";
    public static final String KEY_MEASURES = "measuresName";
    public static final String KEY_STAFFID = "staffID";
    public static final String KEY_ROLE_TYPE = "roleType";
    public static final String PREF_NAME = "PrefKey";
    public static final int MEDIA_TYPE_IMAGE = 1;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_DOCUMENT_REQUEST = 10;

    private Button bChooseFile;
    private Button bCamera;
    private Button bUpload;
    private Button bLogout;
    private Button bCancel;
    private Button bDocument;
    private EditText etTitle;
    private EditText etDescription;
    private ImageView imageView;
    private Spinner sCategory;
    private Spinner sKPI;
    private Spinner sMeasures;
    private String categoryName = "";
    private int categoryID = 0;
    private String kpiName = "";
    private int kpiID = 0;
    private String measuresName = "";
    private String staffID = "";
    private int isLecturer;
    private SharedPreferences preferences;
    private String roleType = "";

    private Bitmap bitmap;
    private Bitmap resized;
    private Uri fileUri;
    private File f;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        staffID = preferences.getString("staffid", "0");
        isLecturer = preferences.getInt("isLecturer", 1);
        roleType = preferences.getString("roleType", roleType);
        bChooseFile = (Button) findViewById(R.id.bChooseFile);
        bCamera = (Button) findViewById(R.id.bCamera);
        bUpload = (Button) findViewById(R.id.bUpload);
        bLogout = (Button) findViewById(R.id.bLogout);
        bCancel = (Button) findViewById(R.id.bCancel);
        bDocument = (Button) findViewById(R.id.bDocument);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etDescription = (EditText) findViewById(R.id.etDescription);
        imageView = (ImageView) findViewById(R.id.imageView);
        sCategory = (Spinner) findViewById(R.id.sCategory);
        sKPI = (Spinner) findViewById(R.id.sKPI);
        sMeasures = (Spinner) findViewById(R.id.sMeasures);
        String URL_CATEGORY = "http://192.168.173.1/e-KPI/php/GetCategory.php?isLecturer="+isLecturer;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("categoryID", 0);
        editor.putInt("kpiID", 0);
        editor.commit();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        100);
                return;

            }
        }
        //enable_button();

        //private void enable_button(){
            bDocument.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    new MaterialFilePicker()
                            .withActivity(ImageUploadActivity.this)
                            .withRequestCode(PICK_DOCUMENT_REQUEST)
                            .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by file name using regexp
                            //.withFilter(Pattern.compile(".*\\.pptx$")) // Filtering files and directories by file name using regexp
                            .withFilterDirectories(false) // Set directories filterable (false by default)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();
                }
            });
        //}

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

        new SpinnerDownloader(ImageUploadActivity.this, URL_CATEGORY, sCategory).execute();
        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                categoryName = parent.getItemAtPosition(position).toString();
                categoryID = position+1;

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("categoryID", categoryID);
                editor.commit();
                KPISpinner(categoryID);
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

    private void uploadImage() {
        if (etTitle.getText().toString().equals("") || etDescription.getText().toString().equals("")) {
            Toast.makeText(ImageUploadActivity.this, "Please fill in required fields.", Toast.LENGTH_LONG).show();
        } else if (resized != null && f == null){
            SetInsertData();
        }else if (resized == null && f != null)
        {
            Thread t =new Thread(new Runnable(){
                @Override
                public void run() {

                    String content_type = getMimeType(f.getPath());

                    String file_path = f.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type", content_type)
                            .addFormDataPart("uploaded_file", file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                            .build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("http://192.168.173.1/e-KPI/php/UploadFile.php")
                            .post(request_body)
                            .build();
                    try {
                        okhttp3.Response response = client.newCall(request).execute();

                        if(!response.isSuccessful()){
                            throw new IOException("Error: "+response);
                        }
                        pd.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            t.start();
            SetInsertData();
        }
        else if (resized == null && f == null){
            Toast.makeText(ImageUploadActivity.this, "Please choose an image.", Toast.LENGTH_LONG).show();
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
                        finish();
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

    @Override
   /* public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults){
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            enable_button();
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
    }*/

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uri filePath = data.getData();
        f = null;
        resized = null;
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
        }else
        if(requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK){
            f = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
            Toast.makeText(ImageUploadActivity.this, f.getPath()+" is selected.", Toast.LENGTH_LONG).show();
        }
    }

    private String getMimeType(String path){
        path = path.replaceAll("\\s","");
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public void KPISpinner(int categoryID) {

        String URL_KPI = "http://192.168.173.1/e-KPI/php/GetKPI_Spinner.php?categoryID="+categoryID;
        new SpinnerDownloader(ImageUploadActivity.this, URL_KPI, sKPI).execute();
        sKPI.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                kpiName = parent.getItemAtPosition(position).toString();
                kpiID = position+1;

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("kpiID", kpiID);
                editor.commit();
                MeasuresSpinner(kpiID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void MeasuresSpinner(int kpiID) {

        String URL_MEASURES = "http://192.168.173.1/e-KPI/php/GetMeasures_Spinner.php?kpiID="+kpiID;
        new SpinnerDownloader(ImageUploadActivity.this, URL_MEASURES, sMeasures).execute();
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

    private void SetInsertData()
    {
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
                            Intent intent = new Intent(ImageUploadActivity.this, ImageUploadActivity.class);
                            ImageUploadActivity.this.startActivity(intent);
                            finish();

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
                })
                //if(requestCode == PICK_DOCUMENT_REQUEST){

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = null;//getStringImage(resized);
                if(resized != null)
                {image = getStringImage(resized);}

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
                if(resized != null ) {
                    params.put(KEY_IMAGE, image);
                }else if(f != null)
                {
                    params.put(KEY_DOCUMENT, f.getPath().substring(f.getPath().lastIndexOf("/")+1));
                }

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