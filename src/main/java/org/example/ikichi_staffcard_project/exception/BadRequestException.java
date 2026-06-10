package org.example.ikichi_staffcard_project.exception;

/**
 * カスタム例外: 不正なリクエストの場合
 */
public class BadRequestException extends RuntimeException {
    private final String errorCode;

    public BadRequestException(String message) {
        super(message);
        this.errorCode = "BAD_REQUEST";
    }

    public BadRequestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
