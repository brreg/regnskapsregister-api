package no.regnskap.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RestcallLogRepository {

    @Autowired
    private ConnectionManager connectionManager;

    //List<RestcallLog> findByRequestTimeGt(LocalDateTime datoFra);
    //List<RestcallLog> findByRequestTimeLt(LocalDateTime datoTil);
    //List<RestcallLog> findByRequestTimeBetween(LocalDateTime datoFra, LocalDateTime datoTil);
}
