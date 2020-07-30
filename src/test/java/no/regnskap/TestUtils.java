package no.regnskap;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import no.regnskap.generated.model.Tidsperiode;

import java.io.FileInputStream;
import java.util.Properties;

import static no.regnskap.TestData.*;

public class TestUtils {

    public static void sftpUploadFile(String host, int port, String filename){
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            Properties config = new Properties();

            config.setProperty("StrictHostKeyChecking", "no");

            session = jsch.getSession(SFTP_USER, host, port);
            session.setPassword(SFTP_PWD);
            session.setConfig(config);
            session.connect(); // Create SFTP Session

            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.put(new FileInputStream("src/test/resources/sftp-files/" + filename), SFTP_DIR + "/" + filename);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            channelSftp.disconnect();
            channel.disconnect();
            session.disconnect();
        }
    }

    public static boolean forYear(final Tidsperiode tidsperiode, final int year) {
        return (tidsperiode!=null &&
                (tidsperiode.getFraDato()==null || tidsperiode.getFraDato().getYear()<=year) &&
                (tidsperiode.getTilDato()==null || tidsperiode.getTilDato().getYear()>=year));
    }

}
