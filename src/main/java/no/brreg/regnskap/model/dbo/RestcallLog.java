package no.brreg.regnskap.model.dbo;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;


public class RestcallLog {
    private Integer id;
    private String callerIp;
    private String requestedOrgnr;
    private String requestedMethod;
    private LocalDateTime requestTime;

    public RestcallLog(final String salt, final HttpServletRequest httpServletRequest, final String requestedMethod, final String requestedOrgnr) {
        this(salt, RestcallLog.getIPFromRequest(httpServletRequest), requestedMethod, requestedOrgnr);
    }

    public RestcallLog(final String salt, final String callerIp, final String requestedMethod, final String requestedOrgnr) {
        anonymizeAndSetCallerIp(salt, callerIp);
        this.requestedOrgnr = requestedOrgnr;
        this.requestedMethod = requestedMethod;
        this.requestTime = LocalDateTime.now();
    }

    public static String getIPFromRequest(final HttpServletRequest httpServletRequest) {
        if (httpServletRequest == null) {
            return null;
        }

        String ip = httpServletRequest.getHeader("X-Forwarded-For");
        if (ip==null || ip.isEmpty()) {
            ip = httpServletRequest.getRemoteAddr();
        }

        return ip;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getCallerIp() {
        return callerIp;
    }

    private void setCallerIp(final String callerIp) {
        this.callerIp = callerIp;
    }

    public void anonymizeAndSetCallerIp(final String salt, final String callerIp) {
        try {
            setCallerIp(callerIp==null ? null : Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((salt+callerIp).getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException e) {
            setCallerIp(callerIp);
        }
    }

    public String getRequestedOrgnr() {
        return requestedOrgnr;
    }

    public void setRequestedOrgnr(final String requestedOrgnr) {
        this.requestedOrgnr = requestedOrgnr;
    }

    public String getRequestedMethod() {
        return requestedMethod;
    }

    public void setRequestedMethod(final String requestedMethod) {
        this.requestedMethod = requestedMethod;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(final LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
}