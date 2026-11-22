package com.ms.instrumentos.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.ms.instrumentos.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NumeroSerieDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manjerErroresDuplicados(
            NumeroSerieDuplicadoException ex,
            HttpServletRequest request) {
        ErrorResponse erroresResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erroresResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarErrores(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        ErrorResponse reponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reponse);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manjarDatosNoexistente(
            RecursoNoEncontradoException ex,
            HttpServletRequest request) {
        ErrorResponse erroresResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erroresResponse);
    }

    @ExceptionHandler(FechaInvalidaExeption.class)
    public ResponseEntity<ErrorResponse> manjarErroresFechas(
            FechaInvalidaExeption ex,
            HttpServletRequest request) {
        ErrorResponse erroresResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroresResponse);
    }
}
