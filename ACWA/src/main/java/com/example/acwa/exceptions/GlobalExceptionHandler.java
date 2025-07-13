package com.example.acwa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import com.example.acwa.exceptions.NotFoundException;
import com.example.acwa.exceptions.ConflictException;

@ControllerAdvice  // Active la gestion globale des exceptions
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // Pour gérer toutes les exceptions d'accès refusé
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        // On récupère le premier champ qui pose problème
        FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        // On prépare un message personnalisé
        String message = "Erreur sur le champ '" + fieldError.getField() + "' : " + fieldError.getDefaultMessage();
        return ResponseEntity.badRequest().body(message); // HTTP 400 + message
    }

    // Gestion des cas où une ressource n'est pas trouvée (ex : un ID qui n'existe pas)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        // HTTP 404 + message de l'exception
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Gestion des erreurs de conflit (ex : doublon, tentative de création d’un utilisateur déjà existant)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflict(ConflictException ex) {
        // HTTP 409 + message explicatif
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // Gestion globale des autres exceptions non prévues (catch-all)

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOther(Exception ex) {
        ex.printStackTrace();
        // HTTP 500 + message générique
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
    }
}
