package com.backend.ms_roles.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.backend.ms_roles.dto.ErroresReponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErroresReponse> manejarCredencialesInvalidas(
            CredencialesInvalidasException ex,
            HttpServletRequest request) {

        ErroresReponse reponse = new ErroresReponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reponse);
    }

    @ExceptionHandler(DuplicadosException.class)
    public ResponseEntity<ErroresReponse> manejarDuplicados(
            DuplicadosException ex,
            HttpServletRequest request) {

        ErroresReponse reponse = new ErroresReponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(reponse);
    }

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<ErroresReponse> manejarDatosInexistentes(
            NoEncontradoException ex,
            HttpServletRequest request) {

        ErroresReponse reponse = new ErroresReponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(reponse);
    }

    @ExceptionHandler(ReglasNegocioException.class)
    public ResponseEntity<ErroresReponse> manejarErroresReglasNegocio(
            ReglasNegocioException ex,
            HttpServletRequest request) {

        ErroresReponse reponse = new ErroresReponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroresReponse> manejarErrores(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {


        String mensaje = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Datos invalidos"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        ErroresReponse reponse = new ErroresReponse(
                HttpStatus.BAD_REQUEST.value(),
                mensaje,
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reponse);
    }
}
