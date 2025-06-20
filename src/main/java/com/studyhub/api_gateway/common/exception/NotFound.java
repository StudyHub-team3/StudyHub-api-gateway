package com.studyhub.api_gateway.common.exception;

public class NotFound extends ClientError {
    public NotFound(String message) {
        this.errorCode = "Not Found";
        this.errorMessage = "message";
    }
}
