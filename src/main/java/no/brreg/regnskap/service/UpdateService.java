package no.brreg.regnskap.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.annotation.PostConstruct;
import no.brreg.regnskap.repository.RegnskapLogRepository;
import no.brreg.regnskap.slack.Slack;
import no.brreg.regnskap.spring.properties.FileimportProperties;
import no.brreg.regnskap.spring.properties.SftpProperties;
import no.brreg.regnskap.spring.properties.SlackProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Vector;


@Service
@EnableScheduling
@ConditionalOnProperty(value = "regnskap.update.enabled", havingValue = "true")
public class UpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);

    @Autowired
    private SftpProperties sftpProperties;

    @Autowired
    private FileimportProperties fileimportProperties;

    @Autowired
    private SlackProperties slackProperties;

    @Autowired
    private RegnskapLogRepository regnskapLogRepository;

    private static Object updateLock = new Object();
    private static LocalDateTime previousUpdateTime;


    @PostConstruct
    @Scheduled(fixedDelay = 60000)
    void intermittentScheduleTask() {
        updateAccountingData();
    }

    @PostConstruct
    @Scheduled(cron = "0 15 5 * * *") // Check server for new accounting files once a day at 05:15
    void dailyScheduleTask() {
        updateAccountingData();
    }

    private void updateAccountingData() {
        synchronized (updateLock) {
            if (previousUpdateTime!=null && previousUpdateTime.plusHours(24+1).isAfter(LocalDateTime.now())) {
                return; //We have updated recently
            }

            try {
                new Thread(() -> {
                    fileImport();

                    Session session = null;
                    Channel channel = null;

                    try {
                        JSch jsch = new JSch();
                        Properties config = new Properties();

                        config.setProperty("StrictHostKeyChecking", "no");

                        session = jsch.getSession(sftpProperties.getUser(),
                                sftpProperties.getHost(), Integer.valueOf(sftpProperties.getPort()));
                        session.setPassword(sftpProperties.getPassword());
                        session.setConfig(config);
                        session.connect(); // Create SFTP Session

                        channel = session.openChannel("sftp");
                        if (channel instanceof ChannelSftp) {
                            ChannelSftp channelSftp = (ChannelSftp) channel;

                            channelSftp.connect();
                            channelSftp.cd(sftpProperties.getDirectory()); // Change Directory on SFTP Server

                            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(sftpProperties.getDirectory()); // List of content in source folder

                            //Iterate through list of folder content
                            for (ChannelSftp.LsEntry item : fileList) {
                                String extension = item.getFilename().substring(item.getFilename().lastIndexOf('.') + 1);
                                if (!item.getAttrs().isDir() && extension.equals("xml") && !regnskapLogRepository.hasLogged(item.getFilename())) { // Do not download if it's a directory, not xml or if the file already has been persisted
                                    try {
                                        regnskapLogRepository.persistRegnskapFile(item.getFilename(),
                                                channelSftp.get(sftpProperties.getDirectory() + "/" + item.getFilename()));
                                    } catch (Exception e) {
                                        LOGGER.error("Exception when downloading accounting file {}: {}", sftpProperties.getDirectory() + "/" + item.getFilename(), e.getMessage());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed fetching accounting files from {}@{}:{}", sftpProperties.getUser(), sftpProperties.getHost(), sftpProperties.getPort());
                        LOGGER.error("Exception when downloading accounting files: " + e.getMessage(), e);
                        Slack.postMessage(slackProperties.getToken(), slackProperties.getChannel(), "Exception when downloading accounting files: " + e.getMessage());
                    } finally {
                        if (channel != null) {
                            channel.disconnect();
                        }
                        if (session != null) {
                            session.disconnect();
                        }
                    }
                }).start();
            } finally {
                previousUpdateTime = LocalDateTime.now();
            }
        }
    }

    private void fileImport() {
        if (fileimportProperties.getDirectory()==null || fileimportProperties.getDirectory().isEmpty()) {
            return;
        }

        File directory = new File(fileimportProperties.getDirectory());
        if (!directory.isDirectory()) {
            return;
        }

        for (File file : directory.listFiles()) {
            if (!file.isFile()) {
                continue;
            }
            final String filename = file.getName();
            String extension = filename.substring(filename.lastIndexOf('.') + 1);
            try {
                if (extension.equals("xml") && !regnskapLogRepository.hasLogged(filename)) {
                        regnskapLogRepository.persistRegnskapFile(filename, new FileInputStream(file));
                }
            } catch (Exception e) {
                LOGGER.error("Exception when importing local file {}", filename);
            }
        }
    }
}
