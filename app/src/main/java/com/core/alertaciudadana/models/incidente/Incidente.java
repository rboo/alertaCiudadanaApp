package com.core.alertaciudadana.models.incidente;

public class Incidente {
    private String descripcion;
    private String fecha;
    private String hora;
    private String imagen;
    private Double latitud;
    private Double longitud;
    private String titulo;
    private String uid;
    private String usuario;
    private Integer numberPhone;

    public Incidente() {
    }

    public Incidente(String descripcion, String fecha, String hora, String imagen, Double latitud, Double longitud, String titulo, String uid, String usuario, Integer numberPhone) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.imagen = imagen;
        this.latitud = latitud;
        this.longitud = longitud;
        this.titulo = titulo;
        this.uid = uid;
        this.usuario = usuario;
        this.numberPhone = numberPhone;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(Integer numberPhone) {
        this.numberPhone = numberPhone;
    }

    @Override
    public String toString() {
        return "Incidente{" +
                "descripcion='" + descripcion + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", imagen='" + imagen + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", titulo='" + titulo + '\'' +
                ", uid='" + uid + '\'' +
                ", usuario='" + usuario + '\'' +
                ", numberPhone=" + numberPhone +
                '}';
    }
}
