package de.gtmextra.client.feature;

import de.gtmextra.client.config.GTMExtraConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.PlayerLikeEntity;

/** Decides whether the local player's render state should use the crouching pose while flying. */
public final class SneakAnimation {

    private SneakAnimation() {
    }

    public static boolean shouldRenderSneaking(PlayerLikeEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        return GTMExtraConfig.isSneakAnimationEnabled()
                && client.player == player
                && client.player.getAbilities().creativeMode
                && client.player.getAbilities().flying
                && client.options.sneakKey.isPressed();
    }
}
