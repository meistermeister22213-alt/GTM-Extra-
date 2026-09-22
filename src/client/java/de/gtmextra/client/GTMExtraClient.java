package de.gtmextra.client;

import de.gtmextra.client.config.GTMExtraConfig;
import net.fabricmc.api.ClientModInitializer;

/** Client-only initialization point for future GTM Extra features. */
public final class GTMExtraClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GTMExtraConfig.load();
    }
}
