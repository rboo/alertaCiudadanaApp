package com.core.alertaciudadana.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
    TextView tv_skip_login, tv_register;
    EditText et_email, et_password;
    CheckBox chk_remind;
    SharedPreferences prefs;
    private static final String TAG = login.class.getSimpleName().toString();
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_login);

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        user = new UserImpl(this, mAuth, mDatabase);
        prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
        prefs.edit();
        loadAccess();

        et_email = findViewById(R.id.et_usuario);
        et_password = findViewById(R.id.et_contrasena);
        tv_skip_login = findViewById(R.id.tv_skip_login);
        tv_register = findViewById(R.id.tv_register);
        chk_remind = findViewById(R.id.chk_remind);
        Button btn_login = findViewById(R.id.btn_ingresar);

        btn_login.setOnClickListener(this);
        tv_skip_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        checkPermission();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ingresar:
                if(chk_remind.isChecked()){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("remind", chk_remind.isChecked());
                    editor.commit();
                }
                validateInput();
                break;
            case R.id.tv_skip_login:
                Intent intent = new Intent(login.this, MenuDrawer.class);
                //mAuth.getCurrentUser().getUid();
                startActivity(intent);
                finish();
                break;
            case R.id.tv_register:
                startActivity(new Intent(login.this, register.class));
                //finish();
                break;
        }
    }

    private void validateInput(){
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (email.isEmpty()){
            Toast.makeText(this, "Por favor ingrese un email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()){
            Toast.makeText(this, "Por favor ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        user.login(email,password);
    }

    private void loadAccess(){
        boolean remind = prefs.getBoolean("remind", false);
        if (remind){
            Intent intent = new Intent(this, MenuDrawer.class);
            startActivity(intent);
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionPhone = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE);
        int permissionSms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int permisionNumbers = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionPhone != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

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

}