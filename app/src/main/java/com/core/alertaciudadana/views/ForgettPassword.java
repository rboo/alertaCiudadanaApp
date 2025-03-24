package com.core.alertaciudadana.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

    Button btnAccept;
    EditText etEmail;
    FirebaseAuth mAuth;
    private static final String TAG = ForgettPassword.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forgett_password);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.et_correo_lost);
        btnAccept = findViewById(R.id.btn_aceptar);
        etEmail.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_aceptar) {
            String email = etEmail.getText().toString().trim();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                showDialog("Se ha enviado satisfactoriamente un email a su cuenta " + email + ", favor de verificar. En caso de no encontrarlo buscarlo en SPAM");
                            }
                        }
                    });
        }
    }

    public void showDialog(String message){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton("", (dialog, id) -> {
                    // User cancelled the dialog
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
}