package com.hireconnect.web.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "common/error";
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public String handleServiceDown(ServiceUnavailableException ex, Model model) {
        model.addAttribute("errorMessage", "Service is currently unavailable. Try again later.");
        return "common/error";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(UnauthorizedException ex, Model model) {
        model.addAttribute("errorMessage", "You are not authorized to access this page.");
        return "common/access-denied";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequest(BadRequestException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "common/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Something went wrong. Please try again.");
        return "common/error";
    }
}