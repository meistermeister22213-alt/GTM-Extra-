package de.gtmextra.client.mixin;

import de.gtmextra.client.feature.ItemGlow;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Shows the Item Glow label through the vanilla nameplate renderer. */
@Mixin(EntityRenderer.class)
abstract class ItemGlowLabelMixin {

    @Inject(method = "hasLabel", at = @At("RETURN"), cancellable = true)
    private void gtmExtra$showSelectedItemLabel(Entity entity, double squaredDistanceToCamera,
                                                CallbackInfoReturnable<Boolean> callback) {
        if (entity instanceof ItemEntity && ItemGlow.shouldShowLabel(entity)) {
            callback.setReturnValue(true);
        }
    }

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void gtmExtra$setSelectedItemLabel(Entity entity, EntityRenderState state, float tickProgress,
                                                CallbackInfo callback) {
        if (entity instanceof ItemEntity itemEntity && ItemGlow.shouldShowLabel(entity)) {
            state.displayName = ItemGlow.getLabel(itemEntity);
        }
    }
}
