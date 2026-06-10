package org.example.ikichi_staffcard_project.exception;

import org.example.ikichi_staffcard_project.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.logging.Logger;

/**
 * グローバル例外ハンドラー
 * 全Controller で発生した例外を統一的に処理
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    /**
     * リソースが見つからない場合
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warning("ResourceNotFound: " + ex.getMessage());
        
        ApiResponse<?> response = ApiResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);
    }

    /**
     * 不正なリクエストの場合
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(BadRequestException ex) {
        logger.warning("BadRequest: " + ex.getMessage());
        
        ApiResponse<?> response = ApiResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    /**
     * 認証失敗
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentials(BadCredentialsException ex) {
        logger.warning("Authentication failed: " + ex.getMessage());
        
        ApiResponse<?> response = ApiResponse.error(
            "ユーザーIDまたはパスワードが正しくありません",
            "INVALID_CREDENTIALS"
        );
        
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
    }

    /**
     * 予期しないエラー
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex) {
        logger.severe("Unexpected error: " + ex.getMessage());
        ex.printStackTrace();
        
        ApiResponse<?> response = ApiResponse.error(
            "予期しないエラーが発生しました",
            "INTERNAL_SERVER_ERROR",
            ex.getMessage()
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}
