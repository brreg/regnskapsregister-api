package no.regnskap.slack;

import com.hubspot.algebra.Result;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.models.response.SlackError;
import com.hubspot.slack.client.models.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Slack {
    private static final Logger LOGGER = LoggerFactory.getLogger(Slack.class);


    public static ChatPostMessageResponse postMessage(final String token, final String channel, final String message) {
        if (token==null || token.isEmpty() || "disabled".equalsIgnoreCase(token)) {
            return null;
        }

        try {
            Result<ChatPostMessageResponse, SlackError> postResult = BasicRuntimeConfig.getClient(token).postMessage(
                    ChatPostMessageParams.builder()
                            .setText(message)
                            .setChannelId(channel)
                            .build()
            ).join();

            return postResult.unwrapOrElseThrow();
        } catch(Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

}
