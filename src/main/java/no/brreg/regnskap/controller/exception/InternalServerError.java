package no.brreg.regnskap.controller.exception;

public class InternalServerError extends RuntimeException {
    boolean logStackTrace = true;

    public InternalServerError(String message, Throwable cause) {
        this(message, cause, true);
    }

    public InternalServerError(String message, Throwable cause, boolean logStackTrace) {
        super(message, cause);
        this.logStackTrace = logStackTrace;
    }

    public InternalServerError(Throwable cause) {
        super("Internal Server Error", cause);
    }
}
