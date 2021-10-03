package com.core.alertaciudadana;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.core.alertaciudadana.presenters.UserImpl;
import com.core.alertaciudadana.views.MenuDrawer;
import com.core.alertaciudadana.views.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {

    private UserImpl userimpl;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    SharedPreferences prefs;
    private static final String TAG = SplashActivity.class.getSimpleName().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class));

        /*Firebase Initialization*/
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        /*SharedPreferences Initialization*/
        prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
        prefs.edit();

        userimpl = new UserImpl(this, mAuth, mDatabase);
        //validateAccess();
    }

    public void validateAccess(){
        String userData = prefs.getString("user", null);
        if(!userimpl.isLogged(userData == null ? "" : userData)){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user", null);
            editor.putBoolean("remind", false);
            editor.commit();
            Intent intent = new Intent(this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.d(TAG,"El usuario que estuvo guardado en sharedPreferences ya no existe en firebase, mostrando login");
        }else{
            loadAccess();
            Log.d(TAG,"si el usuario existe, verificamos si esta guardado su usuario en sharedPreferences para saltarnor el login");
        }

    }

    private void loadAccess() {
        boolean remind = prefs.getBoolean("remind", false);
        if (remind) {
            Intent intent = new Intent(this, MenuDrawer.class);
            startActivity(intent);
        }
    }

}