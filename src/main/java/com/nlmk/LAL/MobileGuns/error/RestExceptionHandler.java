package com.nlmk.LAL.MobileGuns.error;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nlmk.LAL.MobileGuns.tools.MyTools;

@RestControllerAdvice
public class RestExceptionHandler {

    // ── Bobine introuvable → 404 : 
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
            ResourceNotFoundException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("error", "Ressource introuvable");
        response.put("message", ex.getMessage());
        response.put("timestamp", MyTools.getNowFormattedTimestamp());

        MyTools.logError("RestExceptionHandler - Not Found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ── Erreur métier → 400 ────────────────────────
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusiness(
            BusinessException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("error", "Erreur métier");
        response.put("message", ex.getMessage());
        response.put("timestamp", MyTools.getNowFormattedTimestamp());

        MyTools.logError("RestExceptionHandler - Business Error", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ── Toutes les autres erreurs → 500 ───────────
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(
            Exception ex) {

        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("trace", ex.toString());
        response.put("timestamp", MyTools.getNowFormattedTimestamp());

        MyTools.logError("RestExceptionHandler - Error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
