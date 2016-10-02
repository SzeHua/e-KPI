package com.example.user.e_kpiv3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;


public class EvidenceListActivity extends AppCompatActivity {
    private List<Evidence> evidenceList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EvidenceAdapter mAdapter;
    private SharedPreferences preferences;
    public static final String PREF_NAME = "PrefKey";

    private Button bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidence_list);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        preferences = getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        bLogout = (Button) findViewById(R.id.bLogout);

        mAdapter = new EvidenceAdapter(evidenceList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Evidence evidence = evidenceList.get(position);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("evidenceid", evidence.getEvidenceID());
                editor.putString("title", evidence.getTitle());
                editor.putString("description", evidence.getDescription());
                editor.putString("date", evidence.getDate());
                editor.putString("categoryName", evidence.getCategoryname());
                editor.putString("kpiName", evidence.getKpiName());
                editor.putString("measureName", evidence.getMeasureName());
                editor.commit();

                Intent intent = new Intent(EvidenceListActivity.this, ViewEvidenceDetailActivity.class);
                EvidenceListActivity.this.startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ViewEvidenceList viewEvidenceList = new ViewEvidenceList(this);
        viewEvidenceList.execute();

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void prepareMovieData(String row) {
        String[] field = row.split("@");

        Evidence evidence = new Evidence(field[0], field[1], field[2],field[3],field[4],field[5],field[6]);
        evidenceList.add(evidence);

        mAdapter.notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private EvidenceListActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final EvidenceListActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildLayoutPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    public void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences(EvidenceListActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(EvidenceListActivity.this, LoginActivity.class);
        EvidenceListActivity.this.startActivity(intent);
    }

    public class ViewEvidenceList extends AsyncTask<String, Void, String> {
        Context context;

        ViewEvidenceList(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String login_url = "http://192.168.173.1/e-KPI/php/ViewEvidenceList.php";
            SharedPreferences preference = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String staffID = preference.getString("staffid", "0");

            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("staffid","UTF-8")+"="+URLEncoder.encode(staffID,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                //String result="";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.equals("")) {
                Toast.makeText(context, "No evidence uploaded.", Toast.LENGTH_LONG).show();
            } else {
                String[] list = result.split("<br>");
                for(String row : list)
                {
                    prepareMovieData(row);
                }
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }



}
