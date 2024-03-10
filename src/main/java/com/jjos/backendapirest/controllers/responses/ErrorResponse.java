package com.jjos.backendapirest.controllers.responses;

public class ErrorResponse extends ApiResponse<String> {
    public ErrorResponse(String message) {
        this.setSuccess(false);
        this.setMessage(message);
    }
}
