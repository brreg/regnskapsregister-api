package no.brreg.regnskap.repository;

import no.brreg.regnskap.model.dbo.RestcallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
public class RestcallLogRepository {

    @Autowired
    private ConnectionManager connectionManager;


    public List<String> getStatisticsByIp(LocalDate fraDato, LocalDate tilDato, final int maxRows) throws SQLException {
        return getStatisticsByColumn("calleriphash", fraDato, tilDato, maxRows);
    }

    public List<String> getStatisticsByMethod(LocalDate fraDato, LocalDate tilDato, final int maxRows) throws SQLException {
        return getStatisticsByColumn("requestedmethod", fraDato, tilDato, maxRows);
    }

    public List<String> getStatisticsByOrgnr(LocalDate fraDato, LocalDate tilDato, final int maxRows) throws SQLException {
        return getStatisticsByColumn("requestedorgnr", fraDato, tilDato, maxRows);
    }

    public List<String> getStatisticsByColumn(final String column, LocalDate fraDato, LocalDate tilDato, final int maxRows) throws SQLException {
        List<String> returnList = new ArrayList();
        try (Connection connection = connectionManager.getConnection()) {
            final String where = (fraDato != null || tilDato != null) ? "WHERE " : "";
            final String fraFilter = (fraDato != null) ? "requestedtime>=? " : "";
            final String and = (fraDato != null && tilDato != null) ? "AND " : "";
            final String tilFilter = (tilDato != null) ? "requestedtime<=? " : "";

            final String sql = "SELECT COUNT(*) AS c, " + column + " FROM rregapi.restcallog " +
                    where + fraFilter + and + tilFilter +
                    "GROUP BY " + column + " " +
                    "ORDER BY c DESC " +
                    "LIMIT ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int i = 1;
                if (fraDato != null) {
                    stmt.setTimestamp(i++, Timestamp.valueOf(fraDato.atStartOfDay()));
                }
                if (tilDato != null) {
                    stmt.setTimestamp(i++, Timestamp.valueOf(tilDato.atStartOfDay()));
                }
                stmt.setInt(i, maxRows);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    final String element = rs.getInt("c") + ";" + rs.getString(column);
                    returnList.add(element);
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
        return returnList;
    }

    public void persistRestcall(final RestcallLog restcallLog) throws SQLException, NoSuchAlgorithmException {
        try (Connection connection = connectionManager.getConnection()) {
            try {
                final String sql = "INSERT INTO rregapi.restcallog (calleriphash,requestedorgnr,requestedmethod,requestedtime) " +
                                   "VALUES (?,?,?,?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, restcallLog.getCallerIp());

                    if (restcallLog.getRequestedOrgnr()==null) {
                        stmt.setNull(2, Types.VARCHAR);
                    } else {
                        stmt.setString(2, restcallLog.getRequestedOrgnr());
                    }

                    if (restcallLog.getRequestedMethod()==null) {
                        stmt.setNull(3, Types.VARCHAR);
                    } else {
                        stmt.setString(3, restcallLog.getRequestedMethod());
                    }

                    stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.executeUpdate();
                }

                connection.commit();
            } catch (Exception e) {
                try {
                    connection.rollback();
                    throw e;
                } catch (SQLException e2) {
                    throw e2;
                }
            }
        }
    }

    public boolean isDatabaseReady() {
        return connectionManager.getDatabaseIsReady();
    }

}
