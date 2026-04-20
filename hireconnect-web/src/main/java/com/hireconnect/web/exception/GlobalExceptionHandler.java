package com.hireconnect.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex,
                                HttpServletRequest request,
                                Model model) {

        model.addAttribute("title", "Page Not Found");
        model.addAttribute("status", 404);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "common/error";
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public String handleServiceUnavailable(ServiceUnavailableException ex,
                                           HttpServletRequest request,
                                           Model model) {

        model.addAttribute("title", "Service Unavailable");
        model.addAttribute("status", 503);
        model.addAttribute("errorMessage",
                ex.getMessage() != null
                        ? ex.getMessage()
                        : "One of the HireConnect services is currently unavailable.");
        model.addAttribute("path", request.getRequestURI());

        return "common/error";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(UnauthorizedException ex,
                                     HttpServletRequest request,
                                     Model model) {

        model.addAttribute("title", "Access Denied");
        model.addAttribute("status", 403);
        model.addAttribute("errorMessage",
                ex.getMessage() != null
                        ? ex.getMessage()
                        : "You are not authorized to access this page.");
        model.addAttribute("path", request.getRequestURI());

        return "common/access-denied";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequest(BadRequestException ex,
                                   HttpServletRequest request,
                                   Model model) {

        model.addAttribute("title", "Bad Request");
        model.addAttribute("status", 400);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "common/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex,
                                HttpServletRequest request,
                                Model model) {

        model.addAttribute("title", "Unexpected Error");
        model.addAttribute("status", 500);
        model.addAttribute("errorMessage", "Something went wrong while processing your request.");
        model.addAttribute("path", request.getRequestURI());

        return "common/error";
    }
}