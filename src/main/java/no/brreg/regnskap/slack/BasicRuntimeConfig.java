package no.brreg.regnskap.slack;

import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.SlackClientFactory;
import com.hubspot.slack.client.SlackClientRuntimeConfig;


public class BasicRuntimeConfig {

    public BasicRuntimeConfig() {
    }

    public static SlackClient getClient(final String token) {
        return SlackClientFactory.defaultFactory().build(get(token));
    }

    public static SlackClientRuntimeConfig get(final String token) {
        return SlackClientRuntimeConfig.builder().setTokenSupplier(() -> {
            return token;
        }).build();
    }
}
