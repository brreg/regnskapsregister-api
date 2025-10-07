package no.brreg.regnskap.model;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class Partner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Partner.class);

    private String name = "";
    private boolean isAuthorized = false;


    private Partner() {
    }

    public static Partner fromRequest(final DataSource dataSource, final HttpServletRequest httpServletRequest) {
        Partner partner = null;

        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (authorizationHeader!=null && authorizationHeader.startsWith("Basic ")) {
            partner = new Partner();
            String nameKey = new String(Base64.getDecoder().decode(authorizationHeader.substring("Basic ".length())), StandardCharsets.UTF_8);
            int separatorPos = nameKey.indexOf(':');
            if (separatorPos>0 && separatorPos<nameKey.length()) {
                partner.name = nameKey.substring(0, separatorPos);
                String key = nameKey.substring(separatorPos+1);
                try {
                    partner.authorize(dataSource, key);
                } catch (SQLException e) {
                    LOGGER.error("Authorize failed: " + e.getMessage());
                }
            }
            if (!partner.isAuthorized()) {
                throw new IllegalArgumentException("Invalid username/password");
            }
        }
        return partner;
    }

    private void authorize(final DataSource dataSource, final String key) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            final String sql = "SELECT COUNT(name) FROM rregapi.partners WHERE name=? AND key=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, this.name);
                stmt.setString(2, key);
                ResultSet rs = stmt.executeQuery();
                this.isAuthorized = rs.next() && rs.getInt(1)>0;
            }
        }
    }

    public String getName() {
        return name;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }
}
