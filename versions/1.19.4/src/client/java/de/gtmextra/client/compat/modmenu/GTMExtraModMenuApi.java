package de.gtmextra.client.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.gtmextra.client.screen.GTMExtraConfigScreen;

/** Makes the GTM Extra configuration screen available from ModMenu's Configure button. */
public final class GTMExtraModMenuApi implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GTMExtraConfigScreen::new;
    }
}
