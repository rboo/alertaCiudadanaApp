package com.core.alertaciudadana.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.core.alertaciudadana.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgettPassword extends AppCompatActivity implements View.OnClickListener {

    Button btn_aceptar;
    EditText et_correo;
    FirebaseAuth mAuth;
    private static final String TAG = ForgettPassword.class.getSimpleName().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forgett_password);

        mAuth = FirebaseAuth.getInstance();
        et_correo = findViewById(R.id.et_correo_lost);
        btn_aceptar = findViewById(R.id.btn_aceptar);
        et_correo.setOnClickListener(this);
        btn_aceptar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        /*switch (view.getId()) {
            case R.id.btn_aceptar:
                String correo = et_correo.getText().toString().trim();
                mAuth.sendPasswordResetEmail(correo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    showDialog("Se ha enviado satisfactoriamente un email a su cuenta "+correo+", favor de verificar. En caso de no encontrarlo buscarlo en SPAM");
                                }
                            }
                        });
                break;
        }*/
    }

    public void showDialog(String message){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
}