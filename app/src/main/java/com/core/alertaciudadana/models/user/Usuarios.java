package com.core.alertaciudadana.models.user;

public class Usuarios {
    private String apellidos;
    private String clave;
    private String correo;
    private String direccion;
    private String fechanac;
    private String imagen;
    private String nombres;
    private String numerodocumento;
    private String sexo;
    private String telefono;
    private String tipoacceso;
    private String tokengcm;

    public Usuarios() {
    }

    public Usuarios(String apellidos, String clave, String correo, String direccion, String fechanac, String imagen, String nombres, String numerodocumento, String sexo, String telefono, String tipoacceso, String tokengcm) {
        this.apellidos = apellidos;
        this.clave = clave;
        this.correo = correo;
        this.direccion = direccion;
        this.fechanac = fechanac;
        this.imagen = imagen;
        this.nombres = nombres;
        this.numerodocumento = numerodocumento;
        this.sexo = sexo;
        this.telefono = telefono;
        this.tipoacceso = tipoacceso;
        this.tokengcm = tokengcm;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFechanac() {
        return fechanac;
    }

    public void setFechanac(String fechanac) {
        this.fechanac = fechanac;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getNumerodocumento() {
        return numerodocumento;
    }

    public void setNumerodocumento(String numerodocumento) {
        this.numerodocumento = numerodocumento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoacceso() {
        return tipoacceso;
    }

    public void setTipoacceso(String tipoacceso) {
        this.tipoacceso = tipoacceso;
    }

    public String getTokengcm() {
        return tokengcm;
    }

    public void setTokengcm(String tokengcm) {
        this.tokengcm = tokengcm;
    }

    @Override
    public String toString() {
        return "User{" +
                "apellidos='" + apellidos + '\'' +
                ", clave='" + clave + '\'' +
                ", correo='" + correo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fechanac='" + fechanac + '\'' +
                ", imagen='" + imagen + '\'' +
                ", nombres='" + nombres + '\'' +
                ", numerodocumento='" + numerodocumento + '\'' +
                ", sexo='" + sexo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", tipoacceso='" + tipoacceso + '\'' +
                ", tokengcm='" + tokengcm + '\'' +
                '}';
    }
}