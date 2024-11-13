package no.brreg.regnskap.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AarsregnskapYearResultSetExtractor implements ResultSetExtractor<List<String>> {
    @Override
    public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
        var years = new ArrayList<String>();
        while (rs.next()) {
            var path = rs.getString("path");
            if (path != null) {
                years.add(rs.getString("regnaar"));
            }
        }
        return years;
    }
}
