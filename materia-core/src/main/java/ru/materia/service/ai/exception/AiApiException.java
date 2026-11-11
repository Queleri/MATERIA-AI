package ru.materia.service.ai.exception;

public class AiApiException extends RuntimeException {
    private final int statusCode;

    public AiApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
