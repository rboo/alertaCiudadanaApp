package com.core.alertaciudadana.interfaces;

import com.core.alertaciudadana.models.incidente.Incidente;

import java.util.List;

public interface IncidenteInteractor {

    public void registrarIncidente(Incidente incidente);
    public List<Incidente> listarIncidentes();
    public void notificarIncidente();

}
