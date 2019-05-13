package no.regnskap.repository;

import no.regnskap.generated.model.Regnskap;
import no.regnskap.repository.dbo.RegnskapDbo;
import no.regnskap.repository.dbo.InvoiceOriginalDbo;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Component
public class RegnskapManager {

    public Regnskap createRegnskap(final String invoiceOriginalXml, final Regnskap newRegnskap) throws SQLException {
        RegnskapDbo newRegnskapDbo;
        try (Connection connection = ConnectionManager.getConnection()) {
            try {
                if (newRegnskap==null) {
                    return null;
                }

                InvoiceOriginalDbo newRegnskapOriginalDbo = new InvoiceOriginalDbo(invoiceOriginalXml);
                newRegnskapOriginalDbo.persist(connection);

                newRegnskapDbo = new RegnskapDbo(newRegnskap, newRegnskapOriginalDbo);
                newRegnskapDbo.persist(connection);
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

        return newRegnskapDbo;
    }

    public Regnskap getRegnskapById(final String id) throws SQLException {
        Regnskap invoice = null;
        try (Connection connection = ConnectionManager.getConnection()) {
            try {
                try {
                    invoice = new RegnskapDbo(connection, RegnskapDbo.findInternalId(connection, id));
                } catch (NoSuchElementException | NumberFormatException e) {
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
        return invoice;
    }

    public List<Regnskap> getRegnskaps() throws SQLException {
        List<Regnskap> invoices = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection()) {
            final String sql = "SELECT _id FROM invoice";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    try {
                        invoices.add(new RegnskapDbo(connection, rs.getInt("_id")));
                    } catch (NoSuchElementException e) {
                    }
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
        return invoices;
    }

}
