package no.brreg.regnskap.slack;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.webhook.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SlackPoster {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackPoster.class);

    public static void postMessage(final String token, final String channel, final String message) {
        if (token == null || token.isEmpty() || "disabled".equalsIgnoreCase(token)) {
            LOGGER.warn("Slack disabled or token missing. Tried to send message to channel {}: {}", channel, message);
            return;
        }

        final var payload = Payload
                .builder()
                .attachments(List.of(Attachment.builder().channelName(channel).build()))
                .text(message)
                .build();

        try {
            final var response = Slack.getInstance().send(token, payload);
            if (response.getCode() != 200) {
                LOGGER.error("Unexpected slack response: {}", response.getMessage());
            } else {
                LOGGER.info("Successfully sent slack message to channel {}: {}", channel, message);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
