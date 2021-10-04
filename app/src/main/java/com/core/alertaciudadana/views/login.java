package com.core.alertaciudadana.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.core.alertaciudadana.R;
import com.core.alertaciudadana.presenters.UserImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class login extends AppCompatActivity implements View.OnClickListener {

    private UserImpl user;
    TextView tv_skip_login, tv_register, tv_recoveryPassword;
    EditText et_email, et_password;
    CheckBox chk_remind;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private static final String TAG = login.class.getSimpleName().toString();
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    FirebaseAuth mAuth;
    LocationManager mloLocationManager;
    private final static int REQUEST_CHECK_SETTINGS = 1;
    private static final int REQUEST_CHECK_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_login);

        /*Firebase Initialization*/
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        /*Application Initialization*/
        user = new UserImpl(this, mAuth, mDatabase);

        /*SharedPreferences Initialization*/
        prefs = this.getSharedPreferences("session",Context.MODE_PRIVATE);
        editor = prefs.edit();

        /*Inputs Components*/
        et_email = findViewById(R.id.et_usuario);
        et_password = findViewById(R.id.et_contrasena);
        tv_recoveryPassword = findViewById(R.id.recoveyPassword);
        //tv_skip_login = findViewById(R.id.tv_skip_login);
        tv_register = findViewById(R.id.tv_register);
        chk_remind = findViewById(R.id.chk_remind);
        Button btn_login = findViewById(R.id.btn_aceptar);

        /*Listeners Components*/
        btn_login.setOnClickListener(this);
        //tv_skip_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_recoveryPassword.setOnClickListener(this);

        checkPermission();

        /*Verify GPS*/
        mloLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        /*if (!mloLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            displayPromptForEnablingGPS(this);
        }*/

        validateAccess();
        loadAccess();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_aceptar:
                /*if (chk_remind.isChecked()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("remind", chk_remind.isChecked());
                    editor.commit();
                }*/
                validateInput();
                break;
            case R.id.recoveyPassword:
                startActivity(new Intent(login.this, ForgettPassword.class));
                break;
            /*case R.id.tv_skip_login:
                Intent intent = new Intent(login.this, MenuDrawer.class);
                //mAuth.getCurrentUser().getUid();
                startActivity(intent);
                finish();
                break;*/
            case R.id.tv_register:
                startActivity(new Intent(login.this, register.class));
                //finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permisoDeCamaraConcedido();
                    //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    displayPromptForEnablingGPS(this);
                } else {
                    //permisoDeCamaraDenegado();
                }
                break;
            // Aquí más casos dependiendo de los permisos
            // case OTRO_CODIGO_DE_PERMISOS...
        }
    }

    private void validateInput() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        user.login(email, password,chk_remind.isChecked());
    }

    private void loadAccess() {
        boolean remind = prefs.getBoolean("remind", false);
        if (remind) {
            Intent intent = new Intent(this, MenuDrawer.class);
            startActivity(intent);
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        //int permissionPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        //int permissionSms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        //int permisionNumbers = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);
        int permissionCamera = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionCamera != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        /*if (permissionPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }*

        /*if (permissionSms != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }

        if (permisionNumbers != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_NUMBERS);
        }*/

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_LOCATION_PERMISSION);
            return false;
        }
        return true;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT > 23) {
            if (checkAndRequestPermissions()) {
                Log.d(TAG, "checkPermission: ");
            }
        }
    }

    public void displayPromptForEnablingGPS(Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = getString(R.string.msg_settings_gps);

        builder.setMessage(message)
                .setPositiveButton("ACEPTAR",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("NO, GRACIAS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public void validateAccess(){
        String userData = prefs.getString("user", "");
        if(!user.isLogged(userData)){
            //editor = prefs.edit();
            editor.clear().apply();
            /*editor.putString("user", "");
            editor.putBoolean("remind", false);
            editor.commit();*/
            /*Intent intent = new Intent(this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            //finish();
        }else{
           // loadAccess();
        }
        /*if (userData != null){
            user.isLogged(mAuth.getCurrentUser().getUid());
        }else{*/
            //SharedPreferences prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
            /*SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user", null);
            editor.commit();
            user.logout();
            Intent intent = new Intent(this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
        //}
        Log.d(TAG,"Not found userData");
    }

}