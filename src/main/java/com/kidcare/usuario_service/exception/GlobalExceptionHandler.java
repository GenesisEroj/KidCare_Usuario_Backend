package com.kidcare.usuario_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para todos los controladores REST.
 *
 * <p>Captura dos tipos de errores y los convierte en respuestas JSON con código 400:
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} — errores de validación de DTOs
 *       (anotaciones {@code @NotBlank}, {@code @Email}, etc.)</li>
 *   <li>{@link RuntimeException} — errores de negocio lanzados desde los servicios</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Convierte errores de validación de campos en un mapa {@code campo → mensaje}.
     *
     * @param ex excepción lanzada por la validación de Bean Validation
     * @return 400 con mapa de errores por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    /**
     * Convierte errores de negocio en una respuesta JSON con clave {@code "error"}.
     *
     * @param ex excepción lanzada por los servicios
     * @return 400 con el mensaje de error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}