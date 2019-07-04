package no.regnskap.service

import com.jcraft.jsch.Channel
import com.jcraft.jsch.JSch
import no.regnskap.mapper.deserializeXmlString
import no.regnskap.mapper.mapXmlListForPersistence
import no.regnskap.repository.RegnskapLogRepository
import no.regnskap.repository.RegnskapRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.util.*
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.Session
import no.regnskap.mapper.essentialFieldsIncluded
import no.regnskap.model.RegnskapLog
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

private val LOGGER = LoggerFactory.getLogger(UpdateService::class.java)
private val scheduledTasks = LinkedList<String>()

enum class Task { UPDATE_ACCOUNTING_DATA, NO_TASKS }

@Service
class UpdateService(
    private val regnskapRepository: RegnskapRepository,
    private val regnskapLogRepository: RegnskapLogRepository
) {
/*
    @PostConstruct
    @Scheduled(fixedDelay = 60000)
    fun pollScheduledTasks() {
        if(poll() == Task.UPDATE_ACCOUNTING_DATA) GlobalScope.launch { updateAccountingData() }
    }

    @PostConstruct
    @Scheduled(cron = "0 15 5 * * *") // Check server for new accounting files once a day at 05:15
    fun startSchedule() =
        addTask(Task.UPDATE_ACCOUNTING_DATA)
*/
    fun updateDatabase(file: MultipartFile) {
        val filename = file.originalFilename
        val extension = filename?.substring(filename.lastIndexOf('.') + 1)
        if(filename != null && extension.equals("xml") && regnskapLogRepository.findOneByFilename(filename) == null) {
            file.inputStream.persist(filename)
        } else {
            val msg = when {
                filename == null -> "Unable to update, no filename"
                !extension.equals("xml") -> "Will not update, wrong file extension: $extension"
                else -> "File already persisted: $filename"
            }
            LOGGER.error(msg)
            throw Exception(msg)
        }
    }

    fun InputStream.persist(filename: String) {
        try {
            regnskapLogRepository.save(RegnskapLog(filename))

            regnskapRepository.saveAll(
                bufferedReader()
                    .use(BufferedReader::readText)
                    .deserializeXmlString()
                    .mapXmlListForPersistence()
                    .filter { it.essentialFieldsIncluded() }
            )
            LOGGER.info("Persistence completed for: $filename")
        } catch (ex: Exception) {
            LOGGER.error("Persistence failed for: $filename: ", ex.printStackTrace())
        }
    }

    private fun updateAccountingData() {
        var session: Session? = null
        var channel: Channel? = null
        var channelSftp: ChannelSftp? = null

        try {
            val jsch = JSch()
            val config = Properties()
            val sftpProperties = SftpProperties()

            config["StrictHostKeyChecking"] = "no"

            session = jsch.getSession(sftpProperties.user, sftpProperties.host, sftpProperties.port)
            session.setPassword(sftpProperties.password)
            session.setConfig(config)
            session.connect() // Create SFTP Session

            channel = session.openChannel("sftp")
            channel.connect()

            channelSftp = channel as ChannelSftp
            channelSftp.cd(sftpProperties.directory) // Change Directory on SFTP Server

            @Suppress("UNCHECKED_CAST")
            val fileList = channelSftp.ls(sftpProperties.directory) as? Vector<ChannelSftp.LsEntry> // List of content in source folder

            //Iterate through list of folder content
            fileList?.let{
                for (item in it) {
                    val extension = item.filename.substring(item.filename.lastIndexOf('.') + 1)
                    if (!item.attrs.isDir && extension.equals("xml") && regnskapLogRepository.findOneByFilename(item.filename) == null) { // Do not download if it's a directory, not xml or if the file already has been persisted
                        channelSftp
                            .get(sftpProperties.directory + "/" + item.filename) // get InputStream for file
                            .persist(item.filename) // Persist data from InputStream
                    }
                }
            }

        } catch (ex: Exception) {
            LOGGER.error("Exception when downloading accounting files", ex.printStackTrace())
        } finally {
            channelSftp?.disconnect()
            channel?.disconnect()
            session?.disconnect()
        }

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
                    LOGGER.info("{} already added to queue, duplicate not added", task)
                } else {
                    scheduledTasks.add(task.name)
                    LOGGER.info("{} added to queue", task)
                }
            }
    }

}

private class SftpProperties (
    val host: String = System.getenv("RRAPI_SFTP_SERVER"),
    val port: Int = System.getenv("RRAPI_SFTP_PORT").toInt(),
    val user: String = System.getenv("RRAPI_SFTP_USER"),
    val password: String = System.getenv("RRAPI_SFTP_PASSWORD"),
    val directory: String = System.getenv("RRAPI_SFTP_DIRECTORY")
)