package no.regnskap.controller

import no.regnskap.service.RestcallLogService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.time.LocalDate

import javax.servlet.http.HttpServletRequest

private val LOGGER = LoggerFactory.getLogger(StatistikkApiImpl::class.java)

@Controller
open class StatistikkApiImpl(
        private val restcallLogService: RestcallLogService
): no.regnskap.generated.api.StatistikkApi {

    override fun getStatisticsByIp(httpServletRequest: HttpServletRequest, fraDato: LocalDate?, tilDato: LocalDate?): ResponseEntity<List<String>> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    override fun getStatisticsByMethod(httpServletRequest: HttpServletRequest, fraDato: LocalDate?, tilDato: LocalDate?): ResponseEntity<List<String>> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    override fun getStatisticsByOrgnr(httpServletRequest: HttpServletRequest, fraDato: LocalDate?, tilDato: LocalDate?): ResponseEntity<List<String>> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

}