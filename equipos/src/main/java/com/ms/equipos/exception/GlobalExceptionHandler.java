package com.ms.equipos.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.ms.equipos.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EquipoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manejarDuplicados(
            EquipoDuplicadoException ex,
            HttpServletRequest request) {
        ErrorResponse erroresResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erroresResponse);
    }

    
    public ResponseEntity<ErrorResponse> manejarErrores(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Datos invalidos"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        ErrorResponse reponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                mensaje,
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reponse);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarDatosNoexistente(
            RecursoNoEncontradoException ex,
            HttpServletRequest request) {
        ErrorResponse erroresResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erroresResponse);
    }

    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        ErrorResponse erroresResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "El cuerpo de la peticion no es valido",
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroresResponse);
    }
}
