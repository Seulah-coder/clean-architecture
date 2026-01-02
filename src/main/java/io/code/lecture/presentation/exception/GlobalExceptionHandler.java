package io.code.lecture.presentation.exception;

import io.code.lecture.common.exception.DuplicateEnrollmentException;
import io.code.lecture.common.exception.EnrollmentFullException;
import io.code.lecture.common.exception.LectureNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEnrollmentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEnrollment(DuplicateEnrollmentException e) {
        ErrorResponse error = new ErrorResponse("DUPLICATE_ENROLLMENT", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EnrollmentFullException.class)
    public ResponseEntity<ErrorResponse> handleEnrollmentFull(EnrollmentFullException e) {
        ErrorResponse error = new ErrorResponse("ENROLLMENT_FULL", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(LectureNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLectureNotFound(LectureNotFoundException e) {
        ErrorResponse error = new ErrorResponse("LECTURE_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "서버 오류가 발생했습니다");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
