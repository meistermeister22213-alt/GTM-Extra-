package de.gtmextra.client.mixin;

import de.gtmextra.client.feature.SneakAnimation;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Applies crouching only to the client-side render state, never to the player entity. */
@Mixin(PlayerEntityRenderer.class)
abstract class PlayerEntityRendererMixin {

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void gtmExtra$applyFlyingSneakPose(PlayerLikeEntity player, PlayerEntityRenderState state,
                                                float tickProgress, CallbackInfo callback) {
        if (SneakAnimation.shouldRenderSneaking(player)) {
            state.sneaking = true;
            state.isInSneakingPose = true;
        }
    }
}
