package no.regnskap.repository.dbo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.regnskap.generated.model.Regnskap;

import java.sql.*;
import java.util.NoSuchElementException;


@JsonIgnoreProperties({"id"}) /* Default serialization insists on appending this lowercase id element?!? We do not want it */
public class RegnskapDbo extends Regnskap {
    public static final int UNINITIALIZED = 0;

    @JsonIgnore
    private int _id;

    @JsonIgnore
    private int _invoiceoriginalid;


    public RegnskapDbo() {
        super();
        this._id = UNINITIALIZED;
        _invoiceoriginalid = InvoiceOriginalDbo.UNINITIALIZED;
    }

    public RegnskapDbo(final Regnskap regnskap, final InvoiceOriginalDbo invoiceOriginal) {
        super();
        this._id = UNINITIALIZED;
        set_InvoiceOriginalId(invoiceOriginal == null ? InvoiceOriginalDbo.UNINITIALIZED : invoiceOriginal.get_id());
    }

    public RegnskapDbo(final Connection connection, final int _id) throws SQLException {
        if (_id == UNINITIALIZED) {
            throw new NoSuchElementException();
        }

        final String sql = "SELECT _invoiceoriginalid, customizationid, profileid, id, issuedate, duedate, invoicetypecode, "+
                                  "documentcurrencycode, accountingcost, buyerreference FROM invoice WHERE _id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, _id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException();
            }

            this._id = _id;
            set_InvoiceOriginalId(rs.getInt("_invoiceoriginalid"));
            if (rs.wasNull()) {
                set_InvoiceOriginalId(InvoiceOriginalDbo.UNINITIALIZED);
            }
        }
    }

    public static int findInternalId(final Connection connection, final String id) throws SQLException {
        final String sql = "SELECT _id FROM invoice WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("_id");
            }
        }
        throw new NoSuchElementException();
    }

    public int get_id() {
        return this._id;
    }

    public void set_InvoiceOriginalId(int _invoiceOriginalId) {
        this._invoiceoriginalid = _invoiceOriginalId;
    }

    public void persist(final Connection connection) throws SQLException {
        if (get_id() == UNINITIALIZED) {
            final String sql = "INSERT INTO nsg.invoice (_invoiceoriginalid, " +
                                                        "customizationid, profileid, id, issuedate, duedate, invoicetypecode, " +
                                                        "documentcurrencycode, accountingcost, buyerreference) " +
                                      "VALUES (?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (_invoiceoriginalid == InvoiceOriginalDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, _invoiceoriginalid);
                }

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._id = rs.getInt(1);
                }
            }
        } else {
            final String sql = "UPDATE nsg.invoice SET _invoiceoriginalid=?, " +
                                                      "customizationid=?, profileid=?, id=?, issuedate=?, duedate=?, invoicetypecode=?, " +
                                                      "documentcurrencycode=?, accountingcost=?, buyerreference=?) " +
                                                "WHERE _id=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (_invoiceoriginalid == InvoiceOriginalDbo.UNINITIALIZED) {
                    stmt.setNull(1, Types.INTEGER);
                } else {
                    stmt.setInt(1, _invoiceoriginalid);
                }
                stmt.setInt(11, get_id());

                stmt.executeUpdate();
            }
        }
    }
}
