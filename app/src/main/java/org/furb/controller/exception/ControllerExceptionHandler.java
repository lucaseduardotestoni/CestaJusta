package org.furb.controller.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.furb.services.exeptions.BusinessException;
import org.furb.services.exeptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException ex,
                                                                 HttpServletRequest request) {
        logger.warn("Recurso não encontrado em {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handleBusiness(BusinessException ex,
                                                         HttpServletRequest request) {
        logger.warn("Regra de negócio violada em {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidation(MethodArgumentNotValidException ex,
                                                           HttpServletRequest request) {
        List<CampoError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new CampoError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        logger.warn("Validação falhou em {}: {} campo(s) inválido(s)", request.getRequestURI(), errors.size());
        StandardError body = new StandardError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Um ou mais campos são inválidos.",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleUnreadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        logger.warn("Corpo inválido em {}: {}", request.getRequestURI(), ex.getClass().getSimpleName());
        return build(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou malformado.", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                             HttpServletRequest request) {
        logger.warn("Parâmetro inválido em {}: {}", request.getRequestURI(), ex.getName());
        String message = "Parâmetro '" + ex.getName() + "' com valor inválido.";
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> handleAccessDenied(AccessDeniedException ex,
                                                             HttpServletRequest request) {
        logger.warn("Acesso negado em {}", request.getRequestURI());
        return build(HttpStatus.FORBIDDEN, "Acesso negado: permissão insuficiente.", request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<StandardError> handleMaxUpload(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "A imagem excede o tamanho máximo permitido (5MB).", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleUnexpected(Exception ex, HttpServletRequest request) {
        logger.error("Erro inesperado processando {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor.", request);
    }

    private ResponseEntity<StandardError> build(HttpStatus status, String message, HttpServletRequest request) {
        StandardError body = new StandardError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
