package no.brreg.regnskap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "postgres.rreg")
public class PostgresProperties {
    private String db_url;
    private String dbo_user;
    private String dbo_password;
    private String user;
    private String password;

    public String getDbUrl() {
        return db_url;
    }

    public void setDbUrl(final String db_url) {
        this.db_url = db_url;
    }

    public String getDboUser() {
        return dbo_user;
    }

    public void setDboUser(final String user) {
        this.dbo_user = user;
    }

    public String getDboPassword() {
        return dbo_password;
    }

    public void setDboPassword(final String password) {
        this.dbo_password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
