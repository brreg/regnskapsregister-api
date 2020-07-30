package no.regnskap.service;

import no.regnskap.model.dbo.RestcallLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import no.regnskap.repository.RestcallLogRepository;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


@Service
public class RestcallLogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestcallLogService.class);

    private static final int MAX_RETURNED_LOG_ROWS = 50;

    @Autowired
    private RestcallLogRepository restcallLogRepository;


    public void logCall(final HttpServletRequest httpServletRequest, final String requestedMethod) throws SQLException, NoSuchAlgorithmException {
        logCall(httpServletRequest, requestedMethod, null);
    }

    public void logCall(final HttpServletRequest httpServletRequest, final String requestedMethod, final String requestedOrgnr) throws SQLException, NoSuchAlgorithmException {
        restcallLogRepository.persistRestcall(new RestcallLog(httpServletRequest, requestedMethod, requestedOrgnr));
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
