package de.gtmextra;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Entry point shared by dedicated servers and clients. */
public final class GTMExtra implements ModInitializer {

    public static final String MOD_ID = "gtm_extra";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("GTM Extra initialized");
    }
}
