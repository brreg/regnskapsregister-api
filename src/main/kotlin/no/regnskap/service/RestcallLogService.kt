package no.regnskap.service

import no.regnskap.model.RestcallLog
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import no.regnskap.repository.RestcallLogRepository
import javax.servlet.http.HttpServletRequest


private val LOGGER = LoggerFactory.getLogger(RestcallLogService::class.java)

@Service
class RestcallLogService(
        private val restcallLogRepository: RestcallLogRepository
) {

    fun logCall(httpServletRequest: HttpServletRequest, requestedMethod: String, requestedOrgnr: String?) {
        try {
            LOGGER.info("IP:".plus(httpServletRequest.remoteAddr)
                        .plus(" Method:").plus(requestedMethod)
                        .plus(if (requestedOrgnr==null) "" else " OrgNr:$requestedOrgnr"))

            restcallLogRepository.save(RestcallLog(httpServletRequest, requestedMethod, requestedOrgnr))
        } catch (ex: Exception) {
            LOGGER.error("Restcall log persistence failed")
        }
    }

}
