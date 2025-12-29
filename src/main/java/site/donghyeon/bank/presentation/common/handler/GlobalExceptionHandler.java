package site.donghyeon.bank.presentation.common.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.donghyeon.bank.common.exception.BadRequestException;
import site.donghyeon.bank.common.exception.ConflictException;
import site.donghyeon.bank.common.exception.ForbiddenException;
import site.donghyeon.bank.common.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrResponse> handleBadRequest(BadRequestException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrResponse.from(e));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrResponse> handleNotFound(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrResponse.from(e));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrResponse> handleForbidden(ForbiddenException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrResponse.from(e));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrResponse> handleConflict(ConflictException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrResponse.from(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrResponse> handleInternalServerError(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrResponse.internal());
    }
}
