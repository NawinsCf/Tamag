package com.tamago.feedservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.Instant;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = "${server.error.path:${error.path:/error}}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GlobalExceptionHandler.ErrorResponse> error(HttpServletRequest request) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> attrs = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        int status = (attrs.get("status") instanceof Integer) ? (Integer) attrs.get("status") : HttpStatus.INTERNAL_SERVER_ERROR.value();
        String error = attrs.getOrDefault("error", HttpStatus.valueOf(status).getReasonPhrase()).toString();
        String message = attrs.getOrDefault("message", "").toString();
        String path = attrs.getOrDefault("path", request.getRequestURI()).toString();

        GlobalExceptionHandler.ErrorResponse body = new GlobalExceptionHandler.ErrorResponse(
                status,
                error,
                message,
                path,
                Instant.now().toString()
        );

        return ResponseEntity.status(status).body(body);
    }
}
