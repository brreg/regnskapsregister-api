package no.regnskap.repository;

import no.regnskap.generated.model.*;
import no.regnskap.mapper.RegnskapFieldsMapper;
import no.regnskap.model.RegnskapFields;
import no.regnskap.model.RegnskapXml;
import no.regnskap.model.RegnskapXmlHead;
import no.regnskap.model.RegnskapXmlInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


@Component
public class RegnskapRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegnskapRepository.class);

    @Autowired
    private ConnectionManager connectionManager;


    public Regnskap getById(final String idString) throws SQLException {
        int id;
        try {
            id = Integer.valueOf(idString);
        } catch (NullPointerException|NumberFormatException e) {
            LOGGER.info("getById, invalid Id: " + idString);
            return null;
        }

        Regnskap regnskap = null;
        try (Connection connection = connectionManager.getConnection()) {
            try {
                String sql = "SELECT orgnr, regnskapstype, regnaar, oppstillingsplan_versjonsnr, valutakode, startdato, " +
                        "avslutningsdato, mottakstype, avviklingsregnskap, feilvaloer, journalnr, mottatt_dato, " +
                        "orgform, mor_i_konsern, regler_smaa, fleksible_poster, fravalg_revisjon, utarbeidet_regnskapsforer, " +
                        "bistand_regnskapsforer, aarsregnskapstype, land_for_land, revisorberetning_ikke_levert, " +
                        "ifrs_selskap, forenklet_ifrs_selskap, ifrs_konsern, forenklet_ifrs_konsern " +
                        "FROM rreg.regnskap " +
                        "WHERE _id=? ";

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, id);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        regnskap = createRegnskap(id,
                                readString(rs, "orgnr"), readString(rs, "regnskapstype"),
                                readInteger(rs, "regnaar"), readString(rs, "oppstillingsplan_versjonsnr"),
                                readString(rs, "valutakode"), readDate(rs, "startdato"),
                                readDate(rs, "avslutningsdato"), readString(rs, "mottakstype"),
                                readBoolean(rs, "avviklingsregnskap"), readBoolean(rs, "feilvaloer"),
                                readString(rs, "journalnr"), readDate(rs, "mottatt_dato"),
                                readString(rs, "orgform"), readBoolean(rs, "mor_i_konsern"),
                                readBoolean(rs, "regler_smaa"), readBoolean(rs, "fleksible_poster"),
                                readBoolean(rs, "fravalg_revisjon"), readBoolean(rs, "utarbeidet_regnskapsforer"),
                                readBoolean(rs, "bistand_regnskapsforer"), readString(rs, "aarsregnskapstype"),
                                readBoolean(rs, "land_for_land"), readBoolean(rs, "revisorberetning_ikke_levert"),
                                readBoolean(rs, "ifrs_selskap"), readBoolean(rs, "forenklet_ifrs_selskap"),
                                readBoolean(rs, "ifrs_konsern"), readBoolean(rs, "forenklet_ifrs_konsern"));
                    }
                }

                populateRegnskapWithFields(connection, regnskap);

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
        return regnskap;
    }

    public List<Regnskap> getByOrgnr(final String orgnr, final Integer år, final String regnskapstypeKode) throws SQLException {
        List<Regnskap> regnskapList = new ArrayList<>();
        if (orgnr != null) {
            try (Connection connection = connectionManager.getConnection()) {
                try {
                    String sql = "SELECT _id, orgnr, regnskapstype, regnaar, oppstillingsplan_versjonsnr, valutakode, startdato, " +
                            "avslutningsdato, mottakstype, avviklingsregnskap, feilvaloer, journalnr, mottatt_dato, " +
                            "orgform, mor_i_konsern, regler_smaa, fleksible_poster, fravalg_revisjon, utarbeidet_regnskapsforer, " +
                            "bistand_regnskapsforer, aarsregnskapstype, land_for_land, revisorberetning_ikke_levert, " +
                            "ifrs_selskap, forenklet_ifrs_selskap, ifrs_konsern, forenklet_ifrs_konsern " +
                            "FROM rreg.regnskap " +
                            "WHERE orgnr=? ";

                    if (år != null) {
                        sql += "AND regnaar=? ";
                    }

                    if (regnskapstypeKode != null) {
                        sql += "AND regnskapstype=? ";
                    }

                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        int i = 1;
                        stmt.setString(i++, orgnr);

                        if (år != null) {
                            stmt.setInt(i++, år);
                        }

                        if (regnskapstypeKode != null) {
                            stmt.setString(i++, regnskapstypeKode);
                        }

                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            Regnskap regnskap = createRegnskap(readInteger(rs, "_id"),
                                    readString(rs, "orgnr"), readString(rs, "regnskapstype"),
                                    readInteger(rs, "regnaar"), readString(rs, "oppstillingsplan_versjonsnr"),
                                    readString(rs, "valutakode"), readDate(rs, "startdato"),
                                    readDate(rs, "avslutningsdato"), readString(rs, "mottakstype"),
                                    readBoolean(rs, "avviklingsregnskap"), readBoolean(rs, "feilvaloer"),
                                    readString(rs, "journalnr"), readDate(rs, "mottatt_dato"),
                                    readString(rs, "orgform"), readBoolean(rs, "mor_i_konsern"),
                                    readBoolean(rs, "regler_smaa"), readBoolean(rs, "fleksible_poster"),
                                    readBoolean(rs, "fravalg_revisjon"), readBoolean(rs, "utarbeidet_regnskapsforer"),
                                    readBoolean(rs, "bistand_regnskapsforer"), readString(rs, "aarsregnskapstype"),
                                    readBoolean(rs, "land_for_land"), readBoolean(rs, "revisorberetning_ikke_levert"),
                                    readBoolean(rs, "ifrs_selskap"), readBoolean(rs, "forenklet_ifrs_selskap"),
                                    readBoolean(rs, "ifrs_konsern"), readBoolean(rs, "forenklet_ifrs_konsern"));

                            regnskapList.add(regnskap);
                        }
                    }

                    populateRegnskapWithFields(connection, regnskapList);

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
        return regnskapList;
    }

    public List<String> getLog() throws SQLException {
        List<String> logList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection()) {
            try {
                String sql = "SELECT filename FROM rreg.regnskaplog ORDER BY filename ASC";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        logList.add(readString(rs, "filename"));
                    }
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
        return logList;
    }

    public void persistRegnskap(final Connection connection, final RegnskapXml regnskapXml) throws SQLException {
        RegnskapXmlHead regnskapXmlHead = regnskapXml.getHead();
        String sql = "INSERT INTO rreg.regnskap " +
                "(orgnr, regnskapstype, regnaar, oppstillingsplan_versjonsnr, valutakode, startdato, "+
                 "avslutningsdato, mottakstype, avviklingsregnskap, feilvaloer, journalnr, mottatt_dato, "+
                 "orgform, mor_i_konsern, regler_smaa, fleksible_poster, fravalg_revisjon, utarbeidet_regnskapsforer, "+
                 "bistand_regnskapsforer, aarsregnskapstype, land_for_land, revisorberetning_ikke_levert, "+
                 "ifrs_selskap, forenklet_ifrs_selskap, ifrs_konsern, forenklet_ifrs_konsern) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Integer regnskapId = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, regnskapXmlHead.getOrgnr());
            stmt.setString(2, regnskapXmlHead.getRegnskapstype());
            stmt.setInt(3, regnskapXmlHead.getRegnaar());
            stmt.setString(4, regnskapXmlHead.getOppstillingsplanVersjonsnr());
            stmt.setString(5, regnskapXmlHead.getValutakode());
            stmt.setDate(6, ConnectionManager.toSqlDate("yyyyMMdd", regnskapXmlHead.getStartdato()));
            stmt.setDate(7, ConnectionManager.toSqlDate("yyyyMMdd", regnskapXmlHead.getAvslutningsdato()));
            stmt.setString(8, regnskapXmlHead.getMottakstype());
            stmt.setBoolean(9, kodeToBoolean(regnskapXmlHead.getAvviklingsregnskap()));
            stmt.setBoolean(10, kodeToBoolean(regnskapXmlHead.getFeilvaloer()));
            stmt.setString(11, regnskapXmlHead.getJournalnr());
            stmt.setDate(12, ConnectionManager.toSqlDate("yyyyMMdd", regnskapXmlHead.getMottattDato()));
            stmt.setString(13, regnskapXmlHead.getOrgform());
            stmt.setBoolean(14, kodeToBoolean(regnskapXmlHead.getMorselskap()));
            stmt.setBoolean(15, kodeToBoolean(regnskapXmlHead.getReglerSmaa()));
            stmt.setBoolean(16, kodeToBoolean(regnskapXmlHead.getFleksiblePoster()));
            stmt.setBoolean(17, kodeToBoolean(regnskapXmlHead.getFravalgRevisjon()));
            stmt.setBoolean(18, kodeToBoolean(regnskapXmlHead.getUtarbeidetRegnskapsforer()));
            stmt.setBoolean(19, kodeToBoolean(regnskapXmlHead.getBistandRegnskapsforer()));
            stmt.setString(20, regnskapXmlHead.getAarsregnskapstype());
            stmt.setBoolean(21, kodeToBoolean(regnskapXmlHead.getLandForLand()));
            stmt.setBoolean(22, kodeToBoolean(regnskapXmlHead.getRevisorberetningIkkeLevert()));
            stmt.setBoolean(23, kodeToBoolean(regnskapXmlHead.getIfrsSelskap()));
            stmt.setBoolean(24, kodeToBoolean(regnskapXmlHead.getForenkletIfrsSelskap()));
            stmt.setBoolean(25, kodeToBoolean(regnskapXmlHead.getIfrsKonsern()));
            stmt.setBoolean(26, kodeToBoolean(regnskapXmlHead.getForenkletIfrsKonsern()));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                regnskapId = rs.getInt(1);
            }
        }

        sql = "INSERT INTO rreg.felt " +
                "(_id_regnskap, kode, sum) " +
                "VALUES (?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (RegnskapXmlInfo post : regnskapXml.getPosts()) {
                stmt.setInt(1, regnskapId);
                stmt.setString(2, post.getFeltkode());
                stmt.setBigDecimal(3, post.getSum());
                stmt.executeUpdate();
            }
        }
    }

    public Integer persistRegnskap(final Regnskap regnskap) throws SQLException {
        try (Connection connection = connectionManager.getConnection()) {
            try {
                Integer regnskapId = persistRegnskap(connection, regnskap);
                connection.commit();
                return regnskapId;
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

    public Integer persistRegnskap(final Connection connection, final Regnskap regnskap) throws SQLException {
        String sql = "INSERT INTO rreg.regnskap " +
                "(orgnr, regnskapstype, regnaar, oppstillingsplan_versjonsnr, valutakode, startdato, "+
                "avslutningsdato, mottakstype, avviklingsregnskap, feilvaloer, journalnr, mottatt_dato, "+
                "orgform, mor_i_konsern, regler_smaa, fleksible_poster, fravalg_revisjon, utarbeidet_regnskapsforer, "+
                "bistand_regnskapsforer, aarsregnskapstype, land_for_land, revisorberetning_ikke_levert, "+
                "ifrs_selskap, forenklet_ifrs_selskap, ifrs_konsern, forenklet_ifrs_konsern) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Integer regnskapId = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, regnskap.getVirksomhet().getOrganisasjonsnummer());
            stmt.setNull(2, Types.VARCHAR);//stmt.setString(2, regnskapXmlHead.getRegnskapstype());
            stmt.setNull(3, Types.INTEGER);//stmt.setInt(3, regnskapXmlHead.getRegnaar());
            stmt.setNull(4, Types.VARCHAR);//stmt.setString(4, regnskapXmlHead.getOppstillingsplanVersjonsnr());
            stmt.setString(5, regnskap.getValuta());
            stmt.setDate(6, Date.valueOf(regnskap.getRegnskapsperiode().getFraDato()));
            stmt.setDate(7, Date.valueOf(regnskap.getRegnskapsperiode().getTilDato()));
            stmt.setNull(8, Types.VARCHAR);//stmt.setString(8, regnskapXmlHead.getMottakstype());
            stmt.setBoolean(9, regnskap.getAvviklingsregnskap());
            stmt.setNull(10, Types.BOOLEAN);//stmt.setBoolean(10, kodeToBoolean(regnskapXmlHead.getFeilvaloer()));
            stmt.setNull(11, Types.VARCHAR);//stmt.setString(11, regnskapXmlHead.getJournalnr());
            stmt.setNull(12, Types.DATE);//stmt.setDate(12, ConnectionManager.toSqlDate("yyyyMMdd", regnskapXmlHead.getMottattDato()));
            stmt.setString(13, regnskap.getVirksomhet().getOrganisasjonsform());
            stmt.setBoolean(14, regnskap.getVirksomhet().getMorselskap());
            stmt.setBoolean(15, regnskap.getRegnkapsprinsipper().getSmaaForetak());
            stmt.setNull(16, Types.BOOLEAN);//stmt.setBoolean(16, kodeToBoolean(regnskapXmlHead.getFleksiblePoster()));
            stmt.setNull(17, Types.BOOLEAN);//stmt.setBoolean(17, kodeToBoolean(regnskapXmlHead.getFravalgRevisjon()));
            stmt.setNull(18, Types.BOOLEAN);//stmt.setBoolean(18, kodeToBoolean(regnskapXmlHead.getUtarbeidetRegnskapsforer()));
            stmt.setNull(19, Types.BOOLEAN);//stmt.setBoolean(19, kodeToBoolean(regnskapXmlHead.getBistandRegnskapsforer()));
            stmt.setString(20, regnskap.getOppstillingsplan().getValue());
            stmt.setNull(21, Types.BOOLEAN);//stmt.setBoolean(21, kodeToBoolean(regnskapXmlHead.getLandForLand()));
            stmt.setBoolean(22, regnskap.getRevisjon().getIkkeRevidertAarsregnskap());
            stmt.setBoolean(23, regnskap.getRegnkapsprinsipper().getRegnskapsregler() == Regnskapsprinsipper.RegnskapsreglerEnum.IFRS);
            stmt.setBoolean(24, regnskap.getRegnkapsprinsipper().getRegnskapsregler() == Regnskapsprinsipper.RegnskapsreglerEnum.FORENKLETANVENDELSEIFRS);
            stmt.setNull(25, Types.BOOLEAN);//stmt.setBoolean(25, kodeToBoolean(regnskapXmlHead.getIfrsKonsern()));
            stmt.setNull(26, Types.BOOLEAN);//stmt.setBoolean(26, kodeToBoolean(regnskapXmlHead.getForenkletIfrsKonsern()));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                regnskapId = rs.getInt(1);
            }
        }

        sql = "INSERT INTO rreg.felt " +
                "(_id_regnskap, kode, sum) " +
                "VALUES (?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            Map<String,BigDecimal> fields = RegnskapFieldsMapper.regnskapFields(regnskap);
            for (Map.Entry<String,BigDecimal> field : fields.entrySet()) {
                stmt.setInt(1, regnskapId);
                stmt.setString(2, field.getKey());
                stmt.setBigDecimal(3, field.getValue());
                stmt.executeUpdate();
            }
        }

        return regnskapId;
    }

    public static Regnskap createRegnskap(final Integer _id, final String orgnr, final String regnskapstype, final Integer regnaar,
          final String oppstillingsplan_versjonsnr, final String valutakode, final LocalDate startdato,
          final LocalDate avslutningsdato, final String mottakstype, final Boolean avviklingsregnskap, final Boolean feilvaloer,
          final String journalnr, final LocalDate mottatt_dato, final String orgform, final Boolean mor_i_konsern,
          final Boolean regler_smaa, final Boolean fleksible_poster, final Boolean fravalg_revisjon,
          final Boolean utarbeidet_regnskapsforer, final Boolean bistand_regnskapsforer, final String aarsregnskapstype,
          final Boolean land_for_land, final Boolean revisorberetning_ikke_levert, final Boolean ifrs_selskap,
          final Boolean forenklet_ifrs_selskap, final Boolean ifrs_konsern, final Boolean forenklet_ifrs_konsern) {
        Regnskapsprinsipper.RegnskapsreglerEnum regnskapsregler = Regnskapsprinsipper.RegnskapsreglerEnum.REGNSKAPSLOVENALMINNELIGREGLER;
        if (ifrs_selskap) {
            regnskapsregler = Regnskapsprinsipper.RegnskapsreglerEnum.IFRS;
        }
        if (forenklet_ifrs_selskap) {
            regnskapsregler = Regnskapsprinsipper.RegnskapsreglerEnum.FORENKLETANVENDELSEIFRS;
        }

        Regnskap regnskap = new Regnskap()
            .id(_id.toString())
            .avviklingsregnskap(avviklingsregnskap)
            .valuta(valutakode)
            .oppstillingsplan(Regnskap.OppstillingsplanEnum.fromValue(aarsregnskapstype.toLowerCase()))
            .regnskapsperiode(new Tidsperiode().fraDato(startdato)
                                               .tilDato(avslutningsdato))
            .revisjon(new Revisjon().ikkeRevidertAarsregnskap(revisorberetning_ikke_levert))
            .regnkapsprinsipper(new Regnskapsprinsipper().smaaForetak(regler_smaa)
                                                         .regnskapsregler(regnskapsregler))
            .virksomhet(new Virksomhet().organisasjonsnummer(orgnr)
                                        .organisasjonsform(orgform)
                                        .morselskap(mor_i_konsern));
        return regnskap;
    }

    private void populateRegnskapWithFields(final Connection connection, final Regnskap regnskap) throws SQLException {
        if (regnskap != null) {
            populateRegnskapWithFields(connection, Collections.singletonList(regnskap));
        }
    }

    private void populateRegnskapWithFields(final Connection connection, final List<Regnskap> regnskapList) throws SQLException {
        final String sql = "SELECT kode, sum FROM rreg.felt WHERE _id_regnskap=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Regnskap regnskap : regnskapList) {
                stmt.setInt(1, Integer.valueOf(regnskap.getId()));
                ResultSet rs = stmt.executeQuery();

                List<RegnskapXmlInfo> feltList = new ArrayList<>();
                while (rs.next()) {
                    RegnskapXmlInfo regnskapXmlInfo = new RegnskapXmlInfo();
                    regnskapXmlInfo.setFeltkode(readString(rs, "kode"));
                    regnskapXmlInfo.setSum(readBigDecimal(rs, "sum"));
                    feltList.add(regnskapXmlInfo);
                }

                RegnskapFields fields = new RegnskapFields();
                RegnskapFieldsMapper.mapFieldsFromXmlData(feltList, fields);
                regnskap.egenkapitalGjeld(fields.getEgenkapitalGjeld());
                regnskap.eiendeler(fields.getEiendeler());
                regnskap.resultatregnskapResultat(fields.getResultatregnskapResultat());
            }
        }
    }

    private String readString(final ResultSet rs, final String column) throws SQLException {
        String result = rs.getString(column);
        if (rs.wasNull()) {
            return null;
        }
        return result;
    }

    private Integer readInteger(final ResultSet rs, final String column) throws SQLException {
        Integer result = rs.getInt(column);
        if (rs.wasNull()) {
            return null;
        }
        return result;
    }

    private Boolean readBoolean(final ResultSet rs, final String column) throws SQLException {
        Boolean result = rs.getBoolean(column);
        if (rs.wasNull()) {
            return null;
        }
        return result;
    }

    private BigDecimal readBigDecimal(final ResultSet rs, final String column) throws SQLException {
        BigDecimal result = rs.getBigDecimal(column);
        if (rs.wasNull()) {
            return null;
        }
        return result;
    }

    private LocalDate readDate(final ResultSet rs, final String column) throws SQLException {
        Date date = rs.getDate(column);
        if (rs.wasNull()) {
            return null;
        }
        return date.toLocalDate();
    }

    private boolean kodeToBoolean(final String kode) {
        return !"N".equalsIgnoreCase(kode);
    }

    private String booleanToKode(final boolean value, final ResultSet rs) throws SQLException {
        return booleanToKode(rs.wasNull() ? null : value);
    }

    private String booleanToKode(final Boolean value) {
        if (value==null) {
            return null;
        }
        return value ? "J" : "N";
    }

}
