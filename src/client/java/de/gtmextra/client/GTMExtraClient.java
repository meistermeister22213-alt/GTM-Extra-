package de.gtmextra.client;

import de.gtmextra.client.config.GTMExtraConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/** Client-only initialization for GTM Extra settings and commands. */
public final class GTMExtraClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GTMExtraConfig.load();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("gtmhighlighter")
                        .then(argument("enabled", bool()).executes(context -> {
                            boolean enabled = getBool(context, "enabled");
                            GTMExtraConfig.setItemGlowEnabled(enabled);
                            context.getSource().sendFeedback(Text.translatable(enabled
                                    ? "gtm_extra.command.highlighter.enabled"
                                    : "gtm_extra.command.highlighter.disabled"));
                            return 1;
                        }))));
    }
}
