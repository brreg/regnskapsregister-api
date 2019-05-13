package no.regnskap.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.StringReader;
import java.sql.*;
import java.util.NoSuchElementException;


public class InvoiceOriginalDbo {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    private String invoice;


    public InvoiceOriginalDbo(final String invoiceOriginal) {
        this._id = UNINITIALIZED;
        setInvoice(invoiceOriginal);
    }

    public InvoiceOriginalDbo(final Connection connection, final int _id) throws SQLException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT invoice FROM invoiceoriginal WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, _id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = _id;
            setInvoice(rs.getString("invoiceOriginal"));
        }
    }

    public int get_id() {
        return this._id;
    }

    public void setInvoice(String invoiceOriginalXml) {
        this.invoice = invoiceOriginalXml;
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.invoiceoriginal (invoice) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setCharacterStream(1, new StringReader(invoice));

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.invoiceoriginal SET invoice=? WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setCharacterStream(1, new StringReader(invoice));

                stmt.executeUpdate();
            }
        }
    }

}
