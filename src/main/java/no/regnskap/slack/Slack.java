package no.regnskap.slack;

import com.hubspot.algebra.Result;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.models.response.SlackError;
import com.hubspot.slack.client.models.response.chat.ChatPostMessageResponse;


public class Slack {

    public static final String PRODFEIL_CHANNEL = "#prodfeil";


    public static ChatPostMessageResponse postMessage(final String token, final String channel, final String message) {
        if (token==null || token.isEmpty()) {
            return null;
        }

        Result<ChatPostMessageResponse, SlackError> postResult = BasicRuntimeConfig.getClient(token).postMessage(
                ChatPostMessageParams.builder()
                    .setText(message)
                    .setChannelId(channel)
                    .build()
            ).join();

        return postResult.unwrapOrElseThrow();
    }

}
