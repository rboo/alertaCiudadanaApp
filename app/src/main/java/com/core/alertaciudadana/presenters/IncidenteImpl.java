package com.core.alertaciudadana.presenters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.core.alertaciudadana.interfaces.IncidenteInteractor;
import com.core.alertaciudadana.models.incidente.Incidente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class IncidenteImpl implements IncidenteInteractor {

    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final static String TAG = IncidenteImpl.class.getSimpleName().toString();

    public IncidenteImpl(Context context, FirebaseAuth mAuth, DatabaseReference mDatabase) {
        this.context = context;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
    }

    @Override
    public void registrarIncidente(Incidente incidente) {
        ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setMessage("Enviando notificacion");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mDatabase.child("incidentes")
                .child(incidente.getUid())
                .setValue(incidente)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "se guardo correctamente");
                        progressDialog.dismiss();
                        showDialog("Se envio el incidente satisfactoriamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "hubo un error al guardar el incidente " + e.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public List<Incidente> listarIncidentes() {
        return null;
    }

    @Override
    public void notificarIncidente() {

    }

    public void showDialog(String message){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
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
