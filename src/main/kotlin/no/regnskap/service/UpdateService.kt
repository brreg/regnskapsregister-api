package no.regnskap.service

import no.regnskap.mapper.deserializeXmlString
import no.regnskap.mapper.mapXmlListForPersistence
import no.regnskap.model.Checksum
import no.regnskap.model.RegnskapXmlWrap
import no.regnskap.repository.ChecksumRepository
import no.regnskap.repository.RegnskapRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import java.io.BufferedReader
import java.io.IOException
import java.util.*
import javax.annotation.PostConstruct

private val LOGGER = LoggerFactory.getLogger(UpdateService::class.java)
private val scheduledTasks = LinkedList<String>()
enum class Task { CHECK_FOR_UPDATES, NO_TASKS }

@Service
class UpdateService(
    private val regnskapRepository: RegnskapRepository,
    private val checksumRepository: ChecksumRepository
) {

    @PostConstruct
    @Scheduled(fixedDelay = 60000)
    fun pollScheduledTasks() {
        when(poll()) {
            Task.CHECK_FOR_UPDATES -> update()
            Task.NO_TASKS -> {}
        }
    }

    @PostConstruct
    @Scheduled(cron = "0 15 10 * * *")
    fun startSchedule() {
        addTask(Task.CHECK_FOR_UPDATES)
    }

    @Throws(IOException::class)
    private fun update(): RegnskapXmlWrap? {
        val classLoader = javaClass.classLoader
        val xmlInputStream = classLoader.getResourceAsStream("specification/examples/20190327213825-masse-2.xml")
        val xmlString = xmlInputStream.bufferedReader().use(BufferedReader::readText)
        val checksum = DigestUtils.md5DigestAsHex(xmlString.toByteArray())

        if (checksumRepository.findOneByChecksum(checksum) == null) {
            LOGGER.debug("Persistence of Regnskap, checksum: {}", checksum)

            val deserialized = deserializeXmlString(xmlString)
            val listToPersist = mapXmlListForPersistence(deserialized.list)

            regnskapRepository.saveAll(listToPersist)

            checksumRepository.save(Checksum(checksum))

            return deserialized
        } else {
            LOGGER.debug("Checksum found, {}, skipping persistence", checksum)
        }

        return null
    }

    private fun poll(): Task =
        synchronized(scheduledTasks) {
            val taskName = scheduledTasks.poll()
            if(taskName == null) Task.NO_TASKS
            else Task.valueOf(taskName)
        }

    companion object {
        @JvmStatic
        fun addTask(task: Task) =
            synchronized(scheduledTasks) {
                if (scheduledTasks.contains(task.name)) {
                    LOGGER.debug("{} already added to queue, duplicate not added", task)
                } else {
                    scheduledTasks.add(task.name)
                    LOGGER.debug("{} added to queue", task)
                }
            }
    }

}