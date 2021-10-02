package com.core.alertaciudadana.presenters;

import android.content.Context;

import com.core.alertaciudadana.interfaces.IncidenteInteractor;
import com.core.alertaciudadana.models.incidente.Incidente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class IncidenteImpl implements IncidenteInteractor {

    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public IncidenteImpl(Context context, FirebaseAuth mAuth, DatabaseReference mDatabase) {
        this.context = context;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
    }

    @Override
    public void registrarIncidente(Incidente incidente) {
        mDatabase.child("incidentes")
                .child(incidente.getUid())
                .setValue(incidente);
    }

    @Override
    public List<Incidente> listarIncidentes() {
        return null;
    }

    @Override
    public void notificarIncidente() {

    }
}
