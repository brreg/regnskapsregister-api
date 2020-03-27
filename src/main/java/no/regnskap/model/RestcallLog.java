package no.regnskap.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

}