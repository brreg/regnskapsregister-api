package no.regnskap.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import no.regnskap.SftpProperties;
import no.regnskap.SlackProperties;
import no.regnskap.repository.RegnskapLogRepository;
import no.regnskap.slack.Slack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;


@Service
@EnableScheduling
public class UpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);

    @Autowired
    private SftpProperties sftpProperties;

    @Autowired
    private SlackProperties slackProperties;

    @Autowired
    private RegnskapLogRepository regnskapLogRepository;

    private static Object updateLock = new Object();
    private static AtomicBoolean isUpdating = new AtomicBoolean(false);
    private static LocalDateTime previousUpdateTime;


    @PostConstruct
    @Scheduled(fixedDelay = 60000)
    void intermittentScheduleTask() {
        synchronized (updateLock) {
            if (previousUpdateTime==null || previousUpdateTime.plusHours(24+1).isBefore(LocalDateTime.now())) { //If we've just started up, or somehow missed a scheduled time..
                updateAccountingData();
            }
        }
    }

    @PostConstruct
    @Scheduled(cron = "0 15 5 * * *") // Check server for new accounting files once a day at 05:15
    void dailyScheduleTask() {
        synchronized (updateLock) {
            updateAccountingData();
        }
    }

    private void updateAccountingData() {
        if (!isUpdating.getAndSet(true)) {
            try {
                new Thread(() -> {
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
                            ChannelSftp channelSftp = (ChannelSftp)channel;

                            channelSftp.connect();
                            channelSftp.cd(sftpProperties.getDirectory()); // Change Directory on SFTP Server

                            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(sftpProperties.getDirectory()); // List of content in source folder

                            //Iterate through list of folder content
                            for (ChannelSftp.LsEntry item : fileList) {
                                String extension = item.getFilename().substring(item.getFilename().lastIndexOf('.') + 1);
                                if (!item.getAttrs().isDir() && extension.equals("xml") && !regnskapLogRepository.hasLogged(item.getFilename())) { // Do not download if it's a directory, not xml or if the file already has been persisted
                                    regnskapLogRepository.persistRegnskapFile(item.getFilename(),
                                                                          channelSftp.get(sftpProperties.getDirectory() + "/" + item.getFilename()));
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("Exception when downloading accounting files: " + e.getMessage());
                        Slack.postMessage(slackProperties.getToken(), Slack.PRODFEIL_CHANNEL, "Exception when downloading accounting files: " + e.getMessage());
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
                isUpdating.set(false);
            }
        }
    }

}
