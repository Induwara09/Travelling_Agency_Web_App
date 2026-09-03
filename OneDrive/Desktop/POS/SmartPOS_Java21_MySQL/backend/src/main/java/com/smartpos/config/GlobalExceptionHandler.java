package com.smartpos.config;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException e){
        Map<String,String> errors=new LinkedHashMap<>(); e.getBindingResult().getFieldErrors().forEach(x->errors.put(x.getField(),x.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("timestamp", LocalDateTime.now(),"message","Validation failed","errors",errors));
    }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<?> denied(AccessDeniedException e){ return ResponseEntity.status(403).body(Map.of("message","Access denied")); }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> bad(IllegalArgumentException e){ return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<?> generic(Exception e){ return ResponseEntity.status(500).body(Map.of("message",Optional.ofNullable(e.getMessage()).orElse("Internal server error"))); }
}
