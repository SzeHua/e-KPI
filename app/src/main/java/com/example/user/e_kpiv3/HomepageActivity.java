package com.example.user.e_kpiv3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
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
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent (HomepageActivity.this, LoginActivity.class);
        HomepageActivity.this.startActivity(intent);
    }
}
