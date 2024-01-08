package no.brreg.regnskap.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import no.brreg.regnskap.repository.RegnskapLogRepository;
import no.brreg.regnskap.slack.SlackPoster;
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
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


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

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    void hourlyScheduleTask() {
        updateAccountingData();
    }

    private void updateAccountingData() {
        synchronized (updateLock) {
            new Thread(() -> {
                StopWatch stopWatch = new StopWatch();

                stopWatch.start("File Import");
                fileImport();
                stopWatch.stop();

                Session session = null;
                Channel channel = null;

                String user = sftpProperties.getUser();
                String host = sftpProperties.getHost();
                String port = sftpProperties.getPort();
                try {
                    JSch jsch = new JSch();
                    Properties config = new Properties();

                    config.setProperty("StrictHostKeyChecking", "no");

                    session = jsch.getSession(user, host, Integer.parseInt(port));
                    session.setPassword(sftpProperties.getPassword());
                    session.setConfig(config);
                    session.connect();

                    channel = session.openChannel("sftp");
                    if (channel instanceof ChannelSftp) {
                        ChannelSftp channelSftp = (ChannelSftp) channel;

                        channelSftp.connect();
                        LOGGER.debug("Change directory to: {}", sftpProperties.getDirectory());
                        channelSftp.cd(sftpProperties.getDirectory());

                        Vector<ChannelSftp.LsEntry> filesInDirectory = channelSftp.ls(sftpProperties.getDirectory());

                        for (ChannelSftp.LsEntry item : filesInDirectory) {
                            String filename = item.getFilename();
                            String extension = filename.substring(filename.lastIndexOf('.') + 1);
                            boolean isXmlFile = !item.getAttrs().isDir() && extension.equals("xml");
                            boolean fileIsNewXml = isXmlFile && !regnskapLogRepository.hasLogged(filename);

                            if (fileIsNewXml) {
                                String path = sftpProperties.getDirectory() + "/" + filename;
                                try {
                                    stopWatch.start("Process " + filename);
                                    regnskapLogRepository.persistRegnskapFile(filename, channelSftp.get(path));
                                    stopWatch.stop();
                                } catch (Exception e) {
                                    LOGGER.error("Exception when downloading file {}: {}", path, e.getMessage());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed fetching accounting files from {}@{}:{}", user, host, port);
                    final var error = "Exception when downloading accounting files: " + e.getMessage();
                    LOGGER.error(error, e);
                    SlackPoster.postMessage(slackProperties.getToken(), slackProperties.getChannel(), error);
                } finally {
                    if (channel != null) {
                        channel.disconnect();
                    }
                    if (session != null) {
                        session.disconnect();
                    }
                    LOGGER.info(stopWatch.prettyPrint());
                }
            }).start();
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
