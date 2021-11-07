package com.core.alertaciudadana.util;

public enum MessageResponse {
    EMAILUSED(100, "The email address is already in use by another account.", "La dirección de correo electrónico ya está siendo utilizada por otra cuenta."),
    DNIUSED(200,"","El DNI ingresado ya se encuentra registrado"),
    TELEFONOUSED(300,"","El telefono ingresado ya se encuentra registrado");

    private int code;
    private String messageEnglish;
    private String messageSpanish;

    private MessageResponse(int code, String messageEnglish, String messageSpanish) {
        this.code = code;
        this.messageEnglish = messageEnglish;
        this.messageSpanish = messageSpanish;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessageEnglish() {
        return messageEnglish;
    }

    public void setMessageEnglish(String messageEnglish) {
        this.messageEnglish = messageEnglish;
    }

    public String getMessageSpanish() {
        return messageSpanish;
    }

    public void setMessageSpanish(String messageSpanish) {
        this.messageSpanish = messageSpanish;
    }
}
