package no.regnskap.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

@Document("restcall_log")
public class RestcallLog {
    @Id
    private ObjectId id;

    @Indexed
    private String callerIp;

    @Indexed
    private String requestedOrgnr;

    @Indexed
    private String requestedMethod;

    @Indexed
    private LocalDateTime requestTime;

    public RestcallLog(final HttpServletRequest httpServletRequest, final String requestedMethod, final String requestedOrgnr) {
        this(httpServletRequest.getRemoteAddr(), requestedMethod, requestedOrgnr);
    }

    public RestcallLog(final String callerIp, final String requestedMethod, final String requestedOrgnr) {
        anonymizeAndSetCallerIp(callerIp);
        this.requestedOrgnr = requestedOrgnr;
        this.requestedMethod = requestedMethod;
        this.requestTime = LocalDateTime.now();
        this.id = new ObjectId(Integer.toHexString(hashCode()));
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCallerIp() {
        return callerIp;
    }

    public void setCallerIp(String callerIp) {
        this.callerIp = callerIp;
    }

    public void anonymizeAndSetCallerIp(String callerIp) {
        if (callerIp!=null && callerIp.startsWith("b:")) {
            this.callerIp = callerIp;
        } else {
            try {
                this.callerIp = "b:" + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest(callerIp.getBytes(StandardCharsets.UTF_8)));
            } catch (NoSuchAlgorithmException e) {
                this.callerIp = callerIp;
            }
        }
    }

    public String getRequestedOrgnr() {
        return requestedOrgnr;
    }

    public void setRequestedOrgnr(String requestedOrgnr) {
        this.requestedOrgnr = requestedOrgnr;
    }

    public String getRequestedMethod() {
        return requestedMethod;
    }

    public void setRequestedMethod(String requestedMethod) {
        this.requestedMethod = requestedMethod;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public int hashCode() {
        int hash = (this.callerIp==null ? 0 : this.callerIp.hashCode());
        hash = 31*hash + (this.requestedOrgnr==null ? 0 : this.requestedOrgnr.hashCode());
        hash = 31*hash + (this.requestedMethod==null ? 0 : this.requestedMethod.hashCode());
        hash = 31*hash + (this.requestTime==null ? 0 : this.requestTime.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RestcallLog)) {
            return false;
        }
        return this.hashCode() == ((RestcallLog)o).hashCode();
    }
}