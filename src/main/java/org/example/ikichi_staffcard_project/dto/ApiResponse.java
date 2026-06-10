package org.example.ikichi_staffcard_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * 統一されたAPIレスポンスフォーマット
 * 全てのAPIエンドポイントはこのフォーマットを使用
 * 
 * 例：
 * {
 *   "success": true,
 *   "message": "ログインに成功しました",
 *   "data": { ... },
 *   "timestamp": "2024-05-21T11:40:00"
 * }
 */
public class ApiResponse<T> {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("error")
    private ErrorDetail error;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 成功レスポンスを生成
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        response.data = data;
        response.timestamp = LocalDateTime.now();
        return response;
    }

    /**
     * 成功レスポンスを生成（メッセージなし）
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "処理に成功しました");
    }

    /**
     * エラーレスポンスを生成
     */    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.error = new ErrorDetail(errorCode, message);
        response.timestamp = LocalDateTime.now();
        return response;
    }

    /**
     * エラーレスポンスを生成（詳細情報付き）
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, String details) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.error = new ErrorDetail(errorCode, message, details);
        response.timestamp = LocalDateTime.now();
        return response;
    }

    // ============ Getters & Setters ============

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ErrorDetail getError() {
        return error;
    }

    public void setError(ErrorDetail error) {
        this.error = error;
    }

    /**
     * エラー詳細情報
     */
    public static class ErrorDetail {
        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;

        @JsonProperty("details")
        private String details;

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public ErrorDetail(String code, String message, String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getDetails() {
            return details;
        }
    }
}
