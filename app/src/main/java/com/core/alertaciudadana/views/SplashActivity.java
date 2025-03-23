package com.core.alertaciudadana.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.core.alertaciudadana.presenters.UserImpl;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class SplashActivity extends AppCompatActivity {

    private UserImpl userimpl;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    SharedPreferences prefs;
    private static final String TAG = SplashActivity.class.getSimpleName().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class));
    }


}