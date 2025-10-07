package no.brreg.regnskap.service;

import jakarta.servlet.http.HttpServletRequest;
import no.brreg.regnskap.config.properties.IpProperties;
import no.brreg.regnskap.model.dbo.RestcallLog;
import no.brreg.regnskap.repository.RestcallLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.List;


@Service
public class RestcallLogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestcallLogService.class);

    private static final int MAX_RETURNED_LOG_ROWS = 50;

    @Autowired
    private IpProperties ipProperties;

    @Autowired
    private RestcallLogRepository restcallLogRepository;


    public void logCall(final HttpServletRequest httpServletRequest, final String requestedMethod) throws SQLException, NoSuchAlgorithmException {
        logCall(httpServletRequest, requestedMethod, null);
    }

    public void logCall(final HttpServletRequest httpServletRequest, final String requestedMethod, final String requestedOrgnr) throws SQLException, NoSuchAlgorithmException {
        final boolean LOG_REQUEST_HEADERS = false;
        if (LOG_REQUEST_HEADERS && httpServletRequest!=null) {
            LOGGER.info("===== Logging request for " + RestcallLog.getIPFromRequest(httpServletRequest) + " " + requestedMethod + " " + requestedOrgnr + " =====");
            Enumeration<String> headerNamesEnumeration = httpServletRequest.getHeaderNames();
            while (headerNamesEnumeration!=null && headerNamesEnumeration.hasMoreElements()) {
                String headerName = headerNamesEnumeration.nextElement();
                LOGGER.info(headerName + ": " + httpServletRequest.getHeader(headerName));
            }
            LOGGER.info("===== Logging complete =====");
        }
        restcallLogRepository.persistRestcall(new RestcallLog(ipProperties.getSalt(), httpServletRequest, requestedMethod, requestedOrgnr));
    }

    public List<String> getStatisticsByIp(LocalDate fraDato, LocalDate tilDato) throws SQLException {
        return restcallLogRepository.getStatisticsByIp(fraDato, tilDato, MAX_RETURNED_LOG_ROWS);
    }

    public List<String> getStatisticsByMethod(LocalDate fraDato, LocalDate tilDato) throws SQLException {
        return restcallLogRepository.getStatisticsByMethod(fraDato, tilDato, MAX_RETURNED_LOG_ROWS);
    }

    public List<String> getStatisticsByOrgnr(LocalDate fraDato, LocalDate tilDato) throws SQLException {
        return restcallLogRepository.getStatisticsByOrgnr(fraDato, tilDato, MAX_RETURNED_LOG_ROWS);
    }

}
