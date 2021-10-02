package com.core.alertaciudadana.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.core.alertaciudadana.R;
import com.core.alertaciudadana.models.user.Usuarios;
import com.core.alertaciudadana.presenters.UserImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity implements View.OnClickListener {

    private UserImpl user;
    EditText et_nombres, et_apellidos, et_email, et_contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_register);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        user = new UserImpl(this, mAuth, mDatabase);
        Button btn_register = findViewById(R.id.btn_reg_usuario);
        et_nombres = findViewById(R.id.et_nombres);
        et_apellidos = findViewById(R.id.et_apellidos);
        et_email = findViewById(R.id.et_email);
        et_contrasena = findViewById(R.id.et_contrasena);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_reg_usuario:
                String nombres = et_email.getText().toString().trim();
                String apellidos = et_apellidos.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String constrasena = et_contrasena.getText().toString().trim();
                Usuarios dataUsuarios = new Usuarios(
                        apellidos,
                        constrasena,
                        email,
                        "",
                        "",
                        "",
                        nombres,
                        "",
                        "",
                        "",
                        "",
                        ""
                );
                user.createAccount(dataUsuarios);
            break;
        }
    }

    private void validateDataUser(){
        String nombres = et_email.getText().toString().trim();
        String apellidos = et_apellidos.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String constrasena = et_contrasena.getText().toString().trim();
        Usuarios usuarios = new Usuarios(
                apellidos,
                constrasena,
                email,
                "",
                "",
                "",
                nombres,
                "",
                "",
                "",
                "",
                ""
        );
    }
}