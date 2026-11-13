package com.khadidja.reservationsalles.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/** Transforme les erreurs en réponses JSON : { "message": "..." }. */
@RestControllerAdvice
public class GestionErreurs {

    @ExceptionHandler(ErreurMetier.class)
    public ResponseEntity<Map<String, String>> erreurMetier(ErreurMetier e) {
        return ResponseEntity.status(e.getStatut()).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
