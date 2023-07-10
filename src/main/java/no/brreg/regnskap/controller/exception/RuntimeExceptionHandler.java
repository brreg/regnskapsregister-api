package no.brreg.regnskap.controller.exception;

import no.brreg.regnskap.generated.model.ServerErrorRespons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.status;
import static java.time.Instant.now;

@ControllerAdvice
public class RuntimeExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExceptionHandler.class);
    private static final HttpStatus HTTP_STATUS = INTERNAL_SERVER_ERROR;
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public static ResponseEntity<Object> handleException(RuntimeException ex, HttpServletRequest webRequest) {
        return handleException(new InternalServerError(ex), webRequest);
    }

    @ExceptionHandler(InternalServerError.class)
    @ResponseBody
    public static ResponseEntity<Object> handleException(InternalServerError ex, HttpServletRequest webRequest) {
        String trace = generateUUID();
        var errorResponseBody = new ServerErrorRespons()
                .timestamp(getTimestamp())
                .status(String.valueOf(INTERNAL_SERVER_ERROR.value()))
                .error(INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .path(webRequest.getRequestURI())
                .trace(trace);

        if (ex.logStackTrace) {
            LOGGER.error(ex.getMessage() + ", Trace: {}", trace, ex);
        } else {
            LOGGER.error(ex.getMessage() + ", Trace: {}. Error: {}", trace, ex.getCause().getMessage());
        }

        return status(INTERNAL_SERVER_ERROR)
                .body(errorResponseBody);
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private static String getTimestamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .withZone(UTC)
                .format(now());
    }
}
