package no.regnskap.model.persistance;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("regnskap_log")
public class Checksum {
    @Id
    private String _id;

    @Indexed(unique = true)
    private String checksum;

    public Checksum(String checksum) {
        this.checksum = checksum;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
