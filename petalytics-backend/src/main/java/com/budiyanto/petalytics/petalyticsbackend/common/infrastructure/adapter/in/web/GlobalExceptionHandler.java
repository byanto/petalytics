package com.budiyanto.petalytics.petalyticsbackend.common.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.budiyanto.petalytics.petalyticsbackend.ordering.application.exception.InvalidFileContentException;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.exception.UnsupportedMarketplaceException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Not Implemented (501) for unsupported marketplaces
    @ExceptionHandler(UnsupportedMarketplaceException.class)
    public ProblemDetail handleUnsupportedMarketplaceException(UnsupportedMarketplaceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_IMPLEMENTED, ex.getMessage());
        // problemDetail.setType(URI.create("https://api.petalytics.com/probs/unsupported-marketplace"));
        problemDetail.setTitle("Unsupported Marketplace");
        problemDetail.setProperty("marketplace", ex.getMarketplace());
        problemDetail.setProperty("supportedMarketplaces", ex.getSupportedMarketplaces());
        return problemDetail;
    }

    // Bad Request (400) for invalid file content
    @ExceptionHandler(InvalidFileContentException.class)
    public ProblemDetail handleInvalidFileContentException(InvalidFileContentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid File Content");
        return problemDetail;
    }

    // Unprocessable Entity (422) for domain logic and semantic business rule violations
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Unprocessable Entity");
        return problemDetail;
    }

    // Bad Request (400) for structural issues or missing request parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParameterException(MissingServletRequestParameterException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Bad Request");
        return problemDetail;
    }

    // Internal Server Error (500) for any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred on the server.");
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }
}
