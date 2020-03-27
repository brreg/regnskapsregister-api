package no.regnskap.controller

import no.regnskap.configuration.ProfileConditionalValues
import no.regnskap.jena.ExternalUrls
import no.regnskap.jena.JenaType
import no.regnskap.jena.acceptHeaderToJenaType
import no.regnskap.jena.createJenaResponse
import no.regnskap.service.RegnskapService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

import javax.servlet.http.HttpServletRequest

import org.springframework.web.bind.annotation.RequestMethod.GET

private val LOGGER = LoggerFactory.getLogger(StatistikkApiImpl::class.java)

@Controller
open class StatistikkApiImpl(
    private val regnskapService: RegnskapService,
    private val profileConditionalValues: ProfileConditionalValues
): no.regnskap.generated.api.StatistikkApi {
/*
    override fun getStatisticsByIp(httpServletRequest: HttpServletRequest): ResponseEntity<List<String>> {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    override fun getStatisticsByMethod(httpServletRequest: HttpServletRequest): ResponseEntity<List<String>> {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    override fun getStatisticsByOrgnr(httpServletRequest: HttpServletRequest): ResponseEntity<List<String>> {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
*/
}