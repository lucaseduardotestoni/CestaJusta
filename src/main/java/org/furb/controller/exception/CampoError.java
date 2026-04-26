package org.furb.controller.exception;

public class CampoError {

    private String field;
    private String message;

    public CampoError() {
    }

    public CampoError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
