package org.health.medical_service.exceptions;

import org.health.medical_service.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionsHandler {
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ResponseDto> handleExceptions(RuntimeException ex, WebRequest request) {
        ResponseDto errorResponse = new ResponseDto(
                false,
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
