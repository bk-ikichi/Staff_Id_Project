package org.example.ikichi_staffcard_project.exception;

public class LoginArgumentNotValidException extends RuntimeException {
    public LoginArgumentNotValidException(String message) {
        super(message);
    }
}
