package de.gtmextra.client.mixin;

import de.gtmextra.client.feature.ItemGlow;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Forces selected visible item drops to render their quantity and nameplate. */
@Mixin(EntityRenderer.class)
abstract class ItemGlowLabelMixin {

    @Inject(method = "hasLabel", at = @At("RETURN"), cancellable = true)
    private void gtmExtra$showSelectedItemLabel(Entity entity, CallbackInfoReturnable<Boolean> callback) {
        if (entity instanceof ItemEntity && ItemGlow.shouldShowLabel(entity)) {
            callback.setReturnValue(true);
        }
    }
}
