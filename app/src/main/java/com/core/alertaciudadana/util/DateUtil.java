package com.core.alertaciudadana.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String FechaCorta(){
        String fecha = "";
        Date date = new Date();
        SimpleDateFormat formatoDate = new SimpleDateFormat("dd/MM/yyyy", new Locale("ES", "PE"));
        fecha = formatoDate.format(date);
        return fecha;
    }

    public static String HoraActual() {
        String hora;
        Date date = new Date();
        SimpleDateFormat formatoEs = new SimpleDateFormat("HH:mm:ss", new Locale("ES", "PE"));
        hora = formatoEs.format(date);
        return hora;
    }
}
