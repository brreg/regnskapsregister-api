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
            val logEntry = RestcallLog(httpServletRequest, requestedMethod, requestedOrgnr)

            LOGGER.info("IP-hash:".plus(logEntry.callerIp)
                    .plus(" Method:").plus(requestedMethod)
                    .plus(if (requestedOrgnr==null) "" else " OrgNr:$requestedOrgnr"))

            //restcallLogRepository.save(logEntry)
        } catch (ex: Exception) {
            LOGGER.error("Restcall log persistence failed")
        }
    }

}
